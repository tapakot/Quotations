package analysis;

import buffer.QuotationBuffer;
import common.Mathematics;
import common.Point;
import common.Quotation;

import java.util.ArrayList;

import static common.ForexConstants.*;
import static common.ForexConstants.ADVICE_DOWN;
import static common.ForexConstants.ADV_INNER_TREND_LINE;

public class InnerTrendLine {
    public double a, b;
    public boolean up;

    public InnerTrendLine(double a, double b){
        this.a = a;
        this.b = b;
        up = a>0;
    }

    void change(double a, double b){
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

    public static void analyseForInnerTrendLine(AnalyserBuffer anBuffer){
        ArrayList<Point> points = new ArrayList<>();
        for(RelativeExtreme rel : anBuffer.relExtremes){
            Point point = new Point(rel.index, rel.value);
            points.add(point);
        }
        double[] resultOfApp = Mathematics.approximate(points);
        anBuffer.innerTrendLine.change(resultOfApp[0], resultOfApp[1]); //should be the same object to draw
    }

    public static double getAdviceFor(Analyser analyser, String instrumentName, Quotation quo){
        int firstAnBuf;
        int indexOfBuffer = -1;
        for(QuotationBuffer buf : analyser.buffers){
            if(buf.name.equals(instrumentName)){
                indexOfBuffer = analyser.buffers.indexOf(buf);
            }
        }
        firstAnBuf = indexOfBuffer*PERIODS.size();
        AnalyserBuffer anBuffer = analyser.anBuffers.get(firstAnBuf);

        ArrayList<Quotation> history = analyser.buffers.get(indexOfBuffer).history5;
        int last = HIST_COUNT-1;

        double advice = ADVICE_STAY;

        Quotation historyLast = history.get(history.size()-1);
        Quotation historyPreLast = history.get(history.size() - 2);
        InnerTrendLine line = anBuffer.innerTrendLine;
        if (line.up) {
            //getting over
            if ((historyLast.close < line.getY(last)) && (!line.isCovering(last, historyLast.close))) {
                if ((historyPreLast.close) < line.getY(last - 1) && (!line.isCovering(last - 1, historyPreLast.close))) {
                    return ADVICE_CLOSE_UP * ADV_INNER_TREND_LINE;
                }
            }
            if (quo.low * OVER_TD_LINE < line.getY(last + 1)) {
                return ADVICE_CLOSE_UP * ADV_INNER_TREND_LINE;
            }
            //covering
            if (line.isCovering(last + 1, quo.close)) {
                return ADVICE_UP * ADV_INNER_TREND_LINE;
            }
        } else {
            //getting over
            if ((historyLast.close > line.getY(last)) && (!line.isCovering(last, historyLast.close))) {
                if ((historyPreLast.close) > line.getY(last - 1) && (!line.isCovering(last - 1, historyPreLast.close))) {
                    return ADVICE_CLOSE_DOWN * ADV_INNER_TREND_LINE;
                }
            }
            if (quo.low / OVER_TD_LINE > line.getY(last + 1)) {
                return ADVICE_CLOSE_DOWN * ADV_INNER_TREND_LINE;
            }
            //covering
            if (line.isCovering(last + 1, quo.close)) {
                return ADVICE_DOWN * ADV_INNER_TREND_LINE;
            }
        }
        return advice;}
}
