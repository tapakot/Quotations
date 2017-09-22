package ui;

import common.AdjustValue;
import common.ForexConstants;
import common.Settings;
import testing.HistoryTester;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class AdjustmentDialog extends JDialog implements ActionListener{
    MainFrame owner;
    ArrayList<JTextField> startsF, finishesF, stepsF;
    ArrayList<Double> starts, finishes, steps;
    double bestValues[], curValues[];
    double bestResult;
    JButton cancelButton, startButton;

    AdjustmentDialog(MainFrame ownerFrame){
        super(ownerFrame, "Adjustment Test", false);
        owner = ownerFrame;

        starts = new ArrayList<>();
        finishes = new ArrayList<>();
        steps = new ArrayList<>();
        startsF = new ArrayList<>();
        finishesF = new ArrayList<>();
        stepsF = new ArrayList<>();
        bestValues = new double[Settings.adjustValues.size()];
        curValues = new double[Settings.adjustValues.size()];

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setMaximumSize(new Dimension(screen.width, screen.height));
        setMinimumSize(new Dimension(650, 250));
        setPreferredSize(getMinimumSize());

        Box mainBox = Box.createVerticalBox();

        JPanel headPanel = new JPanel(new GridLayout(1, 4, 0, 10));
        headPanel.add(new JLabel("Name"));
        headPanel.add(new JLabel("Start"));
        headPanel.add(new JLabel("Finish"));
        headPanel.add(new JLabel("Step"));
        mainBox.add(headPanel);
        mainBox.add(Box.createVerticalGlue());

        for(AdjustValue adjValue : Settings.adjustValues){
            JPanel panel = new JPanel(new GridLayout(1, 4, 0, 50));
            JLabel nameLabel = new JLabel(adjValue.name);
            panel.add(nameLabel);
            JTextField startField = new JTextField(Double.toString(adjValue.defValue));
            panel.add(startField);
            startsF.add(startField);
            JTextField finishField = new JTextField(Double.toString(adjValue.defValue));
            panel.add(finishField);
            finishesF.add(finishField);
            JTextField stepField = new JTextField("0");
            panel.add(stepField);
            stepsF.add(stepField);
            mainBox.add(panel);
            mainBox.add(Box.createVerticalGlue());
        }

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 0, 100));
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        buttonPanel.add(startButton);
        mainBox.add(buttonPanel);

        this.add(mainBox);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==cancelButton){
            dispose();
        }else
        if(e.getSource()==startButton){
            starts.clear();
            finishes.clear();
            steps.clear();
            for(JTextField stF : startsF){ starts.add(Double.parseDouble(stF.getText())); }
            for(JTextField finF : finishesF){ finishes.add(Double.parseDouble(finF.getText())); }
            for(JTextField stepF : stepsF){ steps.add(Double.parseDouble(stepF.getText())); }
            bestResult = 0;
            for(int i = 0; i<Settings.adjustValues.size(); i++){
                bestValues[i] = 0;
                curValues[i] = 0;
            }
            test(); //bestValues changed
            AdjustmentResultDialog resultDialog = new AdjustmentResultDialog(this);
            resultDialog.setVisible(true);
        }
    }

    void test(){
        doAdj(0);
        ForexConstants.applySettings();
    }

    void doAdj(int index){
        for(double i = starts.get(index); i <= finishes.get(index); i+=steps.get(index)){
            curValues[index] = i;
            //change prop
            String name = Settings.adjustValues.get(index).name;
            String sec = Settings.adjustValues.get(index).section;
            Settings.properties.setProperty("adjustable."+name, Double.toString(i));
            ForexConstants.applyAdjustmentValues();

            if(index == starts.size()-1){
                HistoryTester tester = new HistoryTester(MainFrame.buffer);
                double result = tester.test();
                if(result>bestResult) {
                    for(int j = 0; j<Settings.adjustValues.size(); j++){
                        bestValues[j] = curValues[j];
                        bestResult = result;
                        Settings.adjustValues.get(j).defValue = curValues[j];
                    }
                }
                for(int j = 0; j<Settings.adjustValues.size(); j++) {
                    System.out.print(curValues[j] + "    ");
                }
                System.out.println(result);
            } else {
                doAdj(index+1);
            }
            if(steps.get(index)==0){ break;}
        }
    }
}
