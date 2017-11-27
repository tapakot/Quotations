package analysis;

import buffer.QuotationBuffer;
import common.Quotation;

import java.util.ArrayList;

import static common.ForexConstants.*;

public class RSI {

    public static void analyseFor(ArrayList<Quotation> toAnalyse, AnalyserBuffer anBuffer, int period){
        anBuffer.rsi.clear();
        for(int i=period; i<=HIST_COUNT-1; i++){
            double positiveAverage=0;
            double negativeAverage=0;
            for(int j=i-period+1; j<=i; j++){
                double dif=(int)(toAnalyse.get(j).close*100000)-(int)(toAnalyse.get(j-1).close*100000);
                if(dif>0){
                    positiveAverage+=dif;
                }else{
                    negativeAverage-=dif;
                }
            }
            positiveAverage/=100000;
            positiveAverage/=period;
            negativeAverage/=100000;
            negativeAverage/=period;
            double rsi = positiveAverage/negativeAverage;
            rsi = 100 - 100/(1+rsi);
            anBuffer.rsi.add(rsi);
        }
    }

    public static double getAdviceFor(Analyser analyser, String instrumentName, Quotation quo){
        int firstAnBuf;
        int indexOfBuffer = -1;
        for(QuotationBuffer buf : analyser.buffers){
            if(buf.name.equals(instrumentName)){
                indexOfBuffer = analyser.buffers.indexOf(buf);
            }
        }
        firstAnBuf = indexOfBuffer*PERIODS.size();
        AnalyserBuffer anBuffer = analyser.anBuffers.get(firstAnBuf);

        double lastRSI = anBuffer.rsi.get(anBuffer.rsi.size()-1);
        if(lastRSI>70){
            return ADVICE_DOWN * ADV_RSI;
        }else if(lastRSI<30){
            return ADVICE_UP * ADV_RSI;
        }else{
            return ADVICE_STAY;
        }
    }
}
