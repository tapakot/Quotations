package analysis;


import common.Point;
import common.Quotation;

import java.util.ArrayList;

public class MovingAverage {
    public int length;
    public ArrayList<Point> values;

    public MovingAverage(ArrayList<Quotation> toAn, int length){
        this.length = length;
        values = new ArrayList();
        calculate(toAn);
    }

    void calculate(ArrayList<Quotation> toAn){ //length-1 last and now
        for(int i = length-1; i<toAn.size(); i++){
            double value = 0;
            for(int j = i-length+1; j<=i; j++){
                value += toAn.get(j).close;
            }
            value = value / length;
            values.add(new Point(i, value));
        }
    }

}

