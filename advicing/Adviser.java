package advicing;

import buffer.QuotationBuffer;
import common.*;

import java.util.ArrayList;

import static common.ForexConstants.*;


public class Adviser {

    QuotationBuffer buffer;

    public Adviser(){
        this.buffer = buffer;
    }

    public int getAdvice(ArrayList<Quotation> forAdvice100, Quotation quo){
        summariseIndicators();
        int advice = ADVICE_STAY;

        return advice;
    }

    void summariseIndicators(){
        int exLines_f = exLines();
        //...
    }

    int exLines(){ //what to do relying on extreme resistance lines
        int advice = ADVICE_STAY;


        return advice;
    }
}
