/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mattring.nifi.scripting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.lifecycle.OnUnscheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

@Tags({"scripting"})
@CapabilityDescription("Run a script as a NiFi processor. Currently supports Groovy.")
@SeeAlso({})
@ReadsAttributes({
    @ReadsAttribute(attribute = "various", description = "depends on the script")})
@WritesAttributes({
    @WritesAttribute(attribute = "various", description = "depends on the script")})
public class ScriptedProcessor extends AbstractProcessor {

    public static final PropertyDescriptor SCRIPT_URI = new PropertyDescriptor.Builder().name("Script URI")
            .description("Location of the script to be run")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final Relationship OUTPUT_A = new Relationship.Builder()
            .name("Output A")
            .description("Depends on the script, but usually analogous to success.")
            .build();

    public static final Relationship OUTPUT_B = new Relationship.Builder()
            .name("Output B")
            .description("Depends on the script, but usually analogous to failure.")
            .build();

    private List<PropertyDescriptor> descriptors;
    private Set<Relationship> relationships;
    private final BlockingQueue<ScriptTriggerable> scriptRunnerPool;

    public ScriptedProcessor() {
        this.scriptRunnerPool = new LinkedBlockingQueue<>();
    }

    @Override
    protected void init(final ProcessorInitializationContext context) {

        final List<PropertyDescriptor> descriptors = new ArrayList<>();
        descriptors.add(SCRIPT_URI);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<>();
        relationships.add(OUTPUT_A);
        relationships.add(OUTPUT_B);
        this.relationships = Collections.unmodifiableSet(relationships);

    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void run(final ProcessContext context) {

        final String scriptUriStr = context.getProperty(SCRIPT_URI).getValue();

        if (scriptUriStr.toLowerCase().endsWith(".groovy")) {

            final int numScriptTriggerables = context.getMaxConcurrentTasks();
            // NOTE: 
            // It is possible that the script could change during this loop,
            // causing inconsistency within the pool. 
            // Could be detected, using MD5 or other means.
            for (int i = 0; i < numScriptTriggerables; i++) {
                ScriptTriggerable st = new GroovyScriptTriggerable();
                st.init(scriptUriStr);
                scriptRunnerPool.add(st);
            }

        } else {

            throw new IllegalArgumentException(
                    "Only Groovy scripts are currently supported. Tried: "
                    + scriptUriStr);
        }
    }

    @OnUnscheduled
    public void pause(final ProcessContext context) {
        scriptRunnerPool.clear();
    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {

        ScriptTriggerable st = null;

        try {

            st = borrowScriptTriggerable();
            final Map<String, FlowFile> results = st.onTrigger(context, session);
            if (!(results == null || results.isEmpty())) {
                FlowFile flowFile = results.get("A");
                if (flowFile != null) {
                    session.transfer(flowFile, OUTPUT_A);
                }
                flowFile = results.get("B");
                if (flowFile != null) {
                    session.transfer(flowFile, OUTPUT_B);
                }
            }

        } catch (ProcessException pex) {
            
            throw pex;
            
        } catch (RuntimeException rex) {

            throw rex;

        } catch (Exception ex) {

            throw new RuntimeException(ex);

        } finally {

            if (st != null) {
                returnScriptTriggerable(st);
            }
        }

    }

    private ScriptTriggerable borrowScriptTriggerable() {
        final ScriptTriggerable st = scriptRunnerPool.poll();
        if (st == null) {
            throw new IllegalStateException(
                    "The scriptRunnerPool was unexpectedly empty. Please restart the Scripting Processor.");
        }
        return st;
    }

    private void returnScriptTriggerable(ScriptTriggerable st) {
        scriptRunnerPool.add(st);
    }

}
