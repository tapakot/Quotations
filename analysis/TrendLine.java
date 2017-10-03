package analysis;

import java.util.ArrayList;
import common.*;

import static common.ForexConstants.*;

public class TrendLine {
    static Analyser owner;

    public ArrayList<Point> coordinates;
    public double a, b; //x1*a+b=y1; x2*a+b=y2;
    public int power;
    public boolean up;

    /** creates new one */
    TrendLine(double x1, double y1, double x2, double y2){
        coordinates = new ArrayList<>();
        coordinates.add(new Point(x1, y1));
        coordinates.add(new Point(x2, y2));

        a = (y1 - y2)/(x1 - x2);
        b = y1 - x1*a;

        power = 0;

        up = (y2 > y1);
    }

    /** returns true if the value is covered */
    public boolean isCovering(double x, double value){
        boolean covered = false;
        if((value < x*a + b + TREND_LINE_SENS) && (value > x*a + b - TREND_LINE_SENS)){
            covered = true;
        }
        return covered;
    }

    public void addPoint(double x, double y){
        coordinates.add(new Point(x, y));
        double x1 = coordinates.get(0).getX();
        double y1 = coordinates.get(0).getY();
        a = (y1 - y)/(x1 - x);
        b = y1 - x1*a;
        power++;
    }

    public double getY(double x){
        return x*a+b;
    }

    static void analyseForTrendLines(Analyser analyser){
        owner = analyser;
        ArrayList<TrendLine> TLList = new ArrayList<TrendLine>();
        //test
        /*System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for(Extreme ex : anBuffer.extremes){
            if(ex.max){
                System.out.println(ex.index + " max " + ex.value);
            } else {
                System.out.println(ex.index + " min " + ex.value);
            }
        }
        */
        //extremes are already sorted by index (by anForEx)
        //up lines
        Extreme preMax2 = null; //2nd pre
        Extreme preMax = null; //pre
        boolean maxBigger = false; //true when now is bigger than previous
        boolean maxSmaller = false;
        Extreme preMin2 = null;
        Extreme preMin = null;
        boolean minBigger = false; //true when now is bigger than previous
        boolean minSmaller = false;
        boolean covered = false;
        for(Extreme ex : owner.anBuffer.extremes){
            covered = false;
            if((preMax == null)&&(ex.max)){
                preMax2 = preMax;
                preMax = ex;
                continue;
            }
            if((preMin == null)&&(!ex.max)){
                preMin2 = preMin;
                preMin = ex;
                continue;
            }

            if(ex.max){
                preMax2 = preMax;
                preMax = ex;
                if(preMax.value > preMax2.value){
                    //up lines
                    maxBigger = true;
                    maxSmaller = false;
                    if(minBigger){
                        for(TrendLine tl : TLList) {
                            if (tl.isCovering(preMin.index, preMin.value)) {
                                tl.addPoint(preMin.index, preMin.value);
                                covered = true;
                            }
                        }
                        if(!covered){
                            TLList.add(new TrendLine(preMin2.index, preMin2.value, preMin.index, preMin.value));
                        }
                    }
                } else {
                    //down lines
                    maxBigger = false;
                    maxSmaller = true;
                    if(minSmaller){
                        for(TrendLine tl : TLList) {
                            if (tl.isCovering(preMax.index, preMax.value)) {
                                tl.addPoint(preMax.index, preMax.value);
                                covered = true;
                            }
                        }
                        if(!covered){
                            TLList.add(new TrendLine(preMax2.index, preMax2.value, preMax.index, preMax.value));
                        }
                    }
                }
            } else {
                preMin2 = preMin;
                preMin = ex;
                if(preMin.value > preMin2.value){
                    //up lines
                    minBigger = true;
                    minSmaller = false;
                    if(maxBigger){
                        for(TrendLine tl : TLList) {
                            if (tl.isCovering(preMin.index, preMin.value)) {
                                tl.addPoint(preMin.index, preMin.value);
                                covered = true;
                            }
                        }
                        if(!covered){
                            TLList.add(new TrendLine(preMin2.index, preMin2.value, preMin.index, preMin.value));
                        }
                    }
                } else {
                    //down lines
                    minBigger = false;
                    minSmaller = true;
                    if(maxSmaller){
                        for(TrendLine tl : TLList) {
                            if (tl.isCovering(preMax.index, preMax.value)) {
                                tl.addPoint(preMax.index, preMax.value);
                                covered = true;
                            }
                        }
                        if(!covered){
                            TLList.add(new TrendLine(preMax2.index, preMax2.value, preMax.index, preMax.value));
                        }
                    }
                }
            }
        }

        //loop 2. getting over deletion
        ArrayList<TrendLine> toDelete = new ArrayList<>();
        for(TrendLine tl : TLList){
            for(int i = (int)tl.coordinates.get(0).getX(); i<HIST_COUNT; i++){
                if(tl.up){ //for up lines getting over down
                    //2nd closing
                    if((owner.toAn.get(i).close < tl.getY(i))&&(!tl.isCovering(i, owner.toAn.get(i).close))){
                        if((owner.toAn.get(i).close < tl.getY(i))&&(!tl.isCovering(i, owner.toAn.get(i).close))){
                            toDelete.add(tl);
                        }
                    }
                    //big difference
                    if(owner.toAn.get(i).low * OVER_TREND_LINE < tl.getY(i)){
                        toDelete.add(tl);
                    }
                } else { //for down lines getting over up
                    //2nd closing
                    if((owner.toAn.get(i).close > tl.getY(i))&&(!tl.isCovering(i, owner.toAn.get(i).close))){
                        if((owner.toAn.get(i).close > tl.getY(i))&&(!tl.isCovering(i, owner.toAn.get(i).close))){
                            toDelete.add(tl);
                        }
                    }
                    //big difference
                    if(owner.toAn.get(i).low / OVER_TREND_LINE > tl.getY(i)){
                        toDelete.add(tl);
                    }
                }
            }
        }
        for(TrendLine tl : toDelete){
            TLList.remove(tl);
        }
        toDelete = null;


        for(TrendLine tl : TLList) {
            owner.anBuffer.trendLines.add(tl);
        }

        //test. printing all trend lines
        /*
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for(TrendLine tl : anBuffer.trendLines){
            if(tl.up) {
                System.out.println("up, nom. of points:" + tl.coordinates.size());
            } else {
                System.out.println("down, nom. of points:" + tl.coordinates.size());
            }
            System.out.println("x1: "+ tl.coordinates.get(0).getX());
            System.out.println("y1: "+ tl.coordinates.get(0).getY());
            System.out.println("last x: "+ tl.coordinates.get(tl.coordinates.size()-1).getX());
            System.out.println("last y: "+ tl.coordinates.get(tl.coordinates.size()-1).getY());
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        */
    }
}
