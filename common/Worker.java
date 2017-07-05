package common;

import buffer.QuotationBuffer;
import common.Quotation;
import testing.HistoryTester;
import ui.MainFrame;
import analysis.Analyser;
import ui.UiThread;

import javax.swing.*;

/** Worker manages work of some other classes.
 * One of the main classes.
 * Manages Buffer, User Interface.
 */
public class Worker {
    /** buffer to manage */
    volatile QuotationBuffer buffer;
    /** main frame of User Interface */
    MainFrame frame;

    public Worker(){}

    /** manages all work.
     * main method. starts Buffer, UI, doing tests.
     * manages UI, testers.
     */
    public void work(){
        //start buffer. wait for it
        buffer = new QuotationBuffer();
        buffer.startThread(this);
        while (!buffer.isReady){}   //waits for buffer initialisation

        //start UI
        UiThread ui = new UiThread(buffer);
        ui.start();
        do{}while (!ui.frameIsReady);
        frame = ui.getFrame();

        //start analyser. FOR TESTS ONLY
        Analyser analyser = new Analyser();
        analyser.setQuotationBuffer(buffer);
        analyser.analyse("extremum");

        //test
        System.out.println("maximums:"); //maximums of the 100 last quotations
        for(int i = 0; i<analyser.getBuffer().maximums.size(); i++){
            System.out.println(analyser.getBuffer().maximums.get(i));
        }
        System.out.println("minimums:");
        for(double minimum : analyser.getBuffer().minimums){
            System.out.println(minimum);
        }

        //managing UI
        ui.drawExtremes(analyser.getBuffer().maximums, analyser.getBuffer().minimums);
        ui.drawResLines(analyser.getBuffer().exLines);

        //history test
        HistoryTester tester = new HistoryTester(buffer);
        System.out.println("==================================balance after history test: " + tester.test());
    }

    /** inform worker about new information from buffer. */
    public void realTimeEvent(){
        frame.realTimeEvent();
    }
}
