package common;

import static common.ForexConstants.*;

public class Position {

    public int direction;
    public int money;
    //public int index;
    public double price;
    public double stopLoss;
    public double takeProfit;

    public Position(double price, int direction, int money){
        this.price = price;
        this.direction = direction;
        this.money = money;
    }

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
