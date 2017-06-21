package common;

import buffer.QuotationBuffer;
import common.Quotation;
import ui.MainFrame;
import analysis.Analyser;
import ui.UiThread;

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

        /*Thread ui = new Thread(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });*/
        UiThread ui = new UiThread(buffer);
        ui.start();
        do{}while (!ui.frameIsReady);
        frame = ui.getFrame();
        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){ createGUI(); }
        });*/

        Analyser analyser = new Analyser();
        analyser.setBuffer(buffer);
        analyser.analyse("extremum");
        System.out.println("maximums:");
        for(int i = 0; i<analyser.maximums.size(); i++){
            System.out.println(analyser.maximums.get(i));
        }
        /*for(Double maximum : analyser.maximums){
            System.out.println(maximum);
        }*/
        System.out.println("minimums");
        for(double minimum : analyser.minimums){
            System.out.println(minimum);
        }

        frame.drawExtremums(analyser.maximums, analyser.minimums);
        frame.drawResLines(analyser.exLines);

    }

    private void createGUI(){
        frame = new MainFrame(buffer);
        frame.setVisible(true); //ought to control visibility from main class
    }

    public void realTimeEvent(){
        frame.realTimeEvent();
    }
}
