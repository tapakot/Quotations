package testing;

import advicing.Adviser;
import analysis.AnalyserBuffer;
import buffer.QuotationBuffer;
import common.Position;
import ui.MainFrame;
import ui.RealTimeInformDialog;
import ui.UiThread;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static common.ForexConstants.*;

/** Thread class for real-time test.
 * As an endless event test needs own thread.
 */
class RealTimeTesterThread extends Thread {
    QuotationBuffer buffer;
    MainFrame ui;
    Adviser advicer;
    AnalyserBuffer anBuffer;
    RealTimeTester tester;
    ArrayList<Position> positions;
    ArrayList<Position> toClose;
    double balance;
    Date date;
    Date oldDate;
    SimpleDateFormat dtfrmt;

    RealTimeTesterThread(QuotationBuffer buffer, MainFrame ui, RealTimeTester tester){
        this.buffer = buffer;
        this.ui = ui;
        this.tester = tester;
        advicer = new Adviser();
        positions = new ArrayList<Position>();
        toClose = new ArrayList<>();
        oldDate = new Date();
        balance = START_BALANCE;
        anBuffer = advicer.getAnBuffer();
        ui.drawExtremes(advicer.getAnBuffer().maximums, advicer.getAnBuffer().minimums);
        ui.drawResLines(advicer.getAnBuffer().exLines);
        ui.drawTrendLines(advicer.getAnBuffer().trendLines);
        ui.repaint();
        dtfrmt = new SimpleDateFormat("HH:mm");
    }

    @Override
    public void run(){
        playSound("res\\inform.aiff");
        buffer.test = true;
        buffer.tester = tester;
        Thread.currentThread().setName("real-time test thread");
        System.out.println("started at "+dtfrmt.format(oldDate));
/*        try{wait();} //???
        catch(InterruptedException e){}*/
    }

    void stopThread(){
        this.stop();
    }

    void newData() {
        date = new Date();
        if ((date.getTime() - oldDate.getTime() > 1000 * 60 * 3)||(positions.size()==0)) { //once in 3 minutes
            int advice = advicer.getAdvice(buffer.getQuotation((short) 5), buffer.getBid());
            switch (advice) {
                case ADVICE_UP:
                    for (Position pos : positions) {
                        if (pos.direction == DOWN_DIRECTION) {
                            if(pos.profit(buffer.getBid(), buffer.getAsk()) < -pos.money/10) {
                                closePosition(pos, 1);
                            }
                        }
                    }
                    openPosition(buffer.getBid(), UP_DIRECTION, (int) (balance * PERCENT_OF_BALANCE));
                    oldDate = new Date();
                    break;
                case ADVICE_DOWN:
                    for (Position pos : positions) {
                        if (pos.direction == UP_DIRECTION) {
                            if(pos.profit(buffer.getBid(), buffer.getAsk()) < -pos.money/10) {
                                closePosition(pos, 1);
                            }
                        }
                    }
                    openPosition(buffer.getBid(), DOWN_DIRECTION, (int) (balance * PERCENT_OF_BALANCE));
                    oldDate = new Date();
                    break;
                case ADVICE_CLOSE_UP:
                    for (Position pos : positions) {
                        if (pos.direction == UP_DIRECTION) {
                            if(pos.profit(buffer.getBid(), buffer.getAsk()) < -pos.money/10) {
                                closePosition(pos, 1);
                            }
                        }
                    }
                    break;
                case ADVICE_CLOSE_DOWN:
                    for (Position pos : positions) {
                        if (pos.direction == DOWN_DIRECTION) {
                            if(pos.profit(buffer.getBid(), buffer.getAsk()) < -pos.money/10) {
                                closePosition(pos, 1);
                            }
                        }
                    }
                    break;
            }
        }

        for (Position pos : positions) {
            //dynamic stopLoss
            if(pos.profit(buffer.getBid(), buffer.getAsk()) > 50){
                double newStop = pos.profit(buffer.getBid(), buffer.getAsk()) - pos.money/STOP_LOSS_DIVIDER;
                if(newStop > pos.stopLoss){
                    pos.stopLoss = newStop;
                }
            }

            if (pos.profit(buffer.getBid(), buffer.getAsk()) <= pos.stopLoss) {
                closePosition(pos, 2);
                continue;
            }
            if (pos.money + pos.profit(buffer.getBid(), buffer.getAsk()) <= 0) {
                closePosition(pos, 3);
                continue;
            }
            if (pos.profit(buffer.getBid(), buffer.getAsk()) >= pos.takeProfit) {
                closePosition(pos, 4);
                continue;
            }
        }

        for (Position pos : toClose) { //close once because toClose is HashSet
            try {
                sleep(1000 * 10);
            } catch(InterruptedException e){}
            double profit = 1;
            int cause = 1;
            if (pos.profit(buffer.getBid(), buffer.getAsk()) <= pos.stopLoss) {
                profit = pos.stopLoss;
                cause = 2;
            } else
            if (pos.money + pos.profit(buffer.getBid(), buffer.getAsk()) <= 0) {
                profit = -pos.money;
                cause = 3;
            } else
            if (pos.profit(buffer.getBid(), buffer.getAsk()) >= pos.takeProfit) {
                profit = pos.takeProfit;
                cause = 4;
            }else{
                profit = pos.profit(buffer.getBid(), buffer.getAsk());
            }
            balance += profit + pos.money;
            System.out.print(dtfrmt.format(date) + " Position closed at " + buffer.getBid() + ". was opened at " + pos.price+" "+ pos.getTimeSting() + ". Profit: " + profit+". cause: "+cause);
            System.out.println(" balance after closing: " + balance);
            positions.remove(pos);
        }
        toClose.clear();

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
        inform(1, new Position(price, direction, money, 100));
        try {
            sleep(1000 * 20);
        } catch(InterruptedException e){}
        if((balance - money >= 0)&&(balance>=50)) {
            balance -= money;
            positions.add(new Position(price, direction, money, 100));
            Date date = new Date();
            System.out.print(dtfrmt.format(date)+ " Position opened at " + buffer.getBid() + ".");
            System.out.println(" balance after opening: " + balance);
        }
    }

    /** imitates a closing */
    void closePosition(Position posToClose, int cause){
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

    public void inform(int action, Position pos) {
        playSound("res\\inform.aiff");
        RealTimeInformDialog dialog = new RealTimeInformDialog(ui, action, pos);
        dialog.setVisible(true);
    }

}
