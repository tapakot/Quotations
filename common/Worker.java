package common;

import buffer.QuotationBuffer;
import com.sun.awt.AWTUtilities;
import common.Quotation;
import testing.*;
import ui.MainFrame;
import analysis.Analyser;
import ui.UiThread;
import static common.ForexConstants.*;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Worker manages work of some other classes.
 * One of the main classes.
 * Manages Buffer, User Interface.
 */
public class Worker {
    /** buffer to manage */
    volatile QuotationBuffer buffer;
    /** main frame of User Interface */
    public static MainFrame mainFrame;

    public Worker(){}

    /** manages all work.
     * main method. starts Buffer, UI, doing tests.
     * manages UI, testers.
     */
    public void work(){
        Settings settings = new Settings();
        settings.setFromFile(propFileName, Settings.properties);
        settings.setFromFile(defPropFileName, Settings.defProperties);
        ForexConstants.applySettings();
        Settings.setAdjustValues();
        //test
        for(AdjustValue aValue : settings.adjustValues){
            System.out.println(aValue.name+" = "+aValue.defValue);
        }

        //start buffer. wait for it
        buffer = new QuotationBuffer();
        buffer.startThread(this);
        while (!buffer.isReady){}   //waits for buffer initialisation

        //start UI
        UiThread ui = new UiThread(buffer);
        ui.start();
        do{}while (!ui.frameIsReady);
        mainFrame = ui.getFrame();


        //start analyser. FOR TESTS ONLY
        Analyser analyser = new Analyser();
        analyser.setQuotationBuffer(buffer);
        analyser.analyse();

        //WARNING no update from this analyser
        ui.drawExtremes(analyser.getBuffer().maximums, analyser.getBuffer().minimums);
        ui.getFrame().drawInnerLine(analyser.getBuffer().innerTrendLine);
        ui.getFrame().drawTDSequences(analyser.getBuffer().tdSequences);

        //history test
        /*HistoryTester tester = new HistoryTester(buffer);
        System.out.println("==================================balance after history test: " + tester.test());
        */

        //testing ui part
        /*ArrayList<Position> p = new ArrayList<>();
        p.add(new Position(1.14, DOWN_DIRECTION, 10));
        p.add(new Position(1.13943, DOWN_DIRECTION, 100));
        ui.setPositions(p);*/


        //real-time test
        //RealTimeTester rtTester = new RealTimeTester(buffer, ui);
        //rtTester.test();
     }

    /** inform worker about new information from buffer. */
    public void realTimeEvent(){
        mainFrame.realTimeEvent();
    }
}
