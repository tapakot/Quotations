package ui;

import common.Settings;

import javax.swing.*;
import java.awt.*;

class AdjustmentResultDialog extends JDialog {
    AdjustmentDialog owner;

    AdjustmentResultDialog(AdjustmentDialog owner){
        super(owner, "result", true);
        this.owner = owner;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel(new GridLayout(Settings.adjustValues.size()+1, 1, 10, 10));

        for(int i=0; i<owner.bestValues.length; i++){
            mainPanel.add(new JLabel(Double.toString(owner.bestValues[i])));
        }
        mainPanel.add(new JLabel(Double.toString(owner.bestResult)));

        add(mainPanel);
        pack();
    }
}
