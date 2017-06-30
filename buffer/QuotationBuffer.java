package buffer;

import common.Quotation;
import common.Worker;

import java.util.ArrayList;

public class QuotationBuffer {
    public ArrayList<Quotation> history;
    volatile ArrayList<Quotation> quotations5;  //used by getQuotation() and realTimeEvent() which are different threads
    volatile ArrayList<Quotation> quotations15;
    volatile ArrayList<Quotation> quotations30;
    volatile ArrayList<Quotation> quotations60;
    volatile ArrayList<Quotation> quotations240;
    volatile double bid;
    volatile double ask;
    private int counter;//used by real-time thread
    public boolean isReady;
    public boolean trueData;
    public int countHistory;
    Worker worker;
    RealTimeThread realTimeThread;

    public QuotationBuffer(){
        history = new ArrayList<>(); //getHistory() puts here first 100. needed only for history test
        quotations5 = new ArrayList<>();
        quotations15 = new ArrayList<>();
        quotations30 = new ArrayList<>();
        quotations60 = new ArrayList<>();
        quotations240 = new ArrayList<>();
        counter = 0;
        isReady = false;
    }

    public void startThread(Worker worker){
        this.worker = worker;
        realTimeThread = new RealTimeThread();
        realTimeThread.setBuffer(this);
        realTimeThread.start();
    }

    synchronized public Quotation getQuotation(short period, int index){
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
        showQuotations();
        worker.realTimeEvent();
        if(counter == 240){
            counter = 0;
        }
    }

    private void changeBuffer(Quotation quo){
        if(counter%5==0){
            if (trueData==true){quotations5.remove(0);} else{quotations5.remove(99);}
            //quotations5.add(quo); //передается объект, а не его содержимое. при изменении объекта поменяется и буфер
            Quotation q = new Quotation();
            q.close=quo.close;
            q.open=quo.open;
            q.high=quo.high;
            q.low = quo.low;
            quotations5.add(q);
        }
        /*when all history files exist
        if(counter%15==0){
            if (trueData==true){quotations15.remove(0);} else{quotations15.remove(99);}
            quotations15.add(quo);
        }
        if(counter%30==0){
            if (trueData==true){quotations30.remove(0);} else{quotations30.remove(99);}
            quotations30.add(quo);
        }
        if(counter%60==0){
            if (trueData==true){quotations60.remove(0);} else{quotations60.remove(99);}
            quotations60.add(quo);
        }
        if(counter%240==0){
            if (trueData==true){quotations240.remove(0);} else{quotations240.remove(99);}
            quotations240.add(quo);
        }
        */
    }

    void showQuotations(){
        int i = 0;
        for (Quotation q : quotations5){
            System.out.println(i+" "+ q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close);
            i++;
        }
        System.out.println("===========================================================================================");
        /*for (Quotation q : quotations15){
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
        System.out.println("===========================================================================================");*/
    }

    public double getBid(){
        return bid;
    }

    public double getAsk(){
        return ask;
    }

    public Quotation getOne(short period, int index){
        return realTimeThread.getter.getOne(period, index);
    }
}
