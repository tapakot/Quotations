package analysis;

import buffer.QuotationBuffer;
import common.Quotation;

import java.util.ArrayList;

import static common.ForexConstants.*;

public class TDSequence {
    public ArrayList<TDLine> lines;

    public int direction;
    public boolean up;

    public TDSequence(TDLine firstLine){
        lines = new ArrayList<TDLine>();
        lines.add(firstLine);
        if(firstLine.a > 0){ direction = UP_DIRECTION; up = true;}
        else { direction = DOWN_DIRECTION; up = false;}
    }

    static void analyseForTDSequence(double[][] toAnalyse, AnalyserBuffer anBuffer){
        ArrayList<TDSequence> toDelete = new ArrayList<>();

        RelativeExtreme pre = null;
        for(RelativeExtreme rmax : anBuffer.relHighs){
            if(pre==null){ pre = rmax;}
            else{
                if(rmax.value < pre.value){
                    TDLine line = new TDLine(pre, rmax);
                    boolean added = false;
                    for(TDSequence seq : anBuffer.tdSequences) {
                        if(seq.direction == DOWN_DIRECTION) {
                            if (seq.lines.get(seq.lines.size() - 1).coordinates[1].index == line.coordinates[0].index) {
                                seq.lines.add(line);
                                added = true;
                            }
                        }
                    }
                    if(!added){
                        anBuffer.tdSequences.add(new TDSequence(line));
                    }
                } else{
                    toDelete.clear();
                    for(TDSequence seq : anBuffer.tdSequences) {
                        if (seq.direction == DOWN_DIRECTION) {
                            toDelete.add(seq);
                        }
                    }
                    for(TDSequence toDel : toDelete){
                        anBuffer.tdSequences.remove(toDel);
                    }
                }
            }
            pre = rmax;
        }
        pre = null;
        for(RelativeExtreme rmin : anBuffer.relLows){
            if(pre==null){ pre = rmin;}
            else{
                if(rmin.value > pre.value){
                    TDLine line = new TDLine(pre, rmin);
                    boolean added = false;
                    for(TDSequence seq : anBuffer.tdSequences) {
                        if(seq.direction == UP_DIRECTION) {
                            if (seq.lines.get(seq.lines.size() - 1).coordinates[1].index == line.coordinates[0].index) {
                                seq.lines.add(line);
                                added = true;
                            }
                        }
                    }
                    if(!added){
                        anBuffer.tdSequences.add(new TDSequence(line));
                    }
                }else{
                    toDelete.clear();
                    for(TDSequence seq : anBuffer.tdSequences) {
                        if (seq.direction == UP_DIRECTION) {
                            toDelete.add(seq);
                        }
                    }
                    for(TDSequence toDel : toDelete){
                        anBuffer.tdSequences.remove(toDel);
                    }
                }
            }
            pre = rmin;
        }
        toDelete.clear();
        for(TDSequence seq : anBuffer.tdSequences) {
            if(seq.direction == UP_DIRECTION){
                if(toAnalyse[1][HIST_COUNT-1] < seq.lines.get(seq.lines.size()-1).getY(HIST_COUNT-1)/TD_LINE_SENS){
                    toDelete.add(seq);
                }
            } else {
                if(toAnalyse[0][HIST_COUNT-1] > seq.lines.get(seq.lines.size()-1).getY(HIST_COUNT-1)*TD_LINE_SENS){
                    toDelete.add(seq);
                }
            }
        }
        for(TDSequence toDel : toDelete){
            anBuffer.tdSequences.remove(toDel);
        }
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
        for(TDSequence seq : anBuffer.tdSequences){
            if(seq.lines.size()>=3) {
                Quotation historyLast = history.get(history.size() - 1);
                Quotation historyPreLast = history.get(history.size() - 2);
                TDLine lastLine = seq.lines.get(seq.lines.size() - 1);
                if (seq.up) {
                    //getting over
                    if ((historyLast.close < lastLine.getY(last)) && (!lastLine.isCovering(last, historyLast.close))) {
                        if ((historyPreLast.close) < lastLine.getY(last - 1) && (!lastLine.isCovering(last - 1, historyPreLast.close))) {
                            return ADVICE_CLOSE_UP * ADV_TD_LINES;
                        }
                    }
                    if (quo.low * OVER_TD_LINE < lastLine.getY(last + 1)) {
                        return ADVICE_CLOSE_UP * ADV_TD_LINES;
                    }
                    //covering
                    if (lastLine.isCovering(last + 1, quo.close)) {
                        return ADVICE_UP * ADV_TD_LINES;
                    }
                } else {
                    //getting over
                    if ((historyLast.close > lastLine.getY(last)) && (!lastLine.isCovering(last, historyLast.close))) {
                        if ((historyPreLast.close) > lastLine.getY(last - 1) && (!lastLine.isCovering(last - 1, historyPreLast.close))) {
                            return ADVICE_CLOSE_DOWN * ADV_TD_LINES;
                        }
                    }
                    if (quo.low / OVER_TD_LINE > lastLine.getY(last + 1)) {
                        return ADVICE_CLOSE_DOWN * ADV_TD_LINES;
                    }
                    //covering
                    if (lastLine.isCovering(last + 1, quo.close)) {
                        return ADVICE_DOWN * ADV_TD_LINES;
                    }
                }
            }
        }
        return advice * ADV_TD_LINES;
    }
}
