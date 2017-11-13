package buffer;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;
import common.Quotation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.awt.*;
import java.io.*;

import static common.ForexConstants.HIST_COUNT;

/** thread of quotation buffer. collects information from MetaTrader4. */
class RealTimeThread extends Thread{
    QuotationBuffer buffer;

    int row = 1; //number of needed row. each row for one instrument
    static boolean rtFileOpened = false;

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
        //find the row in real-time file
        InputStream in = null; //probably not the best way to read. see InStreams vs. Files
        Workbook wb = null;
        try {
            in = new FileInputStream("res\\real-time.xls");
            wb = new HSSFWorkbook(in);
        }
        catch (FileNotFoundException e){System.out.println("!!!");}
        catch (IOException e){System.out.println("!!!");}
        Sheet sheet = wb.getSheetAt(0);
        int rowStart = 0; //100th from tail
        int rowEnd = sheet.getLastRowNum();
        for(int rowNum = rowStart; rowNum <= rowEnd; rowNum++){
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(0);
            if(cell.getStringCellValue().equals(buffer.name)){
                this.row = rowNum+1;
                break;
            }
        }
        try{
            in.close();
        } catch (IOException e){};

        //open Excel with real-time quotations (only once)
        if (!rtFileOpened) {
            rtFileOpened = true;
            Desktop dk = Desktop.getDesktop();
            File file = null;
            try {
                file = new File("res\\real-time.xls");
                dk.open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //dde
        try {
            DDEClientConversation conversation = new DDEClientConversation();
            DDEListener listener = new DDEListener(buffer);
            conversation.setEventListener(listener);
            conversation.connect("Excel", "Лист1");
            conversation.startAdvice("R"+row+"C2");
            conversation.startAdvice("R"+row+"C3");
            conversation.startAdvice("R"+row+"C4");

            System.out.println("Started to listen real-time changes");
            buffer.isReady = true;
            while(true){}
            //conversation.disconnect();
        }
        catch (DDEException e) {System.out.println("something went wrong in Real-Time Quos");}
    }

    /** gets 100 quotations of all periods. Gets history all history for history test. Shows 5-min quotations. */
    private void setHistory(){
        CsvToXlsParser.parse(buffer.name, 5);
        CsvToXlsParser.parse(buffer.name, 15);
        CsvToXlsParser.parse(buffer.name, 30);
        CsvToXlsParser.parse(buffer.name, 60);
        CsvToXlsParser.parse(buffer.name, 1440);
        buffer.quotations5 = HistoryGetter.getHistoryOf(buffer.name, (short)5);
        buffer.quotations15 = HistoryGetter.getHistoryOf(buffer.name, (short)15);
        buffer.quotations30 = HistoryGetter.getHistoryOf(buffer.name, (short)30);
        buffer.quotations60 = HistoryGetter.getHistoryOf(buffer.name, (short)60);
        buffer.quotations1440 = HistoryGetter.getHistoryOf(buffer.name, (short)1440);
        buffer.history5 = HistoryGetter.getHistory(buffer, 5);
        buffer.history15 = HistoryGetter.getHistory(buffer, 15);
        buffer.history30 = HistoryGetter.getHistory(buffer, 30);
        buffer.history60 = HistoryGetter.getHistory(buffer, 60);
        buffer.history1440 = HistoryGetter.getHistory(buffer, 1440);
        buffer.showQuotations();
    }
}
