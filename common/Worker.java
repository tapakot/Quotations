package common;

import buffer.QuotationBuffer;
import common.Quotation;
import testing.HistoryTester;
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

        UiThread ui = new UiThread(buffer);
        ui.start();
        do{}while (!ui.frameIsReady);
        frame = ui.getFrame();

        Analyser analyser = new Analyser();
        analyser.setQuotationBuffer(buffer);
        analyser.analyse("extremum");

        //test
        System.out.println("maximums:");
        for(int i = 0; i<analyser.getBuffer().maximums.size(); i++){
            System.out.println(analyser.getBuffer().maximums.get(i));
        }
        System.out.println("minimums");
        for(double minimum : analyser.getBuffer().minimums){
            System.out.println(minimum);
        }

        frame.drawExtremums(analyser.getBuffer().maximums, analyser.getBuffer().minimums);
        frame.drawResLines(analyser.getBuffer().exLines);

        HistoryTester tester = new HistoryTester(buffer);
        System.out.println("==================================balance after history test: " + tester.test());
    }


    public void realTimeEvent(){
        frame.realTimeEvent();
    }
}
