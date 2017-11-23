package testing;

import advicing.*;
import analysis.AnalyserBuffer;
import buffer.QuotationBuffer;
import common.Position;
import ui.MainFrame;
import ui.RealTimeInformDialog;
import ui.RealTimeInformDialogThread;
import ui.UiThread;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static common.ForexConstants.*;

/** Thread class for real-time test.
 * As an endless event test needs own thread.
 */
class RealTimeTesterThread extends Thread {
    MainFrame ui;
    Adviser adviser;
    volatile QuotationBuffer workingBuffer = null;
    RealTimeTester tester;
    ArrayList<Position> positions;
    HashSet<Position> toClose;
    double balance;
    Date date;
    Date oldDate;
    SimpleDateFormat dtfrmt;

    RealTimeTesterThread(MainFrame ui, RealTimeTester tester, Adviser adviser){
        this.ui = ui;
        this.tester = tester;
        this.adviser = adviser;
        positions = new ArrayList<>();
        toClose = new HashSet<>();
        oldDate = new Date();
        balance = START_BALANCE;
        dtfrmt = new SimpleDateFormat("HH:mm");
    }

    @Override
    public void run(){
        playSound("res\\inform.aiff");
        for(QuotationBuffer buf : adviser.buffers) {
            buf.startRTTest(tester);
        }
        workingBuffer = null;
        Thread.currentThread().setName("real-time test thread");
        System.out.println("started at "+dtfrmt.format(oldDate));
    }

    void stopThread(){
        this.stop();
    }

    synchronized void newData(String instrumentName) {
        workingBuffer=null;
        for(QuotationBuffer buf : adviser.buffers){
            if(buf.name.equals(instrumentName)){
                int  index = adviser.buffers.indexOf(buf);
                workingBuffer = adviser.buffers.get(index);
            }
        }
        date = new Date();
        if ((date.getTime() - oldDate.getTime() > 1000 * 60 * 1)||(positions.size()==0)) { //once in 3 minutes
            int advice = adviser.getAdvice(instrumentName, workingBuffer.getBid());
            switch (advice) {
                case ADVICE_UP:
                    for (Position pos : positions) {
                        if (pos.direction == DOWN_DIRECTION) {
                            if(pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) < -pos.money/10) {
                                closePosition(pos, 1);
                            }
                        }
                    }
                    openPosition(workingBuffer.getBid(), UP_DIRECTION, (int) (balance * PERCENT_OF_BALANCE));
                    oldDate = new Date();
                    break;
                case ADVICE_DOWN:
                    for (Position pos : positions) {
                        if (pos.direction == UP_DIRECTION) {
                            if(pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) < -pos.money/10) {
                                closePosition(pos, 1);
                            }
                        }
                    }
                    openPosition(workingBuffer.getBid(), DOWN_DIRECTION, (int) (balance * PERCENT_OF_BALANCE));
                    oldDate = new Date();
                    break;
                case ADVICE_CLOSE_UP:
                    for (Position pos : positions) {
                        if (pos.direction == UP_DIRECTION) {
                            if(pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) < -pos.money/10) {
                                closePosition(pos, 1);
                            }
                        }
                    }
                    break;
                case ADVICE_CLOSE_DOWN:
                    for (Position pos : positions) {
                        if (pos.direction == DOWN_DIRECTION) {
                            if(pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) < -pos.money/10) {
                                closePosition(pos, 1);
                            }
                        }
                    }
                    break;
            }
        }

        for (Position pos : positions) {
            if(pos.instrument.equals(workingBuffer.name)) {
                //dynamic stopLoss
                if (pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) > 50) {
                    double newStop = pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) - pos.money / STOP_LOSS_DIVIDER;
                    if (newStop > pos.stopLoss) {
                        pos.stopLoss = newStop;
                    }
                }

                if (pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) <= pos.stopLoss) {
                    closePosition(pos, 2);
                    continue;
                }
                if (pos.money + pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) <= 0) {
                    closePosition(pos, 3);
                    continue;
                }
                if (pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) >= pos.takeProfit) {
                    closePosition(pos, 4);
                    continue;
                }
            }
        }

        for (Position pos : toClose) { //close once because toClose is HashSet (no)
            if(pos.instrument.equals(workingBuffer.name)) {
                try {
                    sleep(1000 * 10);
                } catch (InterruptedException e) {
                }
                double profit = 1;
                int cause = 1;
                if (pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) <= pos.stopLoss) {
                    profit = pos.stopLoss;
                    cause = 2;
                } else if (pos.money + pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) <= 0) {
                    profit = -pos.money;
                    cause = 3;
                } else if (pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) >= pos.takeProfit) {
                    profit = pos.takeProfit;
                    cause = 4;
                } else {
                    profit = pos.profit(workingBuffer.getBid(), workingBuffer.getAsk());
                }
                balance += profit + pos.money;
                System.out.print(dtfrmt.format(date) + " Position closed at " + workingBuffer.getBid() + ". was opened at " + pos.price + " " + pos.getTimeSting() + ". Profit: " + profit + ". cause: " + cause);
                System.out.println(" balance after closing: " + balance);
                positions.remove(pos);
            }
        }
        toClose.clear();

        //closing
        for (Position pos : toClose) {
            balance += pos.profit(workingBuffer.getBid(), workingBuffer.getAsk()) + pos.money;
            positions.remove(pos);
        }
        toClose.clear();
        ui.setPositions(positions);
        ui.setBalance(balance);
    }

    synchronized void openPosition(double price, int direction, int money){
        inform(1, new Position(workingBuffer.name, price, direction, money, 100));
        try {
            sleep(1000 * 20);
        } catch(InterruptedException e){}
        if((balance - money >= 0)&&(balance>=50)) {
            balance -= money;
            positions.add(new Position(workingBuffer.name, price, direction, money, 100));
            Date date = new Date();
            System.out.print(dtfrmt.format(date)+ " Position opened at " + workingBuffer.getBid() + ".");
            System.out.println(" balance after opening: " + balance);
        }
    }

    /** imitates a closing */
    synchronized void closePosition(Position posToClose, int cause){
        inform(0, posToClose);
        toClose.add(posToClose);
    }

    void playSound(String path){
        try {
            File soundFile = new File(path);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.setFramePosition(0);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
            exc.printStackTrace();
        }
    }

    synchronized public void inform(int action, Position pos) {
        playSound("res\\inform.aiff");
        RealTimeInformDialogThread dialogThread = new RealTimeInformDialogThread(ui, action, pos);
        dialogThread.start();
    }

}
