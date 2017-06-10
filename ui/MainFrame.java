package ui;

import buffer.QuotationBuffer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame{
    static QuotationBuffer buffer;
    static GraphCanvas graphCanvas;

    public MainFrame(QuotationBuffer buffer){
        super("Quotations");

        MainFrame.buffer = buffer;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.setMinimumSize(new Dimension(600, 400));
        super.setPreferredSize(getMinimumSize());

        graphCanvas = new GraphCanvas();
        mainPanel.add(graphCanvas);

        setContentPane(mainPanel);
        pack();
    }

    public void setQuotationBuffer(QuotationBuffer buffer){
        MainFrame.buffer = buffer;
    }

    public void realTimeEvent(){
        graphCanvas.Quotations.remove(0);
        graphCanvas.Quotations.add(buffer.getQuotation((short)5, 99));
        graphCanvas.repaint();
    }

}
