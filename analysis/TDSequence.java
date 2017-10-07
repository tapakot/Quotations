package analysis;

import java.util.ArrayList;

import static common.ForexConstants.*;

public class TDSequence {
    static Analyser owner;

    public ArrayList<TDLine> lines;

    public int direction;
    public boolean up;

    public TDSequence(TDLine firstLine){
        lines = new ArrayList<TDLine>();
        lines.add(firstLine);
        if(firstLine.a > 0){ direction = UP_DIRECTION; up = true;}
        else { direction = DOWN_DIRECTION; up = false;}
    }

    static void analyseForTDSequence(Analyser analyser){
        owner = analyser;
        ArrayList<TDSequence> toDelete = new ArrayList<>();

        RelativeExtreme pre = null;
        for(RelativeExtreme rmax : owner.getBuffer().relHighs){
            if(pre==null){ pre = rmax;}
            else{
                if(rmax.value < pre.value){
                    TDLine line = new TDLine(pre, rmax);
                    boolean added = false;
                    for(TDSequence seq : owner.getBuffer().tdSequences) {
                        if(seq.direction == DOWN_DIRECTION) {
                            if (seq.lines.get(seq.lines.size() - 1).coordinates[1].index == line.coordinates[0].index) {
                                seq.lines.add(line);
                                added = true;
                            }
                        }
                    }
                    if(!added){
                        owner.getBuffer().tdSequences.add(new TDSequence(line));
                    }
                } else{
                    toDelete.clear();
                    for(TDSequence seq : owner.getBuffer().tdSequences) {
                        if (seq.direction == DOWN_DIRECTION) {
                            toDelete.add(seq);
                        }
                    }
                    for(TDSequence toDel : toDelete){
                        owner.getBuffer().tdSequences.remove(toDel);
                    }
                }
            }
            pre = rmax;
        }
        pre = null;
        for(RelativeExtreme rmin : owner.getBuffer().relLows){
            if(pre==null){ pre = rmin;}
            else{
                if(rmin.value > pre.value){
                    TDLine line = new TDLine(pre, rmin);
                    boolean added = false;
                    for(TDSequence seq : owner.getBuffer().tdSequences) {
                        if(seq.direction == UP_DIRECTION) {
                            if (seq.lines.get(seq.lines.size() - 1).coordinates[1].index == line.coordinates[0].index) {
                                seq.lines.add(line);
                                added = true;
                            }
                        }
                    }
                    if(!added){
                        owner.getBuffer().tdSequences.add(new TDSequence(line));
                    }
                }else{
                    toDelete.clear();
                    for(TDSequence seq : owner.getBuffer().tdSequences) {
                        if (seq.direction == UP_DIRECTION) {
                            toDelete.add(seq);
                        }
                    }
                    for(TDSequence toDel : toDelete){
                        owner.getBuffer().tdSequences.remove(toDel);
                    }
                }
            }
            pre = rmin;
        }
        toDelete.clear();
        for(TDSequence seq : owner.getBuffer().tdSequences) {
            if(seq.direction == UP_DIRECTION){
                if(owner.toAnalyse[1][HIST_COUNT-1] < seq.lines.get(seq.lines.size()-1).getY(HIST_COUNT-1)/TD_LINE_SENS){
                    toDelete.add(seq);
                }
            } else {
                if(owner.toAnalyse[0][HIST_COUNT-1] > seq.lines.get(seq.lines.size()-1).getY(HIST_COUNT-1)*TD_LINE_SENS){
                    toDelete.add(seq);
                }
            }
        }
        for(TDSequence toDel : toDelete){
            owner.getBuffer().tdSequences.remove(toDel);
        }
    }
}
