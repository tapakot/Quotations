package analysis;

import buffer.QuotationBuffer;
import common.Quotation;

import java.util.ArrayList;
import java.util.List;

/** Main class of the package.
 * An analyser itself. Needed to analyse history.
 * Sets the values of indicators which are put in buffer.
 */
public class Analyser {
    QuotationBuffer buffer;
    AnalyserBuffer anBuffer;
    ArrayList<Quotation> buffer100;
    double[][] toAnalyse; // [highs][lows]

    //defines sensitivity to extremes
    private double exDiff4 = 1.00013; //difference between nearby in % for extremums 35?+ 20?+ 13?~
    private double exDiff2 = 1.00019; //40

    /** initialisation */
    public Analyser(){
        anBuffer = new AnalyserBuffer();
        toAnalyse = new double[2][100];
    }

    /** sets QuotationBuffer to be analyse */
    public void setQuotationBuffer(QuotationBuffer buffer){     //separated method because it can be test/history/real
        this.buffer = buffer;
        for(int i=0; i<99; i++){
            toAnalyse[0][i] = buffer.getQuotation((short)5, i).high;
            toAnalyse[1][i] = buffer.getQuotation((short)5, i).low;
        }
    }

    /** sets a buffer to be analysed by List of quotations */
    public void setQuotationBuffer(List<Quotation> buffer100){
        for(int i=0; i<99; i++){
            toAnalyse[0][i] = buffer100.get(i).high;
            toAnalyse[1][i] = buffer100.get(i).low;
        }
    }

    /** sets all indicators in buffer */
    public void analyse(String cmd){

        //extremes
        for(int i = 5; i<94; i++){ //0-4; 95-99 could not be interpreted as extremums
            boolean max = true;
            if(toAnalyse[0][i-4]*exDiff4 > toAnalyse[0][i]){ max = false;}
            if(toAnalyse[0][i+4]*exDiff4 > toAnalyse[0][i]){ max = false;}
            if(toAnalyse[0][i-2]*exDiff2 < toAnalyse[0][i]){
                if(toAnalyse[0][i+2]*exDiff2 < toAnalyse[0][i]){ max = true;}
            }
            for(int j=i-5; j<i+6; j++){
                if(toAnalyse[0][j]>toAnalyse[0][i]){
                    max = false;
                }
            }
            if(max){
                anBuffer.maximums.add(toAnalyse[0][i]);
                boolean covered = false;
                for(ResistanceLine line : anBuffer.exLines){
                    if(line.isCoveringError(toAnalyse[0][i])){ covered = true; }
                }
                if(!covered){ anBuffer.exLines.add(new ResistanceLine(toAnalyse[0][i])); }
            }

            boolean min = true;
            if(toAnalyse[1][i-4]/exDiff4 < toAnalyse[1][i]){ min = false;}
            if(toAnalyse[1][i+4]/exDiff4 < toAnalyse[1][i]){ min = false;}
            if(toAnalyse[1][i-2]/exDiff2 > toAnalyse[1][i]){
                if(toAnalyse[1][i+2]/exDiff2 > toAnalyse[1][i]){ min = true;}
            }
            for(int j=i-5; j<i+6; j++){
                if(toAnalyse[1][j]<toAnalyse[1][i]){
                    min = false;
                }
            }
            if(min){
                anBuffer.minimums.add(toAnalyse[1][i]);
                boolean covered = false;
                for(ResistanceLine line : anBuffer.exLines){
                    if(line.isCoveringError(toAnalyse[1][i])){ covered = true; }
                }
                if(!covered){ anBuffer.exLines.add(new ResistanceLine(toAnalyse[1][i])); }
            }
            /*if((toAnalyse[0][i-1]>toAnalyse[0][i-2])&&(toAnalyse[0][i]>toAnalyse[0][i-1])&&(toAnalyse[0][i+1]<toAnalyse[0][i])&&(toAnalyse[0][i+2]<toAnalyse[0][i+1])){ //highs
                if((toAnalyse[1][i-1]>toAnalyse[1][i-2])&&(toAnalyse[1][i]>toAnalyse[1][i-1])&&(toAnalyse[1][i+1]<toAnalyse[1][i])&&(toAnalyse[1][i+2]<toAnalyse[1][i+1])) {//lows
                    //i is maximum
                    maximums.add(toAnalyse[0][i]); //high added
                }
            } else {
                if((toAnalyse[0][i-1]*exDiff < toAnalyse[0][i])&&(toAnalyse[0][i+1]*exDiff < toAnalyse[0][i])){
                    //i is maximum
                    maximums.add(toAnalyse[0][i]); //high added
                } else {
                    if((toAnalyse[0][i-2]*exDiff2 < toAnalyse[0][i])&&(toAnalyse[0][i+2]*exDiff2 < toAnalyse[0][i])) {
                        //i is maximum
                        maximums.add(toAnalyse[0][i]); //high added
                    }
                }
            }*/
        }
    }

    /** returns a buffer with values of indicators */
    public AnalyserBuffer getBuffer(){
        return anBuffer;
    }

    /** clears all indicators. preparation for analysing new collection of quotations */
    public void clearBuffer(){
        anBuffer = new AnalyserBuffer();
    }

}
