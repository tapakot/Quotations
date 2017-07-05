package common;

import static common.ForexConstants.*;

/** Represents a ForEx position.  */
public class Position {

    public int direction;
    public int money;
    public double price;
    public double stopLoss;
    public double takeProfit;

    /** creates new position
     * @param price a price, where position was created
     * @param direction up or down
     * @param money volume of position in USD
     */
    public Position(double price, int direction, int money){
        this.price = price;
        this.direction = direction;
        this.money = money;
    }

    /** count the profit at the current moment
     * @param bid price to close up-directed positions.
     * @param ask price to close down-directed positions.
     */
    public double profit(double bid, double ask){
        double profit;
        if(direction == UP_DIRECTION){    //close by bid
            profit = bid*money - price*money - price*money*COMMISSION;
        } else {    //close by ask
            profit = price*money - bid*money - price*money*COMMISSION;
        }
        return profit;
    }
}
