package testing;

import common.Worker;
import ui.AdjustmentThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdjustmentTester{
    public AdjustmentTester(){

    }

    public void test(){ //manages entire test. even ui output
        AdjustmentThread adjThread = new AdjustmentThread(Worker.mainFrame);
        adjThread.start();
    }

}
