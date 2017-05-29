import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.*;
import org.apache.poi.ss.usermodel.*;

public class Getter {

    public static ArrayList<Quotation> getHistoryOf(short period){
        ArrayList<Quotation> list = new ArrayList<>();
        String path = "res\\EURUSD"+ period+".xls"; //ought to be relative
        InputStream in = null; //probably not the best way to read. see InStreams vs. Files
        Workbook wb = null;
        try {
            in = new FileInputStream(path);
            wb = new HSSFWorkbook(in);
        }
        catch (FileNotFoundException e){System.out.println("!!! Check the path to the History !!!");}
        catch (IOException e){System.out.println("!!! IOExeption while reading History !!!");}
        Sheet sheet = wb.getSheetAt(0);
        int rowStart = sheet.getLastRowNum()-99; //100th from tail
        int rowEnd = sheet.getLastRowNum();
        for(int rowNum = rowStart; rowNum <= rowEnd; rowNum++){
            Row row = sheet.getRow(rowNum);
            Quotation quo = new Quotation(period);
            Cell cell = row.getCell(2);
            quo.open = Double.parseDouble(cell.getStringCellValue());
            cell = row.getCell(3);
            quo.high = Double.parseDouble(cell.getStringCellValue());
            cell = row.getCell(4);
            quo.low = Double.parseDouble(cell.getStringCellValue());
            cell = row.getCell(5);
            quo.close = Double.parseDouble(cell.getStringCellValue());
            list.add(quo);
        }
        try{
            in.close();
        }
        catch (IOException e){}
        System.out.println("!Test. size = "+list.size());
        return list;
    }
}
