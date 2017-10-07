package common;

import java.util.ArrayList;

public class Mathematics {

    public static double round(double d, int digitsAfterComma){
        int mult =1;
        for(int i=0; i<digitsAfterComma; i++){
            mult*=10;
        }
        d = d * mult;
        int i = (int)Math.round(d);
        d = (double) i/mult;
        return d;
    }

    public static double[] approximate(ArrayList<Point> points){
        double[] result = new double[2];
        double sumx =0;
        double sumx2 =0;
        double sumy =0;
        double sumxy = 0;
        for(Point point : points){
            sumx += point.getX();
            sumx2 += point.getX() * point.getX();
            sumy += point.getY();
            sumxy += point.getX() * point.getY();
        }
        int n = points.size();
        double a = (n*sumxy - sumx*sumy)/(n*sumx2 - sumx*sumx);
        double b = (sumy - a*sumx)/n;
        result[0] = a;
        result[1] = b;
        return result;
    }
}
