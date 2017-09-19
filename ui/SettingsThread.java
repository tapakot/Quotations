package ui;

import javax.swing.*;

public class SettingsThread extends Thread {
    MainFrame owner;

    public SettingsThread(MainFrame frameOwner){
        owner = frameOwner;
    }

    @Override
    public void run(){
        Thread.currentThread().setName("settings");
        SettingsDialog settingsDialog = new SettingsDialog(owner);
        settingsDialog.setVisible(true);
    }
}
