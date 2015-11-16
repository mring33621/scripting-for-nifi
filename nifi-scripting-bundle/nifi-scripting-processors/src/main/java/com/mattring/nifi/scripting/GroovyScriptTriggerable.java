
package com.mattring.nifi.scripting;

import groovy.lang.GroovyShell;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;

/**
 *
 * @author Matthew
 */
public class GroovyScriptTriggerable implements ScriptTriggerable {
    
    private final GroovyShell groovyShell;
    
    private Object scriptObject;
    private Method onTriggerMethod;

    public GroovyScriptTriggerable() {
        this.groovyShell = new GroovyShell();
    }

    @Override
    public void init(String scriptFileUriStr) {
        try {
            final URI scriptFileUri = new URI(scriptFileUriStr);
            scriptObject = groovyShell.evaluate(scriptFileUri);
            onTriggerMethod = scriptObject.getClass().getDeclaredMethod("onTrigger", ProcessContext.class, ProcessSession.class);
        } catch(Exception ex) {
            throw new RuntimeException("Problem initializing script: " + scriptFileUriStr, ex);
        }
    }

    @Override
    public Map<String, FlowFile> onTrigger(ProcessContext context, ProcessSession session) throws Exception {
        return (Map<String, FlowFile>) onTriggerMethod.invoke(scriptObject, context, session);
    }
    
}
