package analysis;

import buffer.QuotationBuffer;
import common.Quotation;

import java.util.ArrayList;

import static common.ForexConstants.*;

/** Represents a resistance line indicator */
public class ResistanceLine {
    public double high;
    public double low;
    public double middle;
    public double height;
    public int index;

    /** creates new one */
    ResistanceLine(double middle, int index){
        this.middle = middle;
        this.index = index;
        high = middle + RES_LINE_SENS;
        low = middle - RES_LINE_SENS;
        height = high-low;
    }

    /** returns true if the value is covered */
    public boolean isCovering(double value){
        if((value<=high)&&(value>=low)){
            return true;
        } else {
            return false;
        }
    }

    public static double getAdviceFor(Analyser analyser, String instrumentName, Quotation quo){
        int firstAnBuf;
        int indexOfBuffer = -1;
        for(QuotationBuffer buf : analyser.buffers){
            if(buf.name.equals(instrumentName)){
                indexOfBuffer = analyser.buffers.indexOf(buf);
                break;
            }
        }
        firstAnBuf = indexOfBuffer*PERIODS.size();
        AnalyserBuffer anBuffer = analyser.anBuffers.get(firstAnBuf);

        ArrayList<Quotation> history = analyser.buffers.get(indexOfBuffer).history5;
        int last = HIST_COUNT-1;

        double advice = ADVICE_STAY;

        boolean inALine = false;
        boolean fromUp = false;
        for(ResistanceLine line : anBuffer.exLines){
            //getting over up
            if(quo.high > line.middle*OVER_RES_LINE){
                if((history.get(last).close < line.high)||(history.get(last-1).close < line.high)){
                    return ADVICE_CLOSE_DOWN * ADV_EX_LINES;
                }
            }
            //commented because of better work
            if((history.get(98).close < line.high)&&(history.get(99).close >= line.high)&&(quo.close >= line.high)){
                return ADVICE_CLOSE_DOWN * ADV_EX_LINES;
            }
            //getting over down
            if(quo.low < line.middle/OVER_RES_LINE){
                if((history.get(last).close > line.low)||(history.get(last-1).close > line.low)){
                    return ADVICE_CLOSE_UP * ADV_EX_LINES;
                }
            }
            if((history.get(98).close > line.low)&&(history.get(99).close <= line.low)&&(quo.close <= line.low)){
                return ADVICE_CLOSE_UP * ADV_EX_LINES;
            }
            //resistance
            if(line.isCovering(quo.close) && !line.isCovering(history.get(last).close) && !line.isCovering(history.get(last-1).close)){
                inALine = true;
                if(history.get(last).close > quo.close){
                    fromUp = true;
                }
            }
        }
        //no getting over
        if(inALine){
            if(fromUp){ return ADVICE_UP * ADV_EX_LINES;}
            else { return ADVICE_DOWN * ADV_EX_LINES;}
        }
        else{
            return ADVICE_STAY * ADV_EX_LINES;
        }
    }
}
