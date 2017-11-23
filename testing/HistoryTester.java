package testing;

import advicing.Adviser;
import buffer.QuotationBuffer;
import common.*;
import static common.ForexConstants.*;
import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Class needed to test a strategy on the history
 * Manages the whole test.
 */
public class HistoryTester {
    private QuotationBuffer buffer;
    private double balance;
    private ArrayList<Position> positions;
    private HashSet<Position> toClose;
    private int currentIndex;
    private ArrayList<Quotation> bufferAll5;
    private int upCounter;
    private int downCounter;
    private boolean closed;

    /** initialisation */
    public HistoryTester(QuotationBuffer buffer){
        this.buffer = buffer;
        balance = START_BALANCE;
        positions = new ArrayList<Position>();
        toClose = new HashSet<>();
        upCounter = 0; //to not make positions too frequently
        downCounter = 0;
        closed = false;
    }

    /** starts and manages test. returns the value of balance after test. */
    public double test(){
        //preparing
        currentIndex = HIST_COUNT; //0-99 are analysed. 100th is the first to get advice about
        bufferAll5 = buffer.history5;


        /*while(currentIndex < buffer.countHistory5) {
            List<Quotation> buffer100 = bufferAll5.subList(currentIndex - HIST_COUNT, currentIndex); //in, ex (0-99)
            Quotation currentQuo = bufferAll5.get(currentIndex);
            int advice = adviser.getAdvice(buffer100, currentQuo);
            double nextOpen = bufferAll5.get(currentIndex + 1).open;
            double curLow = currentQuo.low;
            closed = false;
            //handling
            switch (advice) {
                case ADVICE_UP:
                    if (upCounter == 0) {
                        for (Position pos : positions) {
                            if (pos.direction == DOWN_DIRECTION) {
                                closePosition(pos, 1);
                                closed = true;
                            }
                        }
                        openPosition(nextOpen, UP_DIRECTION, (int) (balance * PERCENT_OF_BALANCE));
                        upCounter = UP_COUNTER;
                    }
                    break;
                case ADVICE_DOWN:
                    if (downCounter == 0) {
                        for (Position pos : positions) {
                            if (pos.direction == UP_DIRECTION) {
                                closePosition(pos, 1);
                                closed = true;
                            }
                        }
                        openPosition(nextOpen, DOWN_DIRECTION, (int) (balance * PERCENT_OF_BALANCE));
                        downCounter = DOWN_COUNTER;
                    }
                    break;
                case ADVICE_CLOSE_UP:
                    for (Position pos : positions) {
                        if (pos.direction == UP_DIRECTION) {
                            closePosition(pos, 1);
                            closed = true;
                        }
                    }
                    break;
                case ADVICE_CLOSE_DOWN:
                    for (Position pos : positions) {
                        if (pos.direction == DOWN_DIRECTION) {
                            closePosition(pos, 1);
                            closed = true;
                        }
                    }
                    break;
            }
            //if((upCounter!=UP_COUNTER) && (downCounter!=DOWN_COUNTER) && (!closed)) { //if there was opening then NO CLOSING of this positions on opening quo!
                for (Position pos : positions) {
                    //dynamic stopLoss
                    if(pos.profit(nextOpen, nextOpen) > 50){
                        double newStop = pos.profit(nextOpen, nextOpen) - pos.money/STOP_LOSS_DIVIDER;
                        if(newStop > pos.stopLoss){
                            pos.stopLoss = newStop;
                        }
                    }

                    if ((pos.profit(nextOpen, nextOpen) <= pos.stopLoss) || (pos.profit(curLow, curLow) <= pos.stopLoss)) {
                        closePosition(pos, 2);
                        continue;
                    }
                    if ((pos.money + pos.profit(nextOpen, nextOpen) <= 0) || (pos.money + pos.profit(curLow, curLow) <= 0)) {
                        closePosition(pos, 3);
                        continue;
                    }
                    if ((pos.profit(nextOpen, nextOpen) >= pos.takeProfit) || (pos.profit(currentQuo.high, currentQuo.high) >= pos.takeProfit)) {
                        closePosition(pos, 4);
                        continue;
                    }
                }
            //}
            for (Position pos : toClose) { //close once because toClose is HashSet
                double profit = 1;
                int cause = 1;
                if ((pos.profit(nextOpen, nextOpen) <= pos.stopLoss) || (pos.profit(curLow, curLow) <= pos.stopLoss)) {
                    profit = pos.stopLoss;
                    cause = 2;
                } else
                if ((pos.money + pos.profit(nextOpen, nextOpen) <= 0) || (pos.money + pos.profit(curLow, curLow) <= 0)) {
                    profit = -pos.money;
                    cause = 3;
                } else
                if ((pos.profit(nextOpen, nextOpen) >= pos.takeProfit) || (pos.profit(currentQuo.high, currentQuo.high) >= pos.takeProfit)) {
                    profit = pos.takeProfit;
                    cause = 4;
                }else{
                    profit = pos.profit(nextOpen, nextOpen);
                }
                balance += profit + pos.money;
                /*System.out.print("Position closed at " + nextOpen + ". was opened at " + pos.price + ". Profit: " + profit+". cause: "+cause);
                System.out.println(" balance after closing: " + balance);
                positions.remove(pos);
            }
            toClose.clear();


            currentIndex++;
            if(upCounter != 0) {upCounter--;}
            if(downCounter != 0){downCounter--;}
        }
*/
        //bufferAll = null; //buffer.history becomes null
        return balance;
    }

    /** imitates an opening */
    /*void openPosition(double price, int direction, int money){
        if((balance - money >= 0)&&(balance>=50)) {
            balance -= money;
            //informing output
            /*System.out.print("Position opened at " + price + " (" + (currentIndex + 1) + ")"); //open price of next quo
            if (direction == UP_DIRECTION) {
                System.out.print(" in UP direction.");
            } else {
                System.out.print(" in DOWN direction.");
            }
            System.out.println(" money: " + money + ". balance after opening: " + balance);/

            positions.add(new Position(price, direction, money, 100));
        } else {
            //System.out.println("not enough money to open position");
        }
    }*/

    /** imitates a closing */
    private void closePosition(Position posToClose, int cause){    // wrong!!! (why?) twice profit
        if(currentIndex!=buffer.countHistory5) { //right. countHistory from 0 as well as curIndex.

            toClose.add(posToClose);
            /*System.out.print("Position closed at " + nextOpen + ". was opened at " + posToClose.price + ". Profit: " + profit);
            System.out.println(" balance after closing: " + balance);*/
        }
    }
}
