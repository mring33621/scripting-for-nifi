/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function doIt(thing1, thing2) {
    
    var retMap = {};
    var Thing3 = Java.type("com.mattring.js.tryout.Thing3");
    var t31 = new Thing3();
    t31.setItem(thing1.getItem() + " JavaScript");
    var t32 = new Thing3();
    t32.setItem(thing2.getItem());
    retMap["A"] = t31;
    retMap["B"] = t32;
    return retMap;
}
