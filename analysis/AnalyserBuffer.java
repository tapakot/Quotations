package analysis;

import java.util.ArrayList;

/** Buffer with indicator values */
public class AnalyserBuffer {
    public ArrayList<Extreme> extremes;
    public ArrayList<Double> maximums;
    public ArrayList<Double> minimums;
    /** resistance lines calculated using extremes */
    public ArrayList<ResistanceLine> exLines;
    /** trend lines calculated using extremes */
    public ArrayList<TrendLine> trendLines;

    /** initialising */
    public AnalyserBuffer(){
        extremes = new ArrayList<Extreme>();
        maximums = new ArrayList<Double>();
        minimums = new ArrayList<Double>();
        exLines = new ArrayList<ResistanceLine>();
        trendLines = new ArrayList<TrendLine>();
    }

    public void clean(){
        //maximums.clear();
        //minimums.clear();
        extremes.clear();
        exLines.clear();
        trendLines.clear();
    }
}
