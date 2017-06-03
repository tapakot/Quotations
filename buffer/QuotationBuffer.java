package buffer;

import common.Quotation;

import java.util.ArrayList;

public class QuotationBuffer {
    volatile ArrayList<Quotation> quotations5;  //used by getQuotation() and realTimeEvent() which are different threads
    volatile ArrayList<Quotation> quotations15;
    volatile ArrayList<Quotation> quotations30;
    volatile ArrayList<Quotation> quotations60;
    volatile ArrayList<Quotation> quotations240;
    private int counter;//used by real-time thread
    public boolean isReady;

    public QuotationBuffer(){
        quotations5 = new ArrayList<>();
        quotations15 = new ArrayList<>();
        quotations30 = new ArrayList<>();
        quotations60 = new ArrayList<>();
        quotations240 = new ArrayList<>();
        counter = 0;
        isReady = false;
    }

    public void startThread(){
        RealTimeThread realTimeThread = new RealTimeThread();
        realTimeThread.setBuffer(this);
        realTimeThread.start();
    }

    public Quotation getQuotation(short period, int index){
        Quotation quo = new Quotation();
        switch (period){
            case 5:
                quo = quotations5.get(index);
                break;
            case 15:
                quo = quotations15.get(index);
                break;
            case 30:
                quo = quotations30.get(index);
                break;
            case 60:
                quo = quotations60.get(index);
                break;
            case 240:
                quo = quotations240.get(index);
                break;
        }
        return quo;
    }

    void realTimeEvent(Quotation quo){
        counter += quo.period;
        changeBuffer(quo);
        if(counter == 240){
            counter = 0;
        }
    }

    private void changeBuffer(Quotation quo){
        if(counter%5==0){
            quotations5.remove(0);
            quotations5.add(quo);
        }
        if(counter%15==0){
            quotations15.remove(0);
            quotations15.add(quo);
        }
        if(counter%30==0){
            quotations30.remove(0);
            quotations30.add(quo);
        }
        if(counter%60==0){
            quotations60.remove(0);
            quotations60.add(quo);
        }
        if(counter%240==0){
            quotations240.remove(0);
            quotations240.add(quo);
        }
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