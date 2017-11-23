package advicing;

import analysis.*;
import buffer.QuotationBuffer;
import common.*;

import java.util.ArrayList;
import java.util.List;

import static common.ForexConstants.*;

/** Class to get advice from */
public class Adviser {
    public Analyser analyser;
    public ArrayList<QuotationBuffer> buffers;
    int last;
    Quotation quo; //new quotation
    double bid; //new bid
    int advice;
    int firstAnBuf;

    public Adviser(Analyser analyser){
        this.analyser = analyser;
        this.buffers = analyser.buffers;
        last = HIST_COUNT-1;
    }

    public int getAdvice(String instrumentName,  double bid){
        return getAdvice(instrumentName, new Quotation(bid, bid, bid, bid, 5));
    }

    public int getAdvice(String instrumentName, Quotation quo){


        this.quo = quo;
        int indexOfBuffer = -1;
        for(QuotationBuffer buf : analyser.buffers){
            if(buf.name.equals(instrumentName)){
                indexOfBuffer = analyser.buffers.indexOf(buf);
            }
        }
        firstAnBuf = indexOfBuffer*PERIODS.size();

        advice = ADVICE_STAY;
        analyse(instrumentName, quo);

        return advice;
    }



    void analyse(String instrumentName, Quotation quo){
        double sum = summariseIndicators(instrumentName, quo);

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
    }

    // returns an integer value of advice. absolute value of the returned value is dependence of the advice
    double summariseIndicators(String instrumentName, Quotation quo){
        double result = 0;
        result += ResistanceLine.getAdviceFor(analyser, instrumentName, quo);
        result += TrendLine.getAdviceFor(analyser, instrumentName, quo);
        result += TDSequence.getAdviceFor(analyser, instrumentName, quo);
        result += InnerTrendLine.getAdviceFor(analyser, instrumentName, quo);
        //...
        return result;
    }
}