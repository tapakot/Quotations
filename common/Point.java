package common;

public class Point {
    private double xd;
    private double yd;

    public Point(){
        xd = 0;
        yd = 0;
    }

    public Point(double x, double y){
        xd = x;
        yd = y;
    }

    public Point(int x, int y){
        xd = (double) x;
        yd = (double) y;
    }

    public double getX(){
        return xd;
    }

    public double getY(){
        return yd;
    }
}
