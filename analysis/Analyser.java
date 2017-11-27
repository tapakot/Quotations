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
    public ArrayList<QuotationBuffer> buffers;
    public ArrayList<AnalyserBuffer> anBuffers;
    double[][] toAnalyse; // [high, low][index]
    ArrayList<Quotation> toAn;

    /** initialisation */
    public Analyser(QuotationBuffer buffer){
        buffers = new ArrayList<>();
        anBuffers = new ArrayList<>();
        toAnalyse = new double[2][HIST_COUNT];
        toAn = new ArrayList<Quotation>();
        addBuffer(buffer);
    }

    public void addBuffer(QuotationBuffer buffer){
        buffers.add(buffer);
        for(int i =0; i<PERIODS.size(); i++){
            anBuffers.add(new AnalyserBuffer());
        }
        analyseAll();
    }

    public void analyseAll(){
        for(AnalyserBuffer anBuf : anBuffers){
            anBuf.clean();
        }
        for(QuotationBuffer buf : buffers){
            int j=0;
            for(String period : PERIODS) {
                int p = Integer.parseInt(period);
                //what to analyse
                toAn.clear();
                for (int i = 0; i < HIST_COUNT; i++) {
                    toAnalyse[0][i] = buf.getQuotation(p, i).high;
                    toAnalyse[1][i] = buf.getQuotation(p, i).low;
                    toAn.add(buf.getQuotation(p, i));       //same but quos
                }
                //where to put
                AnalyserBuffer dest = anBuffers.get(buffers.indexOf(buf)*PERIODS.size() + j);
                j++;
                //analyse
                analyse(dest);
            }
        }
    }

    /** sets QuotationBuffer to be analysed */
    /*public void setQuotationBuffer(QuotationBuffer buffer){     //separated method because it can be test/history/real
        this.buffer = buffer;
        toAn.clear();
        for(int i=0; i<HIST_COUNT; i++){
            toAnalyse[0][i] = buffer.getQuotation((short)5, i).high;
            toAnalyse[1][i] = buffer.getQuotation((short)5, i).low;
            toAn.add(buffer.getQuotation((short)5, i));
        }
    }*/

    /** sets a buffer to be analysed by List of quotations */
    /*public void setQuotationBuffer(List<Quotation> buffer100){
        toAn.clear();
        for(int i=0; i<HIST_COUNT; i++){ //99
            toAnalyse[0][i] = buffer100.get(i).high;
            toAnalyse[1][i] = buffer100.get(i).low;
            toAn.add(buffer100.get(i));
        }
    }*/

    /** sets all indicators in buffer */
    /*public void analyse(String cmd){
        switch(cmd){
            case "extremes": Extreme.analyseForExtremes(this);
                break;
            case "trendLines":
                Extreme.analyseForExtremes(this);
                TrendLine.analyseForTrendLines(this);
                break;
            case "relExtremes": RelativeExtreme.analyseForRelExtremes(this, WIDTH_OF_REL_EX);
                break;
            case "TDSequence":
                RelativeExtreme.analyseForRelExtremes(this, WIDTH_OF_REL_EX);
                TDSequence.analyseForTDSequence(this);
                break;
            case "InnerLines":
                RelativeExtreme.analyseForRelExtremes(this, WIDTH_OF_REL_EX);
                InnerTrendLine.analyseForInnerTrendLine(this);
                break;
            case "MA":
                anBuffer.movingAverages.add(new MovingAverage(this, DEFAULT_MA));
        }
    }*/

    public void analyse(AnalyserBuffer anBuf){
        Extreme.analyseForExtremes(toAnalyse, anBuf);
        TrendLine.analyseForTrendLines(toAn, anBuf);
        RelativeExtreme.analyseForRelExtremes(toAnalyse, anBuf);
        TDSequence.analyseForTDSequence(toAnalyse, anBuf);
        InnerTrendLine.analyseForInnerTrendLine(anBuf);
        anBuf.movingAverages.add(new MovingAverage(toAn, 14));
        RSI.analyseFor(toAn, anBuf, 14);
    }

    /** returns a buffer with values of indicators */
    /*public AnalyserBuffer getBuffer(){
        return anBuffer;
    }*/

    /** clears all indicators. preparation for analysing new collection of quotations */
    /*public void clearBuffer(){
        anBuffer.clean();
    }*/

    /*public void analyseForMA(int length){
        anBuffer.movingAverages.add(new MovingAverage(this, length));
    }*/

}
