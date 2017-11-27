package ui;

import analysis.*;
import buffer.QuotationBuffer;
import common.*;

import static common.ForexConstants.*;
import static common.Mathematics.round;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** Canvas where graphics are drawn.
 * Manages all painting on graphs.
 */
class GraphCanvas extends JPanel{
    Graphics2D g2d;
    QuotationBuffer buffer;
    int period; //5, 15, 30, 60, 1440
    /** 100 last quotations (to draw) */
    AnalyserBuffer anBuf;
    ArrayList<Quotation> Quotations; //last
    ArrayList<OscillatorCanvas> oscillators;

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
    boolean tdSequences_f;
    boolean innerLine_f;
    boolean movingAverages_f;

    /** initialisation */
    GraphCanvas(QuotationBuffer buffer, int period){
        setMinimumSize(new Dimension(700, 400));
        setPreferredSize(getMinimumSize());
        this.buffer = buffer;
        this.period = period;
        Quotations = new ArrayList<>();
        oscillators = new ArrayList<>();
        countOfBars = HIST_COUNT-1;
        for(int i= 0; i<countOfBars; i++){
            // should be changed by realTimeEvent
            Quotations.add(buffer.getQuotation(period, HIST_COUNT-countOfBars-1+i)); //in the end but not the last one (unpredictable)
        }

        extremes_f = false;
        resLines_f = false;
        tdSequences_f = false;
        innerLine_f = false;

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
        capacity = averageBar/0.06;//0.08
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
        /*System.out.println("height: "+height);
        System.out.println("center: "+center);
        System.out.println("averageBar: "+averageBar);
        System.out.println("capacity: "+capacity);
        System.out.println("frequencyOfMarks: "+frequencyOfMarks);
        System.out.println("lengthOfPip: "+lengthOfPip);
        System.out.println("base: "+base);
        */

        this.setBackground(Color.gray);

        g2d.setColor(Color.black);
        g2d.drawLine(width, 0, width, height); //separator between graph and quos

        //resistance lines
        if(resLines_f){
            g2d.setColor(Color.BLUE);
            for(ResistanceLine line : anBuf.exLines){
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
            for(Double min : anBuf.minimums){
                g2d.drawLine(0, getY(min), width, getY(min));
            }
            g2d.setColor(Color.RED);
            for(Double max : anBuf.maximums){
                g2d.drawLine(0, getY(max), width, getY(max));
            }
        }

        if(trLines_f){
            g2d.setColor(Color.ORANGE);
            int x1, x2, y1, y2;
            for(TrendLine tl : anBuf.trendLines){
                //from the 1st (first one) bar to the 99th (last one)
                x1 = (int)tl.coordinates.get(0).getX(); //index of bar
                y1 = getY(tl.getY(x1));
                y2 = getY(tl.getY(countOfBars));
                x1 = spaceBetweenBars + (barPeriod * x1); //x coord on the canvas
                x2 = spaceBetweenBars + (barPeriod * countOfBars-1);
                g2d.drawLine(x1, y1, x2, y2);
            }
        }

        if(tdSequences_f){
            g2d.setColor(Color.ORANGE);
            for(TDSequence seq : anBuf.tdSequences){
                if(seq.lines.size()>=2) {
                    for (TDLine line : seq.lines) {
                        int x1 = line.coordinates[0].index - 3;
                        double y1 = line.getY(line.coordinates[0].index - 3);
                        int x2 = line.coordinates[1].index + 3;
                        double y2 = line.getY(line.coordinates[1].index + 3);
                        g2d.drawLine(getX(x1), getY(y1), getX(x2), getY(y2));
                    }
                }
            }
        }

        if(innerLine_f && anBuf.innerTrendLine!=null){
            g2d.setColor(Color.ORANGE);
            int x1 = 0;
            double y1 = anBuf.innerTrendLine.getY(x1);
            int x2 = HIST_COUNT;
            double y2 = anBuf.innerTrendLine.getY(x2);
            g2d.drawLine(getX(x1), getY(y1), getX(x2), getY(y2));
        }

        if(movingAverages_f){
            g2d.setColor(Color.GREEN);
            for(MovingAverage movingAverage : anBuf.movingAverages){
                for(int i = 0; i<movingAverage.values.size()-2; i++){
                    int x1 = (int)movingAverage.values.get(i).getX();
                    double y1 = movingAverage.values.get(i).getY();
                    int x2 = (int)movingAverage.values.get(i+1).getX();
                    double y2 = movingAverage.values.get(i+1).getY();
                    g2d.drawLine(getX(x1), getY(y1), getX(x2), getY(y2));
                }
            }
        }

        /*Thread thisThread = Thread.currentThread();
        System.out.println("in canvas.draw (not main!): "+thisThread);*/
    }

    /** returns Y value on canvas of the value given in pips */
    private int getY(double value){ //counting the Y for drawing lines by value of quotation
        int result;
        result = height-(int)(lengthOfPip*100000*(value-base));
        return result;
    }

    private int getX(int index){
        return spaceBetweenBars + (barPeriod * index);
    }

    void findAnBuffer(){
        int indexOfQuoBuffer = -1;
        int indexOfPeriod = -1;
        for(QuotationBuffer buf : MainFrame.analyser.buffers){
            if(buffer.name.equals(buf.name)){
                indexOfQuoBuffer = MainFrame.analyser.buffers.indexOf(buf);
            }
        }
        for(String per : PERIODS){
            if(period == Integer.parseInt(per)){
                indexOfPeriod = PERIODS.indexOf(per);
            }
        }
        if(indexOfQuoBuffer != -1){
            anBuf = MainFrame.analyser.anBuffers.get(indexOfQuoBuffer*PERIODS.size() + indexOfPeriod);
        }
        //link oscillators
        for(OscillatorCanvas oc : oscillators){
            oc.setValues(anBuf.rsi);
        }
    }

    void addOC(OscillatorCanvas newOC){
        oscillators.add(newOC);
    }

    void realTimeEvent(int counter){
        if(counter%period == 0){
            Quotations.remove(0);
            Quotations.add(buffer.getQuotation(period, HIST_COUNT-1));
            repaint();
        }
    }

    /** commands to repaint with extremes */
    void drawExtremes(boolean draw){ //! true/false. analise just before repainting in paintComponent()
        extremes_f = draw;
        repaint();
    }

    /** commands to repaint with resistance lines. */
    void drawResLines(boolean draw){
        resLines_f = draw;
        repaint();
    }

    void drawTrendLines(boolean draw){
        trLines_f = draw;
        repaint();
    }

    void drawTDSequences(boolean draw){
        tdSequences_f = draw;
        repaint();
    }

    void drawInnerLine(boolean draw){
        innerLine_f = draw;
        repaint();
    }

    void drawMA(boolean draw){
        movingAverages_f = draw;
        repaint();
    }

}
