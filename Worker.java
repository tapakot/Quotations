import java.util.ArrayList;

public class Worker {
    public ArrayList<Quotation> quotations5;
    public ArrayList<Quotation> quotations15;
    public ArrayList<Quotation> quotations30;
    public ArrayList<Quotation> quotations60;
    public ArrayList<Quotation> quotations240;

    public Worker(){}

    public void work(){
        CsvToXlsParser.parse((short)5);
        CsvToXlsParser.parse((short)15);
        CsvToXlsParser.parse((short)30);
        CsvToXlsParser.parse((short)60);
        CsvToXlsParser.parse((short)240);
        quotations5 = Getter.getHistoryOf((short)5);
        quotations15 = Getter.getHistoryOf((short)15);
        quotations30 = Getter.getHistoryOf((short)30);
        quotations60 = Getter.getHistoryOf((short)60);
        quotations240 = Getter.getHistoryOf((short)240);
        showQuotations();
    }

    private void showQuotations(){
        for (Quotation q : quotations5){
            System.out.println(q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close);
        }
        System.out.println("===========================================================================================");
        for (Quotation q : quotations15){
            System.out.println(q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close);
        }
        System.out.println("===========================================================================================");
        for (Quotation q : quotations30){
            System.out.println(q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close);
        }
        System.out.println("===========================================================================================");
        for (Quotation q : quotations60){
            System.out.println(q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close);
        }
        System.out.println("===========================================================================================");
        for (Quotation q : quotations240){
            System.out.println(q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close);
        }
        System.out.println("===========================================================================================");
    }
}
