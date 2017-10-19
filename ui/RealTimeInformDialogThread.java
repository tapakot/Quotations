package ui;

import common.Position;

public class RealTimeInformDialogThread extends Thread{
    MainFrame owner;
    int action;
    Position pos;

    public RealTimeInformDialogThread(MainFrame owner, int action, Position pos){
        this.owner = owner;
        this.action = action;
        this.pos = pos;
    }

    @Override
    public void run() {
        RealTimeInformDialog dialog = new RealTimeInformDialog(owner, action, pos);
        dialog.setVisible(true);
    }
}
