package testing;

import advicing.Adviser;
import buffer.QuotationBuffer;
import common.Position;
import ui.UiThread;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;

import static common.ForexConstants.*;

/** Thread class for real-time test.
 * As an endless event test needs own thread.
 */
class RealTimeTesterThread extends Thread {
    QuotationBuffer buffer;
    UiThread ui;
    Adviser advicer;
    RealTimeTester tester;
    ArrayList<Position> positions;
    ArrayList<Position> toClose;
    double balance;
    Date date;
    Date oldDate;

    RealTimeTesterThread(QuotationBuffer buffer, UiThread ui, RealTimeTester tester){
        this.buffer = buffer;
        this.ui = ui;
        this.tester = tester;
        advicer = new Adviser();
        positions = new ArrayList<Position>();
        toClose = new ArrayList<>();
        oldDate = new Date(0);
        balance = START_BALANCE;
    }

    @Override
    public void run(){
        buffer.test = true;
        buffer.tester = tester;
/*        try{wait();} //???
        catch(InterruptedException e){}*/
    }

    void stopThread(){
        this.stop();
    }

    void newData() {
        date = new Date();
        if (date.getTime() - oldDate.getTime() > 1000 * 30) { //once in 30 seconds
            int advice = advicer.getAdvice(buffer.getQuotation((short) 5), buffer.getBid());
            if (advice == ADVICE_UP) {
                for (Position pos : positions) {
                    if (pos.direction == DOWN_DIRECTION) {
                        closePosition(pos);
                    }
                }
                openPosition(buffer.getBid(), UP_DIRECTION, 30);
                oldDate = date;
            } else if (advice == ADVICE_DOWN) {
                for (Position pos : positions) {
                    if (pos.direction == UP_DIRECTION) {
                        closePosition(pos);
                    }
                }
                openPosition(buffer.getBid(), DOWN_DIRECTION, 30);
                oldDate = date;
            } else if (advice == ADVICE_CLOSE_DOWN) {
                for (Position pos : positions) {
                    if (pos.direction == DOWN_DIRECTION) {
                        closePosition(pos);
                    }
                }
            } else if (advice == ADVICE_CLOSE_UP) {
                for (Position pos : positions) {
                    if (pos.direction == UP_DIRECTION) {
                        closePosition(pos);
                    }
                }
            }
        }
        for (Position pos : positions) {
            if (pos.profit(buffer.getBid(), buffer.getAsk()) > TAKE_PROFIT) {
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
        for (Position pos : toClose) {
            balance += pos.profit(buffer.getBid(), buffer.getAsk()) + pos.money;
            positions.remove(pos);
        }
        toClose.clear();
        ui.setPositions(positions);
        ui.setBalance(balance);
    }

    void openPosition(double price, int direction, int money){
        if(balance>=money) {
            balance -= money;
            positions.add(new Position(price, direction, money, 100));
            System.out.print("Position opened at " + buffer.getBid() + ".");
            System.out.println(" balance after opening: " + balance);
        }
    }

    /** imitates a closing */
    void closePosition(Position posToClose){
        toClose.add(posToClose);
        System.out.print("Position closed at "+ buffer.getBid()+". was opened at "+ posToClose.price+". Profit: "+posToClose.profit(buffer.getBid(), buffer.getAsk()));
        System.out.println(" balance after closing: "+ balance);
    }

}
