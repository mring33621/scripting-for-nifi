/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mattring.js.tryout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Matthew
 */
public class TryoutMain {
    
    static String getFileExtension(String path) {
        String[] parts = path.split("\\.");
        return parts[parts.length - 1];
    }

    public static void main(String[] args) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();

            List<ScriptEngineFactory> factories = manager.getEngineFactories();

            for (ScriptEngineFactory factory : factories) {

                System.out.println("ScriptEngineFactory Info");

                String engName = factory.getEngineName();
                String engVersion = factory.getEngineVersion();
                String langName = factory.getLanguageName();
                String langVersion = factory.getLanguageVersion();

                System.out.printf("\tScript Engine: %s (%s)%n", engName, engVersion);

                List<String> engNames = factory.getNames();
                for (String name : engNames) {
                    System.out.printf("\tEngine Alias: %s%n", name);
                }

                System.out.printf("\tLanguage: %s (%s)%n", langName, langVersion);

            }
            
            String[] scripts = new String[] { 
                "./src/main/groovy/DoSomething.groovy", 
                "./src/main/groovy/DoSomething.js"
            };
            
            for (String script : scripts) {
                String extension = getFileExtension(script);
                ScriptEngine engine = manager.getEngineByName(extension);
                engine.eval(new String(Files.readAllBytes(Paths.get(script))));
                Invocable invocable = (Invocable) engine;
                DoSomething ds = invocable.getInterface(DoSomething.class);
                Map<String, Thing3> out = ds.doIt(new Thing1(), new Thing2());
                System.out.println(out.get("A").getItem());
                System.out.println(out.get("B").getItem());
            }
        } catch (IOException ex) {
            Logger.getLogger(TryoutMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            Logger.getLogger(TryoutMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TryoutMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
