package analysis;


import common.Point;

import java.util.ArrayList;

public class MovingAverage {
    Analyser owner;

    public int length;
    public ArrayList<Point> values;

    public MovingAverage(Analyser analyser, int length){
        owner=analyser;
        this.length = length;
        values = new ArrayList();
        calculate();
    }

    void calculate(){ //length-1 last and now
        for(int i = length-1; i<owner.toAn.size(); i++){
            double value = 0;
            for(int j = i-length+1; j<=i; j++){
                value += owner.toAn.get(j).close;
            }
            value = value / length;
            values.add(new Point(i, value));
        }
    }

}

