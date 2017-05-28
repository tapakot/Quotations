public class Quotation {
    double open = 0;
    double high = 0;
    double low = 0;
    double close = 0;
    short period = 5;

    public Quotation(double open, double high, double low, double close, short period){
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.period = period;
    }

    public Quotation(short period){
        this.period = period;
    }
}
