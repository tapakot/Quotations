package testing;

import buffer.QuotationBuffer;
import common.Quotation;
import ui.MainFrame;
import ui.UiThread;
import advicing.*;

/** Class needed to test strategy on real-time data.
 * Manages the whole test.
 */
public class RealTimeTester {
    MainFrame ui;
    RealTimeTesterThread RTthread;
    Adviser adviser;

    /** initialising.
     * @param ui where to show opened positions
     */
    public RealTimeTester(MainFrame ui, Adviser adviser){
        this.ui = ui;
        this.adviser = adviser;
    }

    /** main method. manages the test.
     * an endless test until stopTest().
     */
    public void test(){
        RTthread = new RealTimeTesterThread(ui, this, adviser);
        RTthread.start();
    }

    public void newData(String instrumentName){
        RTthread.newData(instrumentName);
        ui.newBid();
    }

    public void stopTest(){
        RTthread.stopThread();
    }
}
