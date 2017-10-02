package buffer;

import com.pretty_tools.dde.client.DDEClientEventListener;
import common.Quotation;

import static common.ForexConstants.HIST_COUNT;

/** listener class for DDE conversation */
class DDEListener implements DDEClientEventListener{
    private QuotationBuffer buffer;
    private Quotation changingQuo;
    private String[] partedResult;

    private double prevBid; //for close parameter
    private int prevMinute;
    private double maxAsk, maxBid, minAsk, minBid;

    /** initialising */
    public DDEListener(QuotationBuffer buffer){
        this.buffer = buffer;
        buffer.trueData = false; //because starts between 5-min moments
        changingQuo = new Quotation((short)5);
        changingQuo.open = buffer.getQuotation((short)5, HIST_COUNT-1).open;
        changingQuo.high = buffer.getQuotation((short)5, HIST_COUNT-1).high;
        maxBid = changingQuo.high;
        changingQuo.low = buffer.getQuotation((short)5, HIST_COUNT-1).low;
        minBid = changingQuo.low;
        changingQuo.close = 0;
        partedResult = new String[3];
        prevMinute = 0;
    }

    /** when disconnected. for ex.: file was closed by user. */
    public void onDisconnect() {
        System.out.println("=======================Disconnected from real-time quos===========================");
    }

    /** when new data came */
    public void onItemChanged(String topic, String item, String data) { //item == R1C?
        item = item.substring(3);
        int itemIndex = Integer.parseInt(item)-2; //0-bid, 1-ask, 2-time
        partedResult[itemIndex] = data.trim();
        if(itemIndex == 2) { // 1/3 times
            double bid = Double.parseDouble(partedResult[0]);
            double ask = Double.parseDouble(partedResult[1]);
            int minute = Integer.parseInt(partedResult[2].substring(1,2));
            buffer.bid = bid;
            buffer.ask = ask;
            if((minute%5==0)&&(prevMinute != minute)){ //NULL_POINTER_EXEPTION if starts at 00, 05
                changingQuo.close = prevBid; //изменился
                changingQuo.high = maxBid; //
                changingQuo.low = minBid;
                //System.out.println("for 5 minutes: \nmax/minBid: "+ maxBid+"/"+minBid+"\nmax/minAsk: "+ maxAsk+"/"+minAsk);

                    //System.out.println("\n*** ATTENTION: counting was started between 5-min moments. Unpredictable open, high and low params ***\n");
                buffer.realTimeEvent(changingQuo);
                buffer.trueData = true;
                maxAsk=0;
                maxBid=0;
                minAsk=0;
                minBid=0;
                changingQuo.open = bid;
            }
            else {
                if (bid > maxBid){maxBid=bid;}
                if ((bid < minBid)||(minBid == 0)){minBid=bid;}
                if (ask > maxAsk){maxAsk=ask;}
                if ((ask < minAsk)||(minAsk==0)){minAsk=ask;}
            }
            if(buffer.test){
                buffer.tester.newData();
            }
            prevMinute = minute;
            prevBid = bid;
            //System.out.println(partedResult[0] + " " + partedResult[1] + " " + partedResult[2] + " " + itemIndex);
        }
    }
}
