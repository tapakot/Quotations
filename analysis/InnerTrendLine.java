package analysis;

import common.Mathematics;
import common.Point;

import java.util.ArrayList;

import static common.ForexConstants.TREND_LINE_SENS;

public class InnerTrendLine {
    static Analyser owner;

    public double a, b;
    public boolean up;

    public InnerTrendLine(double a, double b){
        this.a = a;
        this.b = b;
        up = a>0;
    }

    public boolean isCovering(int x, double value){
        boolean covered = false;
        if((value < x*a + b + TREND_LINE_SENS) && (value > x*a + b - TREND_LINE_SENS)){
            covered = true;
        }
        return covered;
    }

    public double getY(double x){
        return x*a+b;
    }

    public static void analyseForInnerTrendLine(Analyser analyser){
        owner = analyser;
        ArrayList<Point> points = new ArrayList<>();
        for(RelativeExtreme rel : owner.getBuffer().relExtremes){
            Point point = new Point(rel.index, rel.value);
            points.add(point);
        }
        double[] resultOfApp = Mathematics.approximate(points);
        owner.getBuffer().innerTrendLine = new InnerTrendLine(resultOfApp[0], resultOfApp[1]);
    }
}
