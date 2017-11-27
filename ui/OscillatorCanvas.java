package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static common.ForexConstants.HIST_COUNT;
import static common.ForexConstants.gridPeriod;

class OscillatorCanvas extends JPanel {
    Graphics2D g2d;
    GraphCanvas owner;

    int height;
    int width;
    int spaceBetweenPoints;

    ArrayList<Double> values;

    OscillatorCanvas(GraphCanvas owner){
        this.owner = owner;
        setMinimumSize(new Dimension(700, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        setPreferredSize(getMinimumSize());

        owner.addOC(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        height = getHeight();
        width = getWidth()-80;
        g2d = (Graphics2D)g;
        super.paintComponent(g2d);

        spaceBetweenPoints = owner.barPeriod;

        this.setBackground(Color.gray);

        g2d.setColor(Color.black);
        //separators
        g2d.drawLine(0,0,width,0);//separator from graphCanvas
        g2d.drawLine(width, 0, width, height);

        //marks on separator
        for(int i = 10; i<100; i+=10){
            int x = width;
            //g2d.drawLine(x-5, getY(i), x+5, getY(i));
            g2d.drawString(Integer.toString(i),  x+7, getY(i)+5);
            while(x >=0) {
                g2d.drawLine(x - 5, getY(i), x + 5, getY(i));
                x-=gridPeriod;
            }
        }

        //res lines
        g2d.setColor(new Color(150, 30, 150));
        g2d.drawLine(0, getY(20), width, getY(20));
        g2d.drawLine(0, getY(80), width, getY(80));

        //value lines
        g2d.setColor(new Color(150, 30, 150));
        int countOfBars = HIST_COUNT -1;
        int startIndex = countOfBars - values.size(); //???
        for(int x = startIndex; x<countOfBars-1; x++){ //line from x to x+1
            //polygons
            /*int xPoints[] = {getX(x), getX(x+1), getX(x+1), getX(x)};
            int yPoints[] = {getY(values.get(x-startIndex)), getY(values.get(x-startIndex+1)), getY(0), getY(0)};
            g2d.fillPolygon(xPoints, yPoints, 4);
            */
            g2d.drawLine(getX(x), getY(values.get(x-startIndex)), getX(x+1), getY(values.get(x-startIndex+1)));
        }
    }

    void setValues(ArrayList<Double> values){
        this.values = values;
    }

    private int getX(int index){
        return owner.spaceBetweenBars + (spaceBetweenPoints * index);
    }

    private int getY(double value){ //full * % = part;
        int result;
        result = (int)(height / 100 * value);
        result = height - result;
        return result;
    }
}
