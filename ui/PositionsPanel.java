package ui;

import buffer.QuotationBuffer;
import common.Quotation;
import static common.ForexConstants.*;

import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.util.ArrayList;

/** Part of the User Interface which shows all opened positions.
 * Manages all output related to positions.
 */
class PositionsPanel extends JPanel {
    private ArrayList<common.Position> positions;
    private JPanel panelOfPositions;
    QuotationBuffer buffer;
    double balance;

    PositionsPanel(QuotationBuffer buffer){ //buffer to calc profit (bid,ask)
        this.buffer = buffer;
        panelOfPositions = null;
        positions = null;
        setMinimumSize(new Dimension(500, 200));
        setMaximumSize(new Dimension(300, 1000));
        setPreferredSize(getMinimumSize());

        balance = START_BALANCE;
        compose();
    }

    void setPositions(ArrayList<common.Position> positions){
        this.positions = positions;
        panelOfPositions = new JPanel(new GridLayout(positions.size()+1, 5, 3, 10));

        //header
        panelOfPositions.add(new JLabel("price"));
        panelOfPositions.add(new JLabel("dir-on"));
        panelOfPositions.add(new JLabel("spend"));
        panelOfPositions.add(new JLabel("time"));
        panelOfPositions.add(new JLabel("profit"));


        for(common.Position pos : positions){
            JLabel priceLabel = new JLabel(Double.toString(pos.price));
            JLabel directionLabel = new JLabel(pos.getDirectionString());
            JLabel profitLabel = new JLabel("$"+Double.toString(pos.profit(buffer.getBid(), buffer.getAsk()))); //only with opened MT4
            JLabel timeLabel = new JLabel(pos.getTimeSting());
            JLabel moneyLabel = new JLabel("$"+Double.toString(pos.money));
            panelOfPositions.add(priceLabel);
            panelOfPositions.add(directionLabel);
            panelOfPositions.add(moneyLabel);
            panelOfPositions.add(timeLabel);
            panelOfPositions.add(profitLabel);
        }

        compose();
    }

    void setBalance(double balance){
        this.balance = balance;
    }

    void reCalc (){
        setPositions(positions);
    }

    void compose(){
        Box box = Box.createVerticalBox();
        JLabel header = new JLabel("OPENED");
        JLabel bal = new JLabel("balance: "+balance);
        box.add(header);

        if(panelOfPositions != null){
            box.add(panelOfPositions);
        }
        box.add(bal);

        removeAll();
        add(box);
        super.revalidate();
        super.repaint();
    }
}
