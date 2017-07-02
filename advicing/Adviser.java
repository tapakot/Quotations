package advicing;

import analysis.Analyser;
import analysis.AnalyserBuffer;
import analysis.ResistanceLine;
import buffer.QuotationBuffer;
import common.*;

import java.util.ArrayList;
import java.util.List;

import static common.ForexConstants.*;


public class Adviser {
    Analyser analyser;
    AnalyserBuffer anBuffer;
    List<Quotation> history;
    Quotation quo; //new quotation

    public Adviser(){
        analyser = new Analyser();
        anBuffer = analyser.getBuffer();
    }

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
            if(sum > 1){
                advice = ADVICE_UP;
            } else {
                advice = ADVICE_CLOSE_DOWN;
            }

        } else if(sum<0){
            if (sum < -1) {
                advice = ADVICE_DOWN;
            } else {
                advice = ADVICE_CLOSE_UP;
            }
        } else {
            advice = ADVICE_STAY;
        }
        return advice;
    }

    int summariseIndicators(){
        int result = 0;
        result += exLines();
        //...
        return result;
    }

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
            if(quo.high > line.middle*1.10){
                if((history.get(99).close < line.high)||(history.get(98).close < line.high)){
                    return ADVICE_CLOSE_DOWN;
                }
            }
            //getting over down
            if(quo.low < line.middle/1.10){
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
