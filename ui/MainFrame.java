package ui;

import analysis.InnerTrendLine;
import analysis.MovingAverage;
import buffer.QuotationBuffer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import static common.ForexConstants.HIST_COUNT;
import static javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT;

/** Main frame of User Interface.
 * Manages all panes.
 * All commands for panes (such as to draw or change info) should be send to MainFrame as a manager.
 */
public class MainFrame extends JFrame{
    static QuotationBuffer buffer;
    static ArrayList<QuotationBuffer> buffers;
    static ArrayList<GraphCanvas> canvases;


    Box center;
    JTabbedPane instrumentPane;
    static GraphCanvas graphCanvas;
    static PositionsPanel posPanel;

    /** preparations and start.
     * sets panes, Layout Manager and size.
     */
    public MainFrame(QuotationBuffer buffer){
        super("Quotations");

        canvases = new ArrayList<>();
        buffers = new ArrayList<>();
        buffers.add(buffer);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.setMinimumSize(new Dimension(1000, 400));
        super.setPreferredSize(getMinimumSize());

        MenuBar menuBar = new MenuBar(buffer, this);
        setJMenuBar(menuBar);

        center = Box.createHorizontalBox();

        instrumentPane = new JTabbedPane(JTabbedPane.LEFT, SCROLL_TAB_LAYOUT);
        center.add(instrumentPane);
        addBuffer(buffer);

        posPanel = new PositionsPanel(buffer);
        center.add(posPanel);

        mainPanel.add(center);

        setContentPane(mainPanel);
        pack();
    }

    /** informs about new data.
     * repaints with new quotations.
     */
    public void realTimeEvent(String nameOfBuffer, int counter){
        //find buffer and index
        /*int indexOfBuffer = -1;
        for(QuotationBuffer buffer : buffers){
            if(buffer.name.equals(nameOfBuffer)){
                indexOfBuffer = buffers.indexOf(buffer);
            }
        }*/
        //foreach
        for (GraphCanvas gc : canvases){
            if(gc.buffer.name.equals(nameOfBuffer)) {
                gc.realTimeEvent(counter);
            }
        }
    }

    public void addBuffer(QuotationBuffer buffer){
        buffers.add(buffer);
        JTabbedPane periodPane = new JTabbedPane(JTabbedPane.TOP, SCROLL_TAB_LAYOUT);
        graphCanvas = new GraphCanvas(buffer, 5);
        periodPane.addTab("5", graphCanvas);
        canvases.add(graphCanvas);
        graphCanvas = new GraphCanvas(buffer, 15);
        periodPane.addTab("15", graphCanvas);
        canvases.add(graphCanvas);
        graphCanvas = new GraphCanvas(buffer, 30);
        periodPane.addTab("30", graphCanvas);
        canvases.add(graphCanvas);
        graphCanvas = new GraphCanvas(buffer, 60);
        periodPane.addTab("hour", graphCanvas);
        canvases.add(graphCanvas);
        graphCanvas = new GraphCanvas(buffer, 1440);
        periodPane.addTab("day", graphCanvas);
        canvases.add(graphCanvas);
        instrumentPane.addTab(buffer.name, periodPane);
        validate();
        repaint();
    }

    /** managing graph canvas. draws extremes. */
    public void drawExtremes(ArrayList maxs, ArrayList mins){
        for(GraphCanvas gc : canvases) {
            gc.drawExtremes(maxs, mins);
        }
    }

    /** managing graph canvas. draws resistance lines */
    public void drawResLines(ArrayList resLines){
        for(GraphCanvas gc : canvases) {
            gc.drawResLines(resLines);
        }
    }

    public void drawTrendLines(ArrayList trLines) {
        for (GraphCanvas gc : canvases) {
            gc.drawTrendLines(trLines);
        }
    }

    public void drawTDSequences(ArrayList tdSequences) {
        for(GraphCanvas gc : canvases) {
            gc.drawTDSequences(tdSequences);
        }
    }

    public void drawInnerLine(InnerTrendLine innerLine) {
        for (GraphCanvas gc : canvases) {
            gc.drawInnerLine(innerLine);
        }
    }

    public void drawMA(ArrayList<MovingAverage> movingAverages) {
        for(GraphCanvas gc : canvases) {
            gc.drawMA(movingAverages);
        }
    }

    /** managing panel of positions. sets new pos-s instead of old ones. */
    public void setPositions(ArrayList pos){ posPanel.setPositions(pos); }

    public void setBalance(double balance){ posPanel.setBalance(balance); }

    public void newBid(){ posPanel.reCalc();}
}
