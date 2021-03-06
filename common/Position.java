package common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static common.ForexConstants.*;

/** Represents a ForEx position.  */
public class Position {
    public String instrument;
    public int direction;
    public int money;
    public double price;
    public double stopLoss;    //in $
    public double takeProfit;
    public Date date;
    public double commission; //in $
    public double allMoney;

    /** creates new position
     * @param instrument name of the instrument (EURUSD)
     * @param price a price, where position was created
     * @param direction up or down
     * @param money volume of position in USD
     */
    public Position(String instrument, double price, int direction, int money, int takeProfit){
        this.instrument = instrument;
        this.price = price;
        this.direction = direction;
        if(money<500){
            this.money = money;
        } else {
            this.money = money;
        }
        //this.takeProfit = TAKE_PROFIT;
        //this.takeProfit = takeProfit;
        this.takeProfit = money*TAKE_PROFIT;
        stopLoss = -money/STOP_LOSS_DIVIDER;
        commission = money * COMMISSION;
        allMoney = money * MULTIPLIERS.get(instrument);
        date = new Date();
    }

    /** count the profit at the current moment
     * @param bid price to close up-directed positions.
     * @param ask price to close down-directed positions.
     */
    public double profit(double bid, double ask){
        double profit;
        double res1, spend;
        spend = allMoney;
        if(direction == UP_DIRECTION){    //close by bid
            res1 = spend / price * bid;
            profit = res1 - spend - commission;
        } else {    //close by ask
            res1 = spend * price / bid;
            profit = res1 - spend - commission;
        }
        profit = round(profit, 2);
        return profit;
    }

    public String toString(double bid, double ask){
        String result = "";
        result += round(price, 5) + " \t";
        if(direction == UP_DIRECTION){
            result+= "UP \t";
        } else {
            result+= "DOWN \t";
        }
        SimpleDateFormat dtfrmt = new SimpleDateFormat("HH:mm");
        result+= dtfrmt.format(date) +" \t";
        result+=round(profit(bid, ask), 2)+" \t";

        return result;
    }

    public String getDirectionString(){
        if(direction == UP_DIRECTION){
            return "UP";
        } else {
            return "DOWN";
        }
    }

    public String getTimeSting(){
        SimpleDateFormat dtfrmt = new SimpleDateFormat("HH:mm");
        return dtfrmt.format(date);
    }

    double round(double d, int digitsAfterComma){
        int mult =1;
        for(int i=0; i<digitsAfterComma; i++){
            mult*=10;
        }
        d = d * mult;
        int i = (int)Math.round(d);
        d = (double) i/mult;
        return d;
    }
}
