package ui;

import analysis.ResistanceLine;
import analysis.TrendLine;
import buffer.QuotationBuffer;
import common.Quotation;
import static common.ForexConstants.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** Canvas where graphics are drawn.
 * Manages all painting on graphs.
 */
class GraphCanvas extends JPanel{
    Graphics2D g2d;
    QuotationBuffer buffer;
    /** 100 last quotations (to draw) */
    ArrayList<Quotation> Quotations; //last
    ArrayList<Double> maxs;
    ArrayList<Double> mins;
    ArrayList<ResistanceLine> resLines;
    ArrayList<TrendLine> trLines;

    //1 pip = 0,00001;
    int height; //height of the visible canvas
    int width; //excludind space for quos
    int countOfBars;
    double center; // central quo
    double averageBar; //in pips
    double capacity; //in pips. capacity of height of the graph
    double frequencyOfMarks; //in pips
    double lengthOfPip; //in pixels
    double base; //in pips. bottom of the graph
    int barPeriod;
    int spaceBetweenBars;

    //what to draw
    /** flag. to draw or not */
    boolean extremes_f;
    /** flag. to draw or not */
    boolean resLines_f;
    boolean trLines_f;

    /** initialisation */
    GraphCanvas(QuotationBuffer buffer){
        setMinimumSize(new Dimension(700, 400));
        setPreferredSize(getMinimumSize());
        this.buffer = buffer;
        Quotations = new ArrayList<>();
        countOfBars = 99;
        for(int i= 0; i<countOfBars; i++){
            // should be changed by realTimeEvent
            Quotations.add(buffer.getQuotation((short)5, 100-countOfBars-1+i)); //in the end but not the last one (unpredictable)
        }

        extremes_f = false;
        resLines_f = false;

        /*for(int i = 0; i<30; i++){
            System.out.println("Graph["+ i +"]: "+ Quotations.get(i));
        }*/
    }

