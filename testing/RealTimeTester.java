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
    QuotationBuffer buffer;
    MainFrame ui;
    RealTimeTesterThread RTthread;

    /** initialising.
     * @param buffer to be analysed and get new data from
     * @param ui where to show opened positions
     */
    public RealTimeTester(QuotationBuffer buffer, MainFrame ui){
        this.buffer = buffer;
        this.ui = ui;
    }

    /** main method. manages the test.
     * an endless test until stopTest().
     */
    public void test(){
        RTthread = new RealTimeTesterThread(buffer, ui, this);
        RTthread.start();
    }

    public void newData(){
        RTthread.newData();
        ui.newBid();
    }

    public void stopTest(){
        RTthread.stopThread();
    }
}
