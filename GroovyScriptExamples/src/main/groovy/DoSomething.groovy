
import com.mattring.js.tryout.Thing1
import com.mattring.js.tryout.Thing2
import com.mattring.js.tryout.Thing3

def doIt(Thing1 thing1, Thing2 thing2) {
    def t31 = new Thing3();
    t31.setItem(thing1.getItem() + ' Groovy');
    def t32 = new Thing3();
    t32.setItem(thing2.getItem());
    def retMap = ['A':t31, 'B':t32];
}