# scripting-for-nifi
A scripting processor for Apache [NiFi](http://nifi.apache.org/). Currently supports [Groovy](http://www.groovy-lang.org/) and [JavaScript](http://www.oracle.com/technetwork/articles/java/jf14-nashorn-2126515.html).

Version 0.6.1

ABOUT:
* Use arbitrary scripts as NiFi Processors!
* Tested on NiFi v0.4.1
* Java 8 compatible. Not sure about Java 6 or 7.

USAGE:
* Build the nifi-scripting-bundle parent project with Maven. Make sure you clean all the target dirs first!
* Grab the .nar file from nifi-scripting-nar/target and drop it into %NIFI_HOME%/lib.
* Restart NiFi.
* Now you can use the ScriptedProcessor.
* The ScriptedProcessor requires a URI-formatted attribute for the script location, like file:///C:/Scripts/Hello.groovy
* The ScriptedProcessor supports 2 outputs: A and B
* If you modify the script, you must stop/start the ScriptedProcessor, in order to pick up the change(s).
* The ScriptedProcessor caches N copies of the compiled script object, where N = maxConcurrentTasks
* See the Hello.groovy or Hello.js scripts included in the GroovyScriptExamples project

SCRIPT TIPS:
* The script filename must end in .groovy or .js
* The script must declare the following function:
```groovy
// groovy
def onTrigger(ProcessContext context, ProcessSession session) throws Exception 
```
```javascript
// JavaScript
function onTrigger(context, session) 
```
* As you can see, your script still has to bang against the NiFi Processor API.
* Sensible data provenance is your script's responsibility!
* The script's onTrigger() function must return a Map<String, FlowFile>, which maps FlowFiles to the ScriptedProcessor's outputs, A and B.
* I have not yet investigated the use of 3rd party libraries in a script.
* Again, please see the included Hello.groovy or Hello.js scripts for guidance.

LICENSE:
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
