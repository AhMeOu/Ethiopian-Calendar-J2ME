import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.*;

/**
 * @author Ahmed
 */
public class ECalendar extends MIDlet {
    Ccanvas cal;
    DateConverter conv;
    public ECalendar(){
        cal=new Ccanvas(this);
        conv=new DateConverter(this);
    }

    public void startApp() {
        show(cal);
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }

    public  void show(Displayable disp) {
        Display.getDisplay(this).setCurrent(disp);
    }
}