    /** paints everything on the separate graph. depends on flags *_f. */
    @Override
    protected void paintComponent(Graphics g) {
        height = getHeight();
        width = getWidth() - 80; //width of Graph excluding space for quotations
        g2d = (Graphics2D) g; //100% safe
        super.paintComponent(g2d);

        //calculating variables for painting:
        double sum;
        //center
        sum = 0;
        for(int i = 0; i<countOfBars; i++){
            sum += (Quotations.get(i).high + Quotations.get(i).low)/2;
        }
        center = sum/countOfBars;
        //averageBar
        sum = 0;
        for(int i = 0; i<countOfBars; i++){
            sum += (Quotations.get(i).high - Quotations.get(i).low);
        }
        averageBar = sum/countOfBars;
        //capacity
        capacity = averageBar/0.08;
        //frequencyOfMarks
        frequencyOfMarks = averageBar/2;
        //lengthOfPip
        lengthOfPip = height/ (capacity*100000);
        //base
        base = center - (capacity/2);
        //spaceBetweenBars
        spaceBetweenBars = (width - WIDTH_OF_BAR*countOfBars)/(countOfBars+1);
        //barPeriod
        barPeriod = spaceBetweenBars + WIDTH_OF_BAR;
        /*
        //rounding (significantly affects the accuracy)
        center = round(center, 5);
        averageBar = round(averageBar, 5);
        capacity = round(capacity, 5);
        frequencyOfMarks = round(frequencyOfMarks, 5);
        lengthOfPip = round(lengthOfPip, 5);
        base = round(base, 5);*/
        //testing
        System.out.println("height: "+height);
        System.out.println("center: "+center);
        System.out.println("averageBar: "+averageBar);
        System.out.println("capacity: "+capacity);
        System.out.println("frequencyOfMarks: "+frequencyOfMarks);
        System.out.println("lengthOfPip: "+lengthOfPip);
        System.out.println("base: "+base);

        this.setBackground(Color.gray);

        g2d.setColor(Color.black);
        g2d.drawLine(width, 0, width, height); //separator between graph and quos

        //resistance lines
        if(resLines_f){
            g2d.setColor(Color.BLUE);
            for(ResistanceLine line : resLines){
                g2d.fillRect(0, getY(line.high), width, getY(line.low)-getY(line.high));
            }
        }

        //marks and digits on the separator; grid
        g2d.setColor(Color.BLACK);
        g2d.drawLine(width-5, height/2, width+5, height/2);
        //up
        double value = center;
        while (getY(value) > 0){
            int x = width;      //center of the grid mark
            g2d.drawString(Double.toString(round(value, 5)),  x+7, getY(value)+5);
            while(x >=0) {
                g2d.drawLine(x - 5, getY(value), x + 5, getY(value));
                x-=gridPeriod;
            }
            value += frequencyOfMarks;
        }
        //down
        value = center;
        while (getY(value) < height){
            int x = width;
            g2d.drawString(Double.toString(round(value, 5)),  x+7, getY(value)+5);
            while(x >=0){
                g2d.drawLine(x-5, getY(value), x+5, getY(value));
                x-=gridPeriod;
            }
            value-=frequencyOfMarks;
        }

        //bars
        for(int i = 0; i<countOfBars; i++){
            int x = spaceBetweenBars + (i*barPeriod);
            double open = Quotations.get(i).open;
            double close = Quotations.get(i).close;
            double high = Quotations.get(i).high;
            double low = Quotations.get(i).low;
            int heightOfBar = Math.abs(getY(high) - getY(low));

            if(close >= open){
                g2d.setColor(Color.green);
            } else {
                g2d.setColor(Color.red);
            }

            g2d.fillRect(x, getY(open)-1, 2, 2);
            x+=2;
            g2d.fillRect(x, getY(high), 2, heightOfBar);
            x+=2;
            g2d.fillRect(x, getY(close)-1, 2, 2);
        }

        if(extremes_f){
            g2d.setColor(Color.CYAN);
            for(Double min : mins){
                g2d.drawLine(0, getY(min), width, getY(min));
            }
            g2d.setColor(Color.RED);
            for(Double max : maxs){
                g2d.drawLine(0, getY(max), width, getY(max));
            }
        }

        if(trLines_f){
            g2d.setColor(Color.ORANGE);
            int x1, x2, y1, y2;
            for(TrendLine tl : trLines){
                //from the 1st (first one) bar to the 99th (last one)
                x1 = (int)tl.coordinates.get(0).getX(); //index of bar
                y1 = getY(tl.getY(x1));
                y2 = getY(tl.getY(99));
                x1 = spaceBetweenBars + (barPeriod * x1); //x coord on the canvas
                x2 = spaceBetweenBars + (barPeriod * 98);
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }

    /** rounds an argument. math. */
    double round(double d, int digitsAfterComma){
        int mult =1;
        for(int i=0; i<digitsAfterComma; i++){
            mult*=10;
        }
        d = d * mult;
        int i = (int)Math.round(d);
        d = (double) i/mult;
        return d;
    }

    /** returns Y value on canvas of the value given in pips */
    private int getY(double value){ //counting the Y for drawing lines by value of quotation
        int result;
        result = height-(int)(lengthOfPip*100000*(value-base));
        return result;
    }

    /** commands to repaint with extremes */
    void drawExtremes(ArrayList<Double> maxs, ArrayList<Double> mins){ //! true/false. analise just before repainting in paintComponent()
        extremes_f = true;
        this.maxs = maxs;
        this.mins = mins;
        repaint();

    }

    /** commands to repaint with resistance lines. */
    void drawResLines(ArrayList<ResistanceLine> resLines){
        this.resLines = resLines;
        resLines_f = true;
        repaint();
    }

    void drawTrendLines(ArrayList<TrendLine> trLines){
        this.trLines = trLines;
        trLines_f = true;
        repaint();
    }
}
