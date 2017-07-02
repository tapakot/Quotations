package testing;

import advicing.Adviser;
import analysis.Analyser;
import buffer.QuotationBuffer;
import common.*;
import static common.ForexConstants.*;

import java.util.ArrayList;
import java.util.List;


public class HistoryTester {
    QuotationBuffer buffer;
    double balance;
    ArrayList<Position> positions;
    ArrayList<Position> toClose;

    public HistoryTester(QuotationBuffer buffer){
        this.buffer = buffer;
        balance = START_BALANCE;
        positions = new ArrayList<Position>();
        toClose = new ArrayList<>();
    }

    public double test(){
        //preparing
        Adviser adviser = new Adviser();
        int currentIndex = 100;//100
        ArrayList<Quotation> bufferAll = buffer.history;
        List<Quotation> buffer100 = bufferAll.subList(currentIndex-100, currentIndex);//in, ex
        Quotation currentQuo = bufferAll.get(currentIndex);
        int advice = adviser.getAdvice(buffer100, currentQuo);
        //handling
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

        while(currentIndex < buffer.countHistory){
            currentIndex++;
            buffer100 = bufferAll.subList(currentIndex-100, currentIndex);
            currentQuo = bufferAll.get(currentIndex);
            advice = adviser.getAdvice(buffer100, currentQuo);

            if(advice==ADVICE_UP){
                for(Position pos : positions){
                    if(pos.direction == DOWN_DIRECTION){ closePosition(pos);}
                }
                openPosition(currentQuo.close, UP_DIRECTION, 10);
            } else if(advice == ADVICE_DOWN){
                for(Position pos : positions){
                    if(pos.direction == UP_DIRECTION){ closePosition(pos);}
                }
                openPosition(currentQuo.close, DOWN_DIRECTION, 10);
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
                if (pos.profit(buffer.getBid(), buffer.getAsk()) > 1) {closePosition(pos);}
            }
            //closing
            for(Position pos : toClose){
                positions.remove(pos);
            }
            toClose.clear();
        }
        return balance;
    }

    void openPosition(double price, int direction, int money){
        positions.add(new Position(price, direction, money));
    }

    void closePosition(Position posToClose){
        balance += posToClose.profit(buffer.getBid(), buffer.getAsk());
        toClose.add(posToClose);
    }
}
