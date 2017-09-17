package ui;

import buffer.QuotationBuffer;
import common.Quotation;
import testing.AdjustmentTester;
import testing.HistoryTester;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MenuBar extends JMenuBar {


    MenuBar(QuotationBuffer buffer) {
        final QuotationBuffer bufferFinal = buffer;

        JMenu fileMenu = new JMenu("File");

        fileMenu.addSeparator();
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        JMenu testMenu = new JMenu("Test");

        JMenuItem adjTest = new JMenuItem("Adjustment test");
        adjTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdjustmentTester adjTester = new AdjustmentTester();
                adjTester.test();
            }
        });
        testMenu.add(adjTest);

        JMenuItem histTest = new JMenuItem("History test");
        histTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HistoryTester tester = new HistoryTester(bufferFinal);
                System.out.println("==================================balance after history test: " + tester.test());
            }
        });
        testMenu.add(histTest);

        this.add(fileMenu);
        this.add(testMenu);
    }

}
