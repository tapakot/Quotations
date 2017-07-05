package buffer;

import common.Quotation;
import common.Worker;

import java.util.ArrayList;

/** Main class of the package. Represents a buffer of quotations of various time periods (5, 15, 30, 60, 240 minutes).
 * Providing all real-time information from MetaTrader4 including Bid, Ask at the moment.
 * Manages buffer work.
 */
public class QuotationBuffer {
    /** collection of all known 5-minute quotations. used for history test. ought to be cleared after test. */
    public ArrayList<Quotation> history;

    volatile ArrayList<Quotation> quotations5;  //used by getQuotation() and realTimeEvent() which are different threads
    volatile ArrayList<Quotation> quotations15;
    volatile ArrayList<Quotation> quotations30;
    volatile ArrayList<Quotation> quotations60;
    volatile ArrayList<Quotation> quotations240;
    volatile double bid;
    volatile double ask;
    private int counter;//used by real-time thread

    /** flag. true when buffer is initialised and ready to be used. */
    public boolean isReady;

    /** flag. true when last quotation is real.
     * it may be not real because updating every 5 minutes. if 5 minutes have not passed, last quotation is wrong wrong.
     */
    public boolean trueData;

    /** count of all known 5-min quotations. approximately 10.000. */
    public int countHistory;

    Worker worker;
    RealTimeThread realTimeThread;

    /** initialising */
    public QuotationBuffer(){
        history = new ArrayList<>(); //getHistory() puts here first 100. needed only for history test (or the f*ck 10000)
        quotations5 = new ArrayList<>();
        quotations15 = new ArrayList<>();
        quotations30 = new ArrayList<>();
        quotations60 = new ArrayList<>();
        quotations240 = new ArrayList<>();
        counter = 0;
        isReady = false;
    }

    /** starts buffer.
     * starts a new thread for buffer to collect information
     * @param worker worker to inform about new data
     */
    public void startThread(Worker worker){
        this.worker = worker;
        realTimeThread = new RealTimeThread();
        realTimeThread.setBuffer(this);
        realTimeThread.start();
    }

    /** returns a quotation from buffer
     * @param period
     * @param index 0-99 index of quotation in buffer. 99th is the last one.
     */
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

    /** informs buffer manager about new data */
    void realTimeEvent(Quotation quo){
        counter += quo.period;
        changeBuffer(quo);
        showQuotations();
        worker.realTimeEvent();
        if(counter == 240){
            counter = 0;
        }
    }

    /** changes buffer */
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

    /** shows last 100 quotations of the particular period */
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

    /** returns Bid at the moment */
    public double getBid(){
        return bid;
    }

    /** returns Ask at the moment */
    public double getAsk(){
        return ask;
    }

    /** returns any known quotation (not only from last 100).
     * needs too much time for execution.
     * @param period
     * @param index from 0 to (countHistory-1)
     */
    public Quotation getOne(short period, int index){
        return realTimeThread.getter.getOne(period, index);
    }
}
