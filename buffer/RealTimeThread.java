package buffer;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import common.Quotation;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/** thread of quotation buffer. collects information from MetaTrader4. */
class RealTimeThread extends Thread{
    QuotationBuffer buffer;
    HistoryGetter getter;

    /** buffer to inform */
    void setBuffer(QuotationBuffer buffer){
        this.buffer = buffer;
    }

    /** gets history (initialising buffer) and starts to collect new data */
    @Override
    public void run(){
        System.out.println("getting history");
        setHistory();
        realTime();
    }

    /** Starts collecting real-time data.
     * Opens excel file and starts DDE conversation. After that the buffer is ready.
     */
    private void realTime(){
        //open Excel with real-time quotations
        Desktop dk = Desktop.getDesktop();
        File file = null;
        try {
            file = new File("res\\real-time.xls");
            dk.open(file);
        }catch(IOException e){e.printStackTrace();}
        /*try{
            Thread.sleep(10000);
        }catch (InterruptedException e){}*/

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

    /** gets 100 quotations of all periods. Gets history all history for history test. Shows 5-min quotations. */
    private void setHistory(){
        getter = new HistoryGetter();
        CsvToXlsParser.parse((short)5);
        buffer.quotations5 = getter.getHistoryOf((short)5);
        buffer.history = getter.getHistory(buffer);
        buffer.showQuotations();
    }
}
