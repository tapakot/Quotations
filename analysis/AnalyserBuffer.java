package analysis;

import java.util.ArrayList;

public class AnalyserBuffer {
    public ArrayList<Double> maximums;
    public ArrayList<Double> minimums;
    public ArrayList<ResistanceLine> exLines;

    public AnalyserBuffer(){
        maximums = new ArrayList<Double>();
        minimums = new ArrayList<Double>();
        exLines = new ArrayList<ResistanceLine>();
    }
}
