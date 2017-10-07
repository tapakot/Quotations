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
        toAnalyse = new double[2][HIST_COUNT];
        toAn = new ArrayList<Quotation>();
    }

    /** sets QuotationBuffer to be analyse */
    public void setQuotationBuffer(QuotationBuffer buffer){     //separated method because it can be test/history/real
        this.buffer = buffer;
        for(int i=0; i<HIST_COUNT; i++){
            toAnalyse[0][i] = buffer.getQuotation((short)5, i).high;
            toAnalyse[1][i] = buffer.getQuotation((short)5, i).low;
            toAn.add(buffer.getQuotation((short)5, i));
        }
    }

    /** sets a buffer to be analysed by List of quotations */
    public void setQuotationBuffer(List<Quotation> buffer100){
        for(int i=0; i<HIST_COUNT; i++){ //99
            toAnalyse[0][i] = buffer100.get(i).high;
            toAnalyse[1][i] = buffer100.get(i).low;
            toAn.add(buffer100.get(i));
        }
    }

    /** sets all indicators in buffer */
    public void analyse(String cmd){
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
        }
    }

    public void analyse(){
        Extreme.analyseForExtremes(this);
        TrendLine.analyseForTrendLines(this);
        RelativeExtreme.analyseForRelExtremes(this, WIDTH_OF_REL_EX);
        TDSequence.analyseForTDSequence(this);
        InnerTrendLine.analyseForInnerTrendLine(this);
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
