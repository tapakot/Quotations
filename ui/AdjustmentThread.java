package ui;

public class AdjustmentThread extends Thread {
    MainFrame owner;

    public AdjustmentThread(MainFrame frameOwner){
        owner = frameOwner;
    }

    @Override
    public void run(){
        Thread.currentThread().setName("adjustment");
        AdjustmentDialog adjustmentDialog = new AdjustmentDialog(owner);
        adjustmentDialog.setVisible(true);
    }
}
