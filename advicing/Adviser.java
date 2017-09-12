package advicing;

import analysis.Analyser;
import analysis.AnalyserBuffer;
import analysis.ResistanceLine;
import analysis.TrendLine;
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

    public AnalyserBuffer getAnBuffer(){
        return anBuffer;
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
        analyser.analyse();
        anBuffer = analyser.getBuffer();

        int advice = ADVICE_STAY;
        int sum = summariseIndicators();

        if(sum>0) {
            if(sum >= UP_ADVICE_MIN_VALUE){
                advice = ADVICE_UP;
            } else if (sum >= CLOSE_DOWN_MIN_VALUE){
                advice = ADVICE_CLOSE_DOWN;
            } else {
                advice = ADVICE_STAY;
            }
        } else if(sum<0){
            if (sum <= DOWN_ADVICE_MAX_VALUE) {
                advice = ADVICE_DOWN;
            } else if (sum <= CLOSE_UP_MAX_VALUE){
                advice = ADVICE_CLOSE_UP;
            } else {
                advice = ADVICE_STAY;
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
        result += trLines();
        //...
        return result;
    }

    /** returns an advice based on exLines indicator */
    double exLines(){ //what to do relying on extreme resistance lines
        double advice = ADVICE_STAY;

        boolean inALine = false;
        boolean fromUp = false;
        for(ResistanceLine line : anBuffer.exLines){
            //getting over up
            if(quo.high > line.middle*OVER_RES_LINE){
                if((history.get(99).close < line.high)||(history.get(98).close < line.high)){
                    return ADVICE_CLOSE_DOWN * ADV_EX_LINES;
                }
            }
            //commented because of better work
            /*if((history.get(98).close < line.high)&&(history.get(99).close >= line.high)&&(quo.close >= line.high)){
                return ADVICE_CLOSE_DOWN * ADV_EX_LINES;
            }*/
            //getting over down
            if(quo.low < line.middle/OVER_RES_LINE){
                if((history.get(99).close > line.low)||(history.get(98).close > line.low)){
                    return ADVICE_CLOSE_UP * ADV_EX_LINES;
                }
            }
            /*if((history.get(98).close > line.low)&&(history.get(99).close <= line.low)&&(quo.close <= line.low)){
                return ADVICE_CLOSE_UP * ADV_EX_LINES;
            }*/
            //resistance
            if(line.isCovering(quo.close) && !line.isCoveringError(history.get(99).close) && !line.isCoveringError(history.get(98).close)){
                inALine = true;
                if(history.get(99).close > quo.close){
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

    double trLines(){
        double advice = ADVICE_STAY;
        for(TrendLine tl : anBuffer.trendLines){
            Quotation historyLast = history.get(history.size()-1);
            if(tl.up) {
                //getting over
                if ((historyLast.close < tl.getY(99)) && (!tl.isCovering(99, historyLast.close))) {
                    if ((quo.close) < tl.getY(100)&&(!tl.isCovering(100, quo.close))){
                        return ADVICE_CLOSE_UP * ADV_TREND_LINES;
                    }
                }
                if (quo.low * OVER_TREND_LINE < tl.getY(100)) {
                    return ADVICE_CLOSE_UP * ADV_TREND_LINES;
                }
                //covering
                if(tl.isCovering(100, quo.close)){
                    return ADVICE_UP * ADV_TREND_LINES;
                }
            } else {
                //getting over
                if ((historyLast.close > tl.getY(99)) && (!tl.isCovering(99, historyLast.close))) {
                    if ((quo.close) > tl.getY(100)&&(!tl.isCovering(100, quo.close))){
                        return ADVICE_CLOSE_DOWN * ADV_TREND_LINES;
                    }
                }
                if (quo.low / OVER_TREND_LINE > tl.getY(100)) {
                    return ADVICE_CLOSE_DOWN * ADV_TREND_LINES;
                }
                //covering
                if(tl.isCovering(100, quo.close)){
                    return ADVICE_DOWN * ADV_TREND_LINES;
                }
            }
        }

        return advice * ADV_TREND_LINES;
    }
}
