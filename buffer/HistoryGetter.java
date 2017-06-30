package buffer;
import common.Quotation;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

class HistoryGetter {

    ArrayList<Quotation> getHistoryOf(short period){      //uses apache.POI to work with CLOSED excel files
        ArrayList<Quotation> list = new ArrayList<>();
        String path = "res\\EURUSD"+ period+".xls";
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

    ArrayList<Quotation> getHistory(QuotationBuffer buffer){
        ArrayList<Quotation> list = new ArrayList<>();
        String path = "res\\EURUSD5.xls";
        InputStream in = null; //probably not the best way to read. see InStreams vs. Files
        Workbook wb = null;
        try {
            in = new FileInputStream(path);
            wb = new HSSFWorkbook(in);
        }
        catch (FileNotFoundException e){System.out.println("!!! Check the path to the History !!!");}
        catch (IOException e){System.out.println("!!! IOExeption while reading History !!!");}
        Sheet sheet = wb.getSheetAt(0);
        buffer.countHistory = sheet.getLastRowNum();
        for(int rowNum = 0; rowNum <= 99; rowNum++){
            Row row = sheet.getRow(rowNum);
            Quotation quo = new Quotation((short) 5);
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

    Quotation getOne(short period, int index){
        Quotation quo = new Quotation(period);
        String path = "res\\EURUSD"+ period+".xls";
        InputStream in = null; //probably not the best way to read. see InStreams vs. Files
        Workbook wb = null;
        try {
            in = new FileInputStream(path);
            wb = new HSSFWorkbook(in);
        }
        catch (FileNotFoundException e){System.out.println("!!! Check the path to the History !!!");}
        catch (IOException e){System.out.println("!!! IOExeption while reading History !!!");}
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(index);
        Cell cell = row.getCell(2);
        quo.open = Double.parseDouble(cell.getStringCellValue());
        cell = row.getCell(3);
        quo.high = Double.parseDouble(cell.getStringCellValue());
        cell = row.getCell(4);
        quo.low = Double.parseDouble(cell.getStringCellValue());
        cell = row.getCell(5);
        quo.close = Double.parseDouble(cell.getStringCellValue());
        try{
            in.close();
        }
        catch (IOException e){}
        return quo;
    }
}
