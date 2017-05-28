import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.*;
import java.util.ArrayList;

public class CsvToXlsParser {
    public static void parse(short period){
        ArrayList cells = null;
        ArrayList rows = new ArrayList();
        String pathSource = "res\\EURUSD" + period + ".csv";
        String pathDest = "res\\EURUSD" + period + ".xls";
        String thisLine = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(pathSource);
        }
        catch(FileNotFoundException e){System.out.println("!!! FileNotFound while parsing to .xls !!!");}
        DataInputStream myInput = new DataInputStream(fin);
        try{
            thisLine = myInput.readLine();
        }
        catch(IOException e){System.out.println("============EXCEPTION===============");}
        while (thisLine != null)
        {
            cells = new ArrayList();
            String str[] = thisLine.split(";");
            for(int j=0;j<str.length;j++) {
                cells.add(str[j]);
            }
            rows.add(cells);
            try{
                thisLine = myInput.readLine();
            }
            catch(IOException e){System.out.println("============EXCEPTION===============");}
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("new sheet");
        for(int rowNumber=0; rowNumber<rows.size(); rowNumber++)
        {
            cells = (ArrayList)rows.get(rowNumber);
            HSSFRow row = sheet.createRow(rowNumber);
            for(int cellNumber=0; cellNumber<cells.size(); cellNumber++)
            {
                HSSFCell cell = row.createCell(cellNumber);
                String data = cells.get(cellNumber).toString();
                cell.setCellValue(data);
            }
        }
        FileOutputStream fileOut = null;
        try{
            fileOut = new FileOutputStream(pathDest);
        }
        catch (FileNotFoundException e){}
        try {
            wb.write(fileOut);
            fileOut.close();
        }
        catch (IOException e){System.out.println("============EXCEPTION===============");}
        System.out.println("Your excel file has been generated");
    }
}