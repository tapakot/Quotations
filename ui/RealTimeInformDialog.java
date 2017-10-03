package ui;

import common.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import static common.ForexConstants.UP_DIRECTION;

public class RealTimeInformDialog extends JDialog implements ActionListener{
    JButton okButton;

    public RealTimeInformDialog(MainFrame owner, int action, Position pos){
        super(owner,"inform", true);
        SimpleDateFormat dtfrmt = new SimpleDateFormat("HH:mm");
        String message = "";
        if(action == 0){
            message+="Close ";
            if(pos.direction == UP_DIRECTION){
                message+=" UP";
            } else {
                message+=" DOWN";
            }
            message+=" opened at " + dtfrmt.format(pos.date);
        } else {
            message+="Open ";
            if(pos.direction == UP_DIRECTION){
                message+=" UP";
            } else {
                message+=" DOWN";
            }
            message+=" with "+pos.money;
        }

        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JLabel messageL = new JLabel(message);
        mainPanel.add(messageL);
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        mainPanel.add(okButton);
        add(mainPanel);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == okButton) {
            dispose();
        }
    }
}
