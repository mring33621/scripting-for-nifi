function onTrigger(context, session) {
    
    var incomingFlowFile = session.get();
    if (incomingFlowFile === null) {
        return;
    }
    
    var copy = session.clone(incomingFlowFile);
    copy = session.putAttribute(copy, "script.attrib", "copy");
    var callBack = function(out) { out.write("\nHello, JavaScript!".getBytes()) };
    copy = session.append(copy, callBack);
    session.getProvenanceReporter().modifyContent(copy, "Appended 'Hello, JavaScript!'");
    
    incomingFlowFile = session.putAttribute(incomingFlowFile, "script.attrib", "original");
    
    return {"A":incomingFlowFile, "B":copy};
}