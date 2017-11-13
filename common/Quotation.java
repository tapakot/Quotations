package common;

/** Represents a ForEx quotation. */
public class Quotation {
    public double open = 0;
    public double high = 0;
    public double low = 0;
    public double close = 0;
    public int period = 5;

    /** creates a new one */
    public Quotation(double open, double high, double low, double close, int period){
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.period = period;
    }

    /** creates a new one with zero params*/
    public Quotation(int period){
        this.period = period;
    }

    /** creates a new one  with zero params and a standard period (5) */
    public Quotation(){
        this.period = 5;
    }

    @Override
    public String toString(){
        String result;
        result = period + ". o:" + open +" h:"+ high+ " l:"+low+" c:"+close;
        return result;
    }
}
