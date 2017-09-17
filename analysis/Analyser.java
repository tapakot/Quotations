package analysis;

import buffer.QuotationBuffer;
import common.Quotation;
import common.Settings;
import common.Settings.*;

import java.util.ArrayList;
import java.util.List;

import static common.ForexConstants.*;

/** Main class of the package.
 * An analyser itself. Needed to analyse history.
 * Sets the values of indicators which are put in buffer.
 */
public class Analyser {
    QuotationBuffer buffer;
    AnalyserBuffer anBuffer;
    double[][] toAnalyse; // [highs][lows]
    ArrayList<Quotation> toAn;

    /** initialisation */
    public Analyser(){
        anBuffer = new AnalyserBuffer();
        toAnalyse = new double[2][100];
        toAn = new ArrayList<Quotation>();
    }

    /** sets QuotationBuffer to be analyse */
    public void setQuotationBuffer(QuotationBuffer buffer){     //separated method because it can be test/history/real
        this.buffer = buffer;
        for(int i=0; i<100; i++){
            toAnalyse[0][i] = buffer.getQuotation((short)5, i).high;
            toAnalyse[1][i] = buffer.getQuotation((short)5, i).low;
            toAn.add(buffer.getQuotation((short)5, i));
        }
    }

    /** sets a buffer to be analysed by List of quotations */
    public void setQuotationBuffer(List<Quotation> buffer100){
        for(int i=0; i<100; i++){ //99
            toAnalyse[0][i] = buffer100.get(i).high;
            toAnalyse[1][i] = buffer100.get(i).low;
            toAn.add(buffer100.get(i));
        }
    }

    /** sets all indicators in buffer */
    public void analyse(String cmd){
        switch(cmd){
            case "extremes": analyseForExtremes();
                break;
            case "trendLines": analyseForTrendLines();
                break;
        }
    }

    public void analyse(){
        analyseForExtremes();
        analyseForTrendLines();
    }

    private void analyseForExtremes() {
        for (int i = 5; i < 94; i++) { //0-4; 95-99 could not be interpreted as extremes
            boolean max = true;
            if (toAnalyse[0][i - 4] * EX_SENS_4 > toAnalyse[0][i]) {
                max = false;
            }
            if (toAnalyse[0][i + 4] * EX_SENS_4 > toAnalyse[0][i]) {
                max = false;
            }
            if (toAnalyse[0][i - 2] * EX_SENS_2 < toAnalyse[0][i]) {
                if (toAnalyse[0][i + 2] * EX_SENS_2 < toAnalyse[0][i]) {
                    max = true;
                }
            }
            if (toAnalyse[0][i - 5] * EX_SENS_5 < toAnalyse[0][i]) { //stop coming from down
                max = true;
            }
            for (int j = i - 5; j < i + 6; j++) {
                if (toAnalyse[0][j] > toAnalyse[0][i]) {
                    max = false;
                }
            }
            if (max) {
                anBuffer.extremes.add(new Extreme(toAnalyse[0][i], true, i));
                anBuffer.maximums.add(toAnalyse[0][i]);
                boolean covered = false;
                for (ResistanceLine line : anBuffer.exLines) {
                    if (line.isCoveringError(toAnalyse[0][i])) {
                        covered = true;
                    }
                }
                if (!covered) {
                    anBuffer.exLines.add(new ResistanceLine(toAnalyse[0][i], i));
                }
            }

            boolean min = true;
            if (toAnalyse[1][i - 4] / EX_SENS_4 < toAnalyse[1][i]) {
                min = false;
            }
            if (toAnalyse[1][i + 4] / EX_SENS_4 < toAnalyse[1][i]) {
                min = false;
            }
            if (toAnalyse[1][i - 2] / EX_SENS_2 > toAnalyse[1][i]) {
                if (toAnalyse[1][i + 2] / EX_SENS_2 > toAnalyse[1][i]) {
                    min = true;
                }
            }
            if (toAnalyse[1][i - 2] / EX_SENS_5 > toAnalyse[1][i]) { //stop coming from up
                min = true;
            }
            for (int j = i - 5; j < i + 6; j++) {
                if (toAnalyse[1][j] < toAnalyse[1][i]) {
                    min = false;
                }
            }
            if (min) {
                anBuffer.extremes.add(new Extreme(toAnalyse[1][i], false, i));
                anBuffer.minimums.add(toAnalyse[1][i]);
                boolean covered = false;
                for (ResistanceLine line : anBuffer.exLines) {
                    if (line.isCoveringError(toAnalyse[1][i])) {
                        covered = true;
                    }
                }
                if (!covered) {
                    anBuffer.exLines.add(new ResistanceLine(toAnalyse[1][i], i));
                }
            }
        }
    }

    private void analyseForTrendLines(){ //use extremes, not resLines
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
        for(Extreme ex : anBuffer.extremes){
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
            for(int i = (int)tl.coordinates.get(0).getX(); i<100; i++){
                if(tl.up){ //for up lines getting over down
                    //2nd closing
                    if((toAn.get(i).close < tl.getY(i))&&(!tl.isCovering(i, toAn.get(i).close))){
                        if((toAn.get(i).close < tl.getY(i))&&(!tl.isCovering(i, toAn.get(i).close))){
                            toDelete.add(tl);
                        }
                    }
                    //big difference
                    if(toAn.get(i).low * OVER_TREND_LINE < tl.getY(i)){
                        toDelete.add(tl);
                    }
                } else { //for down lines getting over up
                    //2nd closing
                    if((toAn.get(i).close > tl.getY(i))&&(!tl.isCovering(i, toAn.get(i).close))){
                        if((toAn.get(i).close > tl.getY(i))&&(!tl.isCovering(i, toAn.get(i).close))){
                            toDelete.add(tl);
                        }
                    }
                    //big difference
                    if(toAn.get(i).low / OVER_TREND_LINE > tl.getY(i)){
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
            anBuffer.trendLines.add(tl);
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


    /** returns a buffer with values of indicators */
    public AnalyserBuffer getBuffer(){
        return anBuffer;
    }

    /** clears all indicators. preparation for analysing new collection of quotations */
    public void clearBuffer(){
        anBuffer.clean();
    }

}
