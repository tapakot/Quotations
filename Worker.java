import buffer.QuotationBuffer;
import common.Quotation;

public class Worker {
    public Worker(){}

    public void work(){
        QuotationBuffer buffer = new QuotationBuffer();
        buffer.startThread();
        while (!buffer.isReady){}
        Quotation quo =  buffer.getQuotation((short) 5, 99);
    }

}
