package buffer;

import common.Quotation;
import common.Worker;
import testing.RealTimeTester;

import java.util.ArrayList;

/** Main class of the package. Represents a buffer of quotations of various time periods (5, 15, 30, 60, 240 minutes).
 * Providing all real-time information from MetaTrader4 including Bid, Ask at the moment.
 * Manages buffer work.
 */
public class QuotationBuffer {
    public String name; //EURUSD
    /** collection of all known 5-minute quotations. used for history test. ought to be cleared after test. */
    public ArrayList<Quotation> history5;
    public ArrayList<Quotation> history15;
    public ArrayList<Quotation> history30;
    public ArrayList<Quotation> history60;
    public ArrayList<Quotation> history1440;
    volatile ArrayList<Quotation> quotations5;  //used by getQuotation() and realTimeEvent() which are different threads
    volatile ArrayList<Quotation> quotations15;
    volatile ArrayList<Quotation> quotations30;
    volatile ArrayList<Quotation> quotations60;
    volatile ArrayList<Quotation> quotations1440;
    volatile double bid;
    volatile double ask;
    private int counter;//used by real-time thread

    private ArrayList<Quotation> changingQuos;

    /** flag. true when buffer is initialised and ready to be used. */
    volatile public boolean isReady;

    /** flag. true when last quotation is real.
     * it may be not real because updating every 5 minutes. if 5 minutes have not passed, last quotation is wrong.
     */
    public boolean trueData;

    /** flag. if to call RealTimeTester.newData() */
    volatile public boolean test; //used by buffer and r/t tester

    /** count of all known 5-min quotations. approximately 10.000. */
    public int countHistory5;

    Worker worker;
    public RealTimeTester tester;
    RealTimeThread realTimeThread;


    /** initialising */
    public QuotationBuffer(String name, Worker worker){
        this.name = name;
        this.worker = worker;
        history5 = new ArrayList<>(); //getHistory() puts here first 100. needed only for history test (or the f*ck 10000)
        history15 = new ArrayList<>();
        history30 = new ArrayList<>();
        history60 = new ArrayList<>();
        history1440 = new ArrayList<>();
        quotations5 = new ArrayList<>();
        quotations15 = new ArrayList<>();
        quotations30 = new ArrayList<>();
        quotations60 = new ArrayList<>();
        quotations1440 = new ArrayList<>();
        counter = 0;
        test = false;
        tester = null;
        isReady = false;
        trueData = false;

        changingQuos = new ArrayList<>();
        changingQuos.add(new Quotation(0, 0, Double.MAX_VALUE, 0, 5));
        changingQuos.add(new Quotation(0, 0, Double.MAX_VALUE, 0, 15));
        changingQuos.add(new Quotation(0, 0, Double.MAX_VALUE, 0, 30));
        changingQuos.add(new Quotation(0, 0, Double.MAX_VALUE, 0, 60));
        changingQuos.add(new Quotation(0, 0, Double.MAX_VALUE, 0, 1440));

        realTimeThread = new RealTimeThread();
        realTimeThread.setBuffer(this);
        realTimeThread.start();
    }

    /** returns a quotation from buffer
     * @param period
     * @param index 0-99 index of quotation in buffer. 99th is the last one.
     */
    synchronized public Quotation getQuotation(int period, int index){
        Quotation quo = new Quotation();
        switch (period){
            case 5:
                quo.close = quotations5.get(index).close;
                quo.open = quotations5.get(index).open;
                quo.high = quotations5.get(index).high;
                quo.low = quotations5.get(index).low;
                break;
            case 15:
                quo.close = quotations15.get(index).close;
                quo.open = quotations15.get(index).open;
                quo.high = quotations15.get(index).high;
                quo.low = quotations15.get(index).low;
                break;
            case 30:
                quo.close = quotations30.get(index).close;
                quo.open = quotations30.get(index).open;
                quo.high = quotations30.get(index).high;
                quo.low = quotations30.get(index).low;
                break;
            case 60:
                quo.close = quotations60.get(index).close;
                quo.open = quotations60.get(index).open;
                quo.high = quotations60.get(index).high;
                quo.low = quotations60.get(index).low;
                break;
            case 1440:
                quo.close = quotations1440.get(index).close;
                quo.open = quotations1440.get(index).open;
                quo.high = quotations1440.get(index).high;
                quo.low = quotations1440.get(index).low;
                break;
            default:
                return null;
        }
        return quo;
    }

