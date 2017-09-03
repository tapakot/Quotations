package testing;

import advicing.Adviser;
import analysis.Analyser;
import buffer.QuotationBuffer;
import common.*;
import static common.ForexConstants.*;

import java.util.ArrayList;
import java.util.List;

/** Class needed to test a strategy on the history
 * Manages the whole test.
 */
public class HistoryTester {
    private QuotationBuffer buffer;
    private double balance;
    private ArrayList<Position> positions;
    private ArrayList<Position> toClose;
    private int currentIndex;
    private ArrayList<Quotation> bufferAll;

    /** initialisation */
    public HistoryTester(QuotationBuffer buffer){
        this.buffer = buffer;
        balance = START_BALANCE;
        positions = new ArrayList<Position>();
        toClose = new ArrayList<>();
    }

    /** starts and manages test. returns the value of balance after test. */
    public double test(){
        //preparing
        Adviser adviser = new Adviser();
        currentIndex = 100; //0-99 are analysed. 100th is the first to get advice about
        bufferAll = buffer.history;


        while(currentIndex < buffer.countHistory) {
            List<Quotation> buffer100 = bufferAll.subList(currentIndex-100, currentIndex); //in, ex (0-99)
            Quotation currentQuo = bufferAll.get(currentIndex);
            int advice = adviser.getAdvice(buffer100, currentQuo);
            double nextOpen = bufferAll.get(currentIndex + 1).open;
            double curLow = currentQuo.low;
            //handling
            switch (advice) {
                case ADVICE_UP:
                    for (Position pos : positions) {
                        if (pos.direction == DOWN_DIRECTION) {
                            closePosition(pos, 1);
                        }
                    }
                    openPosition(nextOpen, UP_DIRECTION, 100);
                    break;
                case ADVICE_DOWN:
                    for (Position pos : positions) {
                        if (pos.direction == UP_DIRECTION) {
                            closePosition(pos, 1);
                        }
                    }
                    openPosition(nextOpen, DOWN_DIRECTION, 100);
                    break;
                case ADVICE_CLOSE_UP:
                    for (Position pos : positions) {
                        if (pos.direction == UP_DIRECTION) {
                            closePosition(pos, 1);
                        }
                    }
                    break;
                case ADVICE_CLOSE_DOWN:
                    for (Position pos : positions) {
                        if (pos.direction == DOWN_DIRECTION) {
                            closePosition(pos, 1);
                        }
                    }
                    break;
            }
            for (Position pos : positions) {
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
            for (Position pos : toClose) {
                positions.remove(pos);
            }
            toClose.clear();
            currentIndex++;
        }

        bufferAll = null; //buffer.history becomes null
        return balance;
    }

    /** imitates an opening */
    void openPosition(double price, int direction, int money){
        if(balance - money >= 0) {
            balance -= money;
            //informing output
            System.out.print("Position opened at " + price + " (" + (currentIndex + 1) + ")"); //open price of next quo
            if (direction == UP_DIRECTION) {
                System.out.print(" in UP direction.");
            } else {
                System.out.print(" in DOWN direction.");
            }
            System.out.println(" money: " + money + ". balance after opening: " + balance);

            positions.add(new Position(price, direction, money, 100));
        } else {
            System.out.println("not enough money to open position");
        }
    }

    /** imitates a closing */
    private void closePosition(Position posToClose, int cause){    // wrong!!!
        if(currentIndex!=buffer.countHistory) { //right. countHistory from 0 as curIndex.
            double nextOpen = bufferAll.get(currentIndex + 1).open;
            double profit = 1;
            switch (cause){
                case 1: //advice
                    profit = posToClose.profit(nextOpen, nextOpen);
                    break;
                case 2: //stopLoss
                    profit = posToClose.stopLoss;
                    break;
                case 3: //0 money
                    profit = -posToClose.money;
                    break;
                case 4: //takeProfit
                    profit = posToClose.takeProfit;
                    break;
            }
            balance += profit + posToClose.money;
            toClose.add(posToClose);
            System.out.print("Position closed at " + nextOpen + ". was opened at " + posToClose.price + ". Profit: " + profit);
            System.out.println(" balance after closing: " + balance);
        }
    }
}
