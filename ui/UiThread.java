package ui;

import buffer.QuotationBuffer;

public class UiThread extends Thread {
    MainFrame frame;
    volatile QuotationBuffer buffer;
    volatile public boolean frameIsReady;

    public UiThread(QuotationBuffer buffer){
        this.buffer = buffer;
        frameIsReady = false;
    }

    public MainFrame getFrame(){
        return frame;
    }

    @Override
    public void run() {
        createGUI();
        frameIsReady = true;
    }


    private void createGUI(){
        frame = new MainFrame(buffer);
        frame.setVisible(true); //ought to control visibility from main class
    }
}