    synchronized public ArrayList<Quotation> getQuotation(int period){
        switch (period){
            case 5:
                return quotations5;
            case 15:
                return quotations15;
            case 30:
                return quotations30;
            case 60:
                return quotations60;
            case 1440:
                return quotations1440;
        }
        return null;
    }

    /** informs buffer manager about new data */
    void realTimeEvent(Quotation quo){
        counter += quo.period;
        changeBuffer(quo);
        //showQuotations();
        worker.realTimeEvent(name, counter);
        if(counter == 1440){
            counter = 0;
        }
    }

    /** changes buffer */
    private void changeBuffer(Quotation quo){
        //quotations5.add(quo); //передается объект, а не его содержимое. при изменении объекта поменяется и буфер
        for(Quotation chQ : changingQuos){
            if(chQ.open == 0){ chQ.open = quo.open;}
            if(chQ.high < quo.high){ chQ.high = quo.high;}
            if(chQ.low > quo.low){ chQ.low = quo.low;}
            chQ.close = quo.close;
        }

        if(counter%5==0){
            if (trueData==true){quotations5.remove(0);} else{quotations5.remove(quotations5.size()-1);}
            Quotation q = new Quotation(quo.open, quo.high, quo.low, quo.close, quo.period);
            quotations5.add(q);
        }
        //when all history files exist
        if(counter%15==0){
            if (trueData==true){quotations15.remove(0);} else{quotations15.remove(quotations15.size()-1);}
            Quotation q = new Quotation(15);
            q.open = changingQuos.get(1).open;
            q.high = changingQuos.get(1).high;
            q.low = changingQuos.get(1).low;
            q.close = changingQuos.get(1).close;
            quotations15.add(q); //adds 5-minute quo
            changingQuos.get(1).open = getBid();
            changingQuos.get(1).high = 0;
            changingQuos.get(1).low = Double.MAX_VALUE;

        }
        if(counter%30==0){
            if (trueData==true){quotations30.remove(0);} else{quotations30.remove(quotations30.size()-1);}
            Quotation q = new Quotation(30);
            q.open = changingQuos.get(2).open;
            q.high = changingQuos.get(2).high;
            q.low = changingQuos.get(2).low;
            q.close = changingQuos.get(2).close;
            quotations30.add(q); //adds 5-minute quo
            changingQuos.get(2).open = getBid();
            changingQuos.get(2).high = 0;
            changingQuos.get(2).low = Double.MAX_VALUE;
        }
        if(counter%60==0){
            if (trueData==true){quotations60.remove(0);} else{quotations60.remove(quotations60.size()-1);}
            Quotation q = new Quotation(60);
            q.open = changingQuos.get(3).open;
            q.high = changingQuos.get(3).high;
            q.low = changingQuos.get(3).low;
            q.close = changingQuos.get(3).close;
            quotations60.add(q); //adds 5-minute quo
            changingQuos.get(3).open = getBid();
            changingQuos.get(3).high = 0;
            changingQuos.get(3).low = Double.MAX_VALUE;
        }
        if(counter%1440==0){
            if (trueData==true){quotations1440.remove(0);} else{quotations1440.remove(quotations1440.size()-1);}
            Quotation q = new Quotation(1440);
            q.open = changingQuos.get(4).open;
            q.high = changingQuos.get(4).high;
            q.low = changingQuos.get(4).low;
            q.close = changingQuos.get(4).close;
            quotations1440.add(q); //adds 5-minute quo
            changingQuos.get(4).open = getBid();
            changingQuos.get(4).high = 0;
            changingQuos.get(4).low = Double.MAX_VALUE;
        }
    }

    /** shows last 100 quotations of the particular period */
    void showQuotations(){
        int i = 0;
        for (Quotation q : quotations5){
            System.out.println(i+" "+ q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close+" "+name);
            i++;
        }
        System.out.println("===========================================================================================");
        i = 0;
        for (Quotation q : quotations15){
            System.out.println(i+" "+ q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close+" "+name);
            i++;
        }
        System.out.println("===========================================================================================");
        /*for (Quotation q : quotations30){
            System.out.println(q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close);
        }
        System.out.println("===========================================================================================");
        for (Quotation q : quotations60){
            System.out.println(q.period+" "+q.open+" "+q.high+" "+q.low+" "+q.close);
        }
        System.out.println("===========================================================================================");
        for (Quotation q : quotations1440){
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
    public Quotation getOne(int period, int index){
        return HistoryGetter.getOne(name, period, index);
    }

    public void startRTTest(RealTimeTester tester){
        this.tester = tester;
        test = true;
    }
}
