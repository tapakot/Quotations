package analysis;




import java.awt.*;
import java.util.ArrayList;

public class TrendLine {
    public ArrayList<Point> coordinates;
    public double a, b; //x1*a+b=y1; x2*a+b=y2;

    /** creates new one */
    /*TrendLine(double x1, double y1, double x2, double y2){
        coordinates = new ArrayList<>();
        coordinates.add(new (x1, y1));
        coordinates.add(new Point(x2, y2));

        a = (y1 - y2)/(x1 - x2);
        b = y1 - x1*a;
    }*/

    /** returns true if the value is covered */
    /*public boolean isCovering(double x, double value){
        return true;
    }*/

    /*public void addPoint(double x, double y){
        coordinates.add(new Point(x, y));
        double x1 = coordinates.get(0).getX();
        double y1 = coordinates.get(0).getY();
        a = (y1 - y)/(x1 - x);
        b = y1 - x1*a;
    }*/

}
