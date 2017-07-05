package ui;

import buffer.QuotationBuffer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/** Main frame of User Interface.
 * Manages all panes.
 * All commands for panes (such as to draw or change info) should be send to MainFrame as a manager.
 */
public class MainFrame extends JFrame{
    static QuotationBuffer buffer;
    static GraphCanvas graphCanvas;

    /** preparations and start.
     * sets panes, Layout Manager and size.
     */
    public MainFrame(QuotationBuffer buffer){
        super("Quotations");

        MainFrame.buffer = buffer;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.setMinimumSize(new Dimension(750, 400));
        super.setPreferredSize(getMinimumSize());

        graphCanvas = new GraphCanvas();
        mainPanel.add(graphCanvas);

        setContentPane(mainPanel);
        pack();
    }

    /** sets a Quotation Buffer to get data from (for drawing) */
    public void setQuotationBuffer(QuotationBuffer buffer){
        MainFrame.buffer = buffer;
    }

    /** informs about new data.
     * repaints with new quotations.
     */
    public void realTimeEvent(){
        graphCanvas.Quotations.remove(0);
        graphCanvas.Quotations.add(buffer.getQuotation((short)5, 99));
        graphCanvas.repaint();
    }

    /** managing graph canvas. draws extremes. */
    public void drawExtremes(ArrayList maxs, ArrayList mins){
        graphCanvas.drawExtremes(maxs, mins);
    }

    /** managing graph canvas. draws resistance lines */
    public void drawResLines(ArrayList resLines){
        graphCanvas.drawResLines(resLines);
    }
}
