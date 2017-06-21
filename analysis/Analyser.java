package analysis;

import buffer.QuotationBuffer;

import java.util.ArrayList;

public class Analyser {
    QuotationBuffer buffer;
    double[][] toAnalyse; // [highs][lows]


    public ArrayList<Double> maximums;
    public ArrayList<Double> minimums;
    public ArrayList<ResistanceLine> exLines;

    private double exDiff5 = 1.00013; //difference between nearby in % for extremums 35?+ 20?+ 13?~
    private double exDiff2 = 1.00019; //40

    public Analyser(){
        maximums = new ArrayList();
        minimums = new ArrayList();
        exLines = new ArrayList<ResistanceLine>();
        toAnalyse = new double[2][100];
    }

    public void setBuffer(QuotationBuffer buffer){
        this.buffer = buffer;
        for(int i=0; i<99; i++){
            toAnalyse[0][i] = buffer.getQuotation((short)5, i).high;
            toAnalyse[1][i] = buffer.getQuotation((short)5, i).low;
        }
    }

    public void analyse(String cmd){
        //extremums
        for(int i = 5; i<94; i++){ //0-4; 95-99 could not be interpreted as extremums
            boolean max = true;

            if(toAnalyse[0][i-4]*exDiff5 > toAnalyse[0][i]){ max = false;}
            if(toAnalyse[0][i+4]*exDiff5 > toAnalyse[0][i]){ max = false;}
            if(toAnalyse[0][i-2]*exDiff2 < toAnalyse[0][i]){
                if(toAnalyse[0][i+2]*exDiff2 < toAnalyse[0][i]){ max = true;}
            }
            for(int j=i-5; j<i+6; j++){
                if(toAnalyse[0][j]>toAnalyse[0][i]){
                    max = false;
                }
            }
            if(max){
                maximums.add(toAnalyse[0][i]);
                boolean covered = false;
                for(ResistanceLine line : exLines){
                    if(line.isCoveringError(toAnalyse[0][i])){ covered = true; }
                }
                if(!covered){ exLines.add(new ResistanceLine(toAnalyse[0][i])); }
            }

            boolean min = true;
            if(toAnalyse[1][i-4]/exDiff5 < toAnalyse[1][i]){ min = false;}
            if(toAnalyse[1][i+4]/exDiff5 < toAnalyse[1][i]){ min = false;}
            if(toAnalyse[1][i-2]/exDiff2 > toAnalyse[1][i]){
                if(toAnalyse[1][i+2]/exDiff2 > toAnalyse[1][i]){ min = true;}
            }
            for(int j=i-5; j<i+6; j++){
                if(toAnalyse[1][j]<toAnalyse[1][i]){
                    min = false;
                }
            }
            if(min){
                minimums.add(toAnalyse[1][i]);
                boolean covered = false;
                for(ResistanceLine line : exLines){
                    if(line.isCoveringError(toAnalyse[1][i])){ covered = true; }
                }
                if(!covered){ exLines.add(new ResistanceLine(toAnalyse[1][i])); }
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
}
