# scripting-for-nifi
A scripting processor for Apache [NiFi](http://nifi.apache.org/). Currently supports [Groovy](http://www.groovy-lang.org/).

Version 0.5.0

ABOUT:
* Use arbitary scripts as NiFi Processors!
* Tested on Nifi v0.3.0
* Java 8 compatible. Not sure about Java 6 or 7.

USAGE:
* Build the nifi-scripting-bundle parent project with Maven.
* Grab the .nar file from nifi-scripting-nar/target and drop it into %NIFI_HOME%/lib.
* Restart NiFi.
* Now you can use the ScriptedProcessor.
* The ScriptedProcessor requires a URI-formatted attribute for the script location, like file:///C:/Scripts/Hello.groovy
* The ScriptedProcessor supports 2 outputs: A and B
* See the Hello.groovy script included in the GroovyScriptExamples project

SCRIPT TIPS:
* The script filename must end in .groovy
* The script must declare the following function:
```groovy
def onTrigger(ProcessContext context, ProcessSession session) throws Exception 
```
* As you can see, your script still has to bang against the NiFi Processor API.
* Sensible data provenance is your script's responsibility!
* The script's onTrigger() function must return a Map<String, FlowFile>, which maps flowfiles to the ScriptedProcessor's outputs, A and B.
* The final non-blank line must be 'this'
* I have not yet investigated the use of 3rd party libraries in a script.
* Again, please see the included Hello.groovy script for guidance

LICENSE:
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
