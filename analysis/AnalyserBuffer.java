package analysis;

import java.util.ArrayList;

/** Buffer with indicator values */
public class AnalyserBuffer {
    volatile public ArrayList<Extreme> extremes;
    volatile public ArrayList<Double> maximums;
    volatile public ArrayList<Double> minimums;
    volatile public ArrayList<RelativeExtreme> relExtremes;
    volatile public ArrayList<RelativeExtreme> relHighs; //relative highs. index and value
    volatile public ArrayList<RelativeExtreme> relLows;
    /** resistance lines calculated using extremes */
    volatile public ArrayList<ResistanceLine> exLines;
    /** trend lines calculated using extremes */
    volatile public ArrayList<TrendLine> trendLines;
    volatile public ArrayList<TDSequence> tdSequences;
    volatile public InnerTrendLine innerTrendLine;
    volatile public ArrayList<MovingAverage> movingAverages;

    /** initialising */
    public AnalyserBuffer(){
        extremes = new ArrayList<Extreme>();
        maximums = new ArrayList<Double>();
        minimums = new ArrayList<Double>();
        exLines = new ArrayList<ResistanceLine>();
        trendLines = new ArrayList<TrendLine>();
        relExtremes = new ArrayList<RelativeExtreme>();
        relHighs = new ArrayList<RelativeExtreme>();
        relLows = new ArrayList<RelativeExtreme>();
        tdSequences = new ArrayList<TDSequence>();
        movingAverages = new ArrayList<MovingAverage>();
    }

    public void clean(){
        maximums.clear();
        minimums.clear();
        extremes.clear();
        exLines.clear();
        trendLines.clear();
        relExtremes.clear();
        relHighs.clear();
        relLows.clear();
        tdSequences.clear();
        innerTrendLine = null;
        movingAverages.clear();
    }
}
