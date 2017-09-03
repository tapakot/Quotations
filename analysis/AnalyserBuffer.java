package analysis;

import java.util.ArrayList;

/** Buffer with indicator values */
public class AnalyserBuffer {
    public ArrayList<Double> maximums;
    public ArrayList<Double> minimums;
    /** resistance lines calculated using extremes */
    public ArrayList<ResistanceLine> exLines;
    /** trend lines calculated using extremes */
    public ArrayList<TrendLine> trendLines;

    /** initialising */
    public AnalyserBuffer(){
        maximums = new ArrayList<Double>();
        minimums = new ArrayList<Double>();
        exLines = new ArrayList<ResistanceLine>();
        trendLines = new ArrayList<TrendLine>();
    }

    public void clean(){
        maximums.clear();
        minimums.clear();
        exLines.clear();
        trendLines.clear();
    }
}
