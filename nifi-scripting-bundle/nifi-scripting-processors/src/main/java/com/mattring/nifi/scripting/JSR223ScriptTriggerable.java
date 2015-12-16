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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;

/**
 *
 * @author Matthew
 */
public class JSR223ScriptTriggerable implements ScriptTriggerable {
    
    static String getFileExtension(String path) {
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }
    
    private Triggerable triggerable; 

    @Override
    public void init(String scriptFileUri) {
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            String scriptFileExtension = getFileExtension(scriptFileUri);
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(scriptFileExtension);
            InputStreamReader scriptReader = new InputStreamReader(new URI(scriptFileUri).toURL().openStream());
            scriptEngine.eval(scriptReader);
            Invocable invocable = (Invocable) scriptEngine;
            this.triggerable = invocable.getInterface(Triggerable.class);
        } catch (IOException | URISyntaxException | ScriptException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Map<String, FlowFile> onTrigger(ProcessContext context, ProcessSession session) throws Exception {
        final Map<String, FlowFile> result = triggerable.onTrigger(context, session);
        return result;
    }
    
}
