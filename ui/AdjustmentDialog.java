package ui;

import javax.swing.*;

class AdjustmentDialog extends JDialog {
    MainFrame owner;

    AdjustmentDialog(MainFrame ownerFrame){
        super(ownerFrame, "Adjustment Test", false);
        owner = ownerFrame;


    }
}
