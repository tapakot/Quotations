package advicing;

import analysis.Analyser;
import analysis.AnalyserBuffer;
import analysis.ResistanceLine;
import common.*;

import java.util.List;

import static common.ForexConstants.*;

/** Class to get advice from */
public class Adviser {
    Analyser analyser;
    AnalyserBuffer anBuffer;
    List<Quotation> history;
    Quotation quo; //new quotation
    double bid; //new bid

    /** initialisation */
    public Adviser(){
        analyser = new Analyser();
        anBuffer = analyser.getBuffer();
    }

    /** returns an advice in CONSTANT form.
     * @param forAdvice100 history to be analysed
     * @param quo a new quotation after which needed an advice
     * @return
     */
    public int getAdvice(List<Quotation> forAdvice100, Quotation quo){
        history = forAdvice100;
        this.quo = quo;
        analyser.setQuotationBuffer(forAdvice100);
        analyser.clearBuffer();
        analyser.analyse("extremes");
        anBuffer = analyser.getBuffer();

        int advice;
        int sum = summariseIndicators();
        if(sum>0) {
            if(sum > UP_ADVICE_MIN_VALUE){
                advice = ADVICE_UP;
            } else {
                advice = ADVICE_CLOSE_DOWN;
            }

        } else if(sum<0){
            if (sum < DOWN_ADVICE_MAX_VALUE) {
                advice = ADVICE_DOWN;
            } else {
                advice = ADVICE_CLOSE_UP;
            }
        } else {
            advice = ADVICE_STAY;
        }
        return advice;
    }

    public int getAdvice(List<Quotation> forAdvice100, double bid){
        return getAdvice(forAdvice100, new Quotation(bid, bid, bid, bid, (short)5));
    }

    /** returns an integer value of advice. absolute value of the returned value is dependence of the advice */
    int summariseIndicators(){
        int result = 0;
        result += exLines();
        //...
        return result;
    }

    /** returns an advice based on exLines indicator */
    int exLines(){ //what to do relying on extreme resistance lines
        int advice = ADVICE_STAY;

        boolean inALine = false;
        boolean fromUp = false;
        for(ResistanceLine line : anBuffer.exLines){
            //resistance
            if(line.isCoveringError(quo.close) && !line.isCoveringError(history.get(99).close) && !line.isCoveringError(history.get(98).close)){
                inALine = true;
                if(history.get(99).close > quo.close){
                    fromUp = true;
                }
            }

            //getting over up
            if(quo.high > line.middle*OVER_RES_LINE){
                if((history.get(99).close < line.high)||(history.get(98).close < line.high)){
                    return ADVICE_CLOSE_DOWN;
                }
            }
            //getting over down
            if(quo.low < line.middle/OVER_RES_LINE){
                if((history.get(99).close > line.low)||(history.get(98).close > line.low)){
                    return ADVICE_CLOSE_UP;
                }
            }
        }
        //no getting over
        if(inALine){
            if(fromUp){ return ADVICE_UP;}
            else { return ADVICE_DOWN;}
        }
        else{
            return ADVICE_STAY;
        }
    }
}
