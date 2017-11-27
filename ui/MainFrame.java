package ui;

import advicing.Adviser;
import analysis.Analyser;
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
    static Analyser analyser;
    static Adviser adviser;


    Box center;
    JTabbedPane instrumentPane;
    static GraphCanvas graphCanvas;
    static OscillatorCanvas osCanvas;
    static PositionsPanel posPanel;
    static Box graphicBox;

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
        super.setMinimumSize(new Dimension(1000, 800));

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
        osCanvas = new OscillatorCanvas(graphCanvas);
        graphicBox = Box.createVerticalBox();
        graphicBox.add(graphCanvas);
        graphicBox.add(osCanvas);
        periodPane.addTab("5", graphicBox);
        canvases.add(graphCanvas);

        graphCanvas = new GraphCanvas(buffer, 15);
        osCanvas = new OscillatorCanvas(graphCanvas);
        graphicBox = Box.createVerticalBox();
        graphicBox.add(graphCanvas);
        graphicBox.add(osCanvas);
        periodPane.addTab("15", graphicBox);
        canvases.add(graphCanvas);

        graphCanvas = new GraphCanvas(buffer, 30);
        osCanvas = new OscillatorCanvas(graphCanvas);
        graphicBox = Box.createVerticalBox();
        graphicBox.add(graphCanvas);
        graphicBox.add(osCanvas);
        periodPane.addTab("30", graphicBox);
        canvases.add(graphCanvas);

        graphCanvas = new GraphCanvas(buffer, 60);
        osCanvas = new OscillatorCanvas(graphCanvas);
        graphicBox = Box.createVerticalBox();
        graphicBox.add(graphCanvas);
        graphicBox.add(osCanvas);
        periodPane.addTab("hour", graphicBox);
        canvases.add(graphCanvas);

        graphCanvas = new GraphCanvas(buffer, 1440);
        osCanvas = new OscillatorCanvas(graphCanvas);
        graphicBox = Box.createVerticalBox();
        graphicBox.add(graphCanvas);
        graphicBox.add(osCanvas);
        periodPane.addTab("day", graphicBox);
        canvases.add(graphCanvas);

        instrumentPane.addTab(buffer.name, periodPane);
        validate();
        repaint();
    }

    public void setAnalyser(Analyser an){
        analyser = an;
    }

    public void setAdviser(Adviser ad){
        adviser = ad;
    }

    public void linkAll(){
        for(GraphCanvas gc : canvases){
            gc.findAnBuffer();
        }
    }

    /** managing graph canvas. draws extremes. */
    public void drawExtremes(boolean draw){
        for(GraphCanvas gc : canvases) {
            gc.drawExtremes(draw);
        }
    }

    /** managing graph canvas. draws resistance lines */
    public void drawResLines(boolean draw){
        for(GraphCanvas gc : canvases) {
            gc.drawResLines(draw);
        }
    }

    public void drawTrendLines(boolean draw) {
        for (GraphCanvas gc : canvases) {
            gc.drawTrendLines(draw);
        }
    }

    public void drawTDSequences(boolean draw) {
        for(GraphCanvas gc : canvases) {
            gc.drawTDSequences(draw);
        }
    }

    public void drawInnerLine(boolean draw) {
        for (GraphCanvas gc : canvases) {
            gc.drawInnerLine(draw);
        }
    }

    public void drawMA(boolean draw) {
        for(GraphCanvas gc : canvases) {
            gc.drawMA(draw);
        }
    }

    /** managing panel of positions. sets new pos-s instead of old ones. */
    public void setPositions(ArrayList pos){ posPanel.setPositions(pos); }

    public void setBalance(double balance){ posPanel.setBalance(balance); }

    public void newBid(){ posPanel.reCalc();}
}
