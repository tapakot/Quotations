package ui;

import analysis.ResistanceLine;
import buffer.QuotationBuffer;
import common.Quotation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class GraphCanvas extends JPanel{
    Graphics2D g2d;
    QuotationBuffer buffer;
    ArrayList<Quotation> Quotations; //last
    ArrayList<Double> maxs;
    ArrayList<Double> mins;
    ArrayList<ResistanceLine> resLines;

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
    boolean extremums_f;
    boolean resLines_f;


    private static final int WIDTH_OF_BAR = 2*2 + 2;
    private static final int gridPeriod = 18; //period of marks of the grid

    GraphCanvas(){
        buffer = MainFrame.buffer;
        Quotations = new ArrayList<>();
        countOfBars = 99;
        for(int i= 0; i<countOfBars; i++){
            // should be changed by realTimeEvent
            Quotations.add(buffer.getQuotation((short)5, 100-countOfBars-1+i)); //in the end but not the last one (unpredictable)
        }

        extremums_f = false;
        resLines_f = false;

        /*for(int i = 0; i<30; i++){
            System.out.println("Graph["+ i +"]: "+ Quotations.get(i));
        }*/
    }

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

        if(extremums_f){
            g2d.setColor(Color.CYAN);
            for(Double min : mins){
                g2d.drawLine(0, getY(min), width, getY(min));
            }
            g2d.setColor(Color.RED);
            for(Double max : maxs){
                g2d.drawLine(0, getY(max), width, getY(max));
            }
        }


    }

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

    private int getY(double value){ //counting the Y for drawing lines by value of quotation
        int result;
        result = height-(int)(lengthOfPip*100000*(value-base));
        return result;
    }

    void drawExtremums(ArrayList<Double> maxs, ArrayList<Double> mins){
        extremums_f = true;
        this.maxs = maxs;
        this.mins = mins;
        repaint();
        /*if(extremums){
            g2d.setColor(Color.RED);
            for(Double min : mins){
                g2d.drawLine(0, getY(min), width, getY(min));
            }
        }*/
    }

    void drawResLines(ArrayList<ResistanceLine> resLines){
        this.resLines = resLines;
        resLines_f = true;
        repaint();
    }
}
