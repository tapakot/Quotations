package common;

import buffer.QuotationBuffer;
import common.Quotation;
import ui.MainFrame;

import javax.swing.*;

public class Worker {
    volatile QuotationBuffer buffer;
    MainFrame frame;

    public Worker(){}

    public void work(){
        buffer = new QuotationBuffer();
        buffer.startThread(this);
        while (!buffer.isReady){}
        Quotation quo =  buffer.getQuotation((short) 5, 99);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){ createGUI(); }
        });
    }

    private void createGUI(){
        frame = new MainFrame(buffer);
        frame.setVisible(true); //ought to control visibility from main class
    }

    public void realTimeEvent(){
        frame.realTimeEvent();
    }
}
