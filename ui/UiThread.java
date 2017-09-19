package ui;

import buffer.QuotationBuffer;

import java.util.ArrayList;

/** Main class of the package. Thread for User Interface.
 * Starts and manages main frame.
 * All commands for frame (UI) should be send to UiThread as a manager.
 */
public class UiThread extends Thread {
    MainFrame frame;
    volatile QuotationBuffer buffer;

    /** flag. true when User Interface was generated */
    volatile public boolean frameIsReady;

    /** initialising */
    public UiThread(QuotationBuffer buffer){
        this.buffer = buffer;
        frameIsReady = false;
    }

    /** returns main frame of User Interface */
    public MainFrame getFrame(){
        return frame;
    }

    @Override
    public void run() {
        createGUI();
        Thread.currentThread().setName("graphics");
        frameIsReady = true;
    }

    /** creates and starts a frame */
    private void createGUI(){
        frame = new MainFrame(buffer);
        frame.setVisible(true); //ought to control visibility from main class
    }

    /** managing main frame. draws extremes. */
    public void drawExtremes(ArrayList maxs, ArrayList mins){
        frame.drawExtremes(maxs, mins);
    }

    /** managing main frame. draws resistance lines */
    public void drawResLines(ArrayList resLines){
        frame.drawResLines(resLines);
    }

    public void drawTrendLines(ArrayList trLines) {
        frame.drawTrendLines(trLines);
    }

    /** managing main frame. sets new pos-s instead of old ones. */
    public void setPositions(ArrayList pos){ frame.setPositions(pos);}

    public void setBalance(double balance){ frame.setBalance(balance);}

    public void newBid(){ frame.newBid();}
}
