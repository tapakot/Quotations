package testing;

import advicing.Adviser;
import analysis.Analyser;
import buffer.QuotationBuffer;
import common.*;
import static common.ForexConstants.*;

import java.util.ArrayList;



public class HistoryTester {
    QuotationBuffer buffer;
    double balance;
    ArrayList<Position> positions;

    public HistoryTester(QuotationBuffer buffer){
        this.buffer = buffer;
        balance = START_BALANCE;
        positions = new ArrayList<Position>();
    }

    public double test(){
        //preparing
        Adviser adviser = new Adviser();
        int currentIndex = 100;
        ArrayList<Quotation> buffer100 = buffer.history;
        Quotation currentQuo = buffer.getOne((short)5, currentIndex);
        int advice = adviser.getAdvice(buffer100, currentQuo);
        //handling
        currentIndex++;

        while(currentIndex < buffer.countHistory){
            buffer100.remove(0);
            buffer100.add(currentQuo);
            currentIndex++;
            currentQuo = buffer.getOne((short)5, currentIndex);
            advice = adviser.getAdvice(buffer100, currentQuo);

            if(advice==ADVICE_UP){
                for(Position pos : positions){
                    if(pos.direction == DOWN_DIRECTION){ closePosition(pos);}
                }
                openPosition(buffer.getAsk(), UP_DIRECTION, 10);
            } else if(advice == ADVICE_DOWN){
                for(Position pos : positions){
                    if(pos.direction == UP_DIRECTION){ closePosition(pos);}
                }
                openPosition(buffer.getBid(), DOWN_DIRECTION, 10);
            }
        }
        return balance;
    }

    void openPosition(double price, int direction, int money){
        positions.add(new Position(price, direction, money));
    }

    void closePosition(Position posToClose){
        for(Position pos : positions){
            if(pos==posToClose){
                balance += pos.profit(buffer.getBid(), buffer.getAsk());
                positions.remove(pos);
            }
        }
    }

}
