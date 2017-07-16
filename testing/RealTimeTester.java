package testing;

import buffer.QuotationBuffer;
import common.Quotation;
import ui.UiThread;
import advicing.*;

/** Class needed to test strategy on real-time data.
 * Manages the whole test.
 */
public class RealTimeTester {
    QuotationBuffer buffer;
    UiThread ui;
    RealTimeTesterThread thread;

    /** initialising.
     * @param buffer to be analysed and get new data from
     * @param ui where to show opened positions
     */
    public RealTimeTester(QuotationBuffer buffer, UiThread ui){
        this.buffer = buffer;
        this.ui = ui;
    }

    /** main method. manages the test.
     * an endless test until stopTest().
     */
    public void test(){
        thread = new RealTimeTesterThread(buffer, ui, this);
        thread.start();
    }

    public void newData(){
        thread.newData();
        ui.newBid();
    }

    public void stopTest(){
        thread.stopThread();
    }
}
