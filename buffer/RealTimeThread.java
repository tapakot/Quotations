package buffer;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import common.Quotation;

import java.awt.*;
import java.io.File;
import java.io.IOException;

class RealTimeThread extends Thread{
    QuotationBuffer buffer;
    HistoryGetter getter;

    void setBuffer(QuotationBuffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void run(){
        System.out.println("getting history");
        setHistory();
        realTime();
    }

    private void realTime(){
        //open Excel with real-time quotations
        Desktop dk = Desktop.getDesktop();
        File file = null;
        try {
            file = new File("res\\real-time.xls");
            dk.open(file);
        }catch(IOException e){e.printStackTrace();}

        //dde
        try {
            DDEClientConversation conversation = new DDEClientConversation();
            DDEListener listener = new DDEListener(buffer);
            conversation.setEventListener(listener);
            conversation.connect("Excel", "Лист1");
            conversation.startAdvice("R1C2");
            conversation.startAdvice("R1C3");
            conversation.startAdvice("R1C4");

            System.out.println("Started to listen real-time changes");
            buffer.isReady = true;
            while(true){}
            //conversation.disconnect();
        }
        catch (DDEException e) {System.out.println("something went wrong in Real-Time Quos");}
    }

    private void setHistory(){
        getter = new HistoryGetter();
        CsvToXlsParser.parse((short)5);
        /*CsvToXlsParser.parse((short)15);
        CsvToXlsParser.parse((short)30);
        CsvToXlsParser.parse((short)60);
        CsvToXlsParser.parse((short)240);*/
        buffer.quotations5 = getter.getHistoryOf((short)5);
        buffer.history = getter.getHistory(buffer);
        buffer.showQuotations();
        /*buffer.quotations15 = getter.getHistoryOf((short)15);
        buffer.quotations30 = getter.getHistoryOf((short)30);
        buffer.quotations60 = getter.getHistoryOf((short)60);
        buffer.quotations240 = getter.getHistoryOf((short)240);*/
    }
}
