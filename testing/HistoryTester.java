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
    QuotationBuffer buffer;
    double balance;
    ArrayList<Position> positions;
    ArrayList<Position> toClose;
    int currentIndex;
    ArrayList<Quotation> bufferAll;

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
        currentIndex = 100;//100
        bufferAll = buffer.history;
        List<Quotation> buffer100 = bufferAll.subList(currentIndex-100, currentIndex);//in, ex
        Quotation currentQuo = bufferAll.get(currentIndex);

        int advice = adviser.getAdvice(buffer100, currentQuo);
        //handling
        if(advice==ADVICE_UP){
            for(Position pos : positions){
                if(pos.direction == DOWN_DIRECTION){ closePosition(pos);}
            }
            openPosition(bufferAll.get(currentIndex+1).open, UP_DIRECTION, 100);
        } else if(advice == ADVICE_DOWN){
            for(Position pos : positions){
                if(pos.direction == UP_DIRECTION){ closePosition(pos);}
            }
            openPosition(bufferAll.get(currentIndex+1).open, DOWN_DIRECTION, 100);
        } else if(advice == ADVICE_CLOSE_DOWN){
            for(Position pos : positions){
                if(pos.direction == DOWN_DIRECTION){ closePosition(pos);}
            }
        } else if(advice == ADVICE_CLOSE_UP) {
            for (Position pos : positions) {
                if (pos.direction == UP_DIRECTION) {closePosition(pos);}
            }
        }
        for(Position pos : toClose){
            positions.remove(pos);
        }
        toClose.clear();

        while(currentIndex < buffer.countHistory-1){
            currentIndex++;
            buffer100 = bufferAll.subList(currentIndex-100, currentIndex);
            currentQuo = bufferAll.get(currentIndex);
            /* test
            if(bufferAll.get(currentIndex+1).open == 1.13882){
                int x = 1;
            }*/
            advice = adviser.getAdvice(buffer100, currentQuo);

            if(advice==ADVICE_UP){
                for(Position pos : positions){
                    if(pos.direction == DOWN_DIRECTION){ closePosition(pos);}
                }
                openPosition(bufferAll.get(currentIndex+1).open, UP_DIRECTION, 30);
            } else if(advice == ADVICE_DOWN){
                for(Position pos : positions){
                    if(pos.direction == UP_DIRECTION){ closePosition(pos);}
                }
                openPosition(bufferAll.get(currentIndex+1).open, DOWN_DIRECTION, 30);
            } else if(advice == ADVICE_CLOSE_DOWN){
                for(Position pos : positions){
                    if(pos.direction == DOWN_DIRECTION){ closePosition(pos);}
                }
            } else if(advice == ADVICE_CLOSE_UP) {
                for (Position pos : positions) {
                    if (pos.direction == UP_DIRECTION) {closePosition(pos);}
                }
            }
            for (Position pos : positions) {
                if (pos.profit(currentQuo.close, currentQuo.close) > TAKE_PROFIT) {
                    closePosition(pos);
                }
                if (pos.profit(buffer.getBid(), buffer.getAsk()) > pos.takeProfit) {
                    closePosition(pos);
                }
                if (pos.money + pos.profit(buffer.getBid(), buffer.getAsk()) <=0  ){
                    closePosition(pos);
                }
            }
            //closing
            for(Position pos : toClose){
                positions.remove(pos);
            }
            toClose.clear();
            //if(balance<=0){ return balance; }
        }
        return balance;
    }

    /** imitates an opening */
    void openPosition(double price, int direction, int money){
        balance -= money;
        System.out.print("Position opened at " + price+ " ("+(currentIndex+1)+")");
        if(direction == UP_DIRECTION){System.out.print(" in UP direction.");}
        else {System.out.print(" in DOWN direction.");}
        System.out.println(" money: "+ money+ ". balance after opening: "+ balance);
        positions.add(new Position(price, direction, money, 100));


    }

    /** imitates a closing */
    void closePosition(Position posToClose){    // wrong!!!
        if(currentIndex!=buffer.countHistory) {
            double nextOpen = bufferAll.get(currentIndex + 1).open;
            balance += posToClose.profit(nextOpen, nextOpen) + posToClose.money;
            toClose.add(posToClose);
            System.out.print("Position closed at " + nextOpen + ". was opened at " + posToClose.price + ". Profit: " + posToClose.profit(nextOpen, nextOpen));
            System.out.println(" balance after closing: " + balance);
        }
    }
}
