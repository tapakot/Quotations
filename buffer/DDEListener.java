package buffer;

import com.pretty_tools.dde.client.DDEClientEventListener;
import common.Quotation;

public class DDEListener implements DDEClientEventListener{
    private QuotationBuffer buffer;
    private Quotation changingQuo;
    private String[] partedResult;

    boolean trueData;
    private double prevBid; //for close parameter
    private double maxAsk, maxBid, minAsk, minBid;


    public DDEListener(QuotationBuffer buffer){
        this.buffer = buffer;
        trueData = false; //because starts between 5-min moments
        changingQuo = new Quotation((short)5);
        changingQuo.open = 0;
        changingQuo.high = 0;
        changingQuo.low = 0;
        changingQuo.close = 0;
        partedResult = new String[3];
    }

    public void onDisconnect() {
        System.out.println("=======================Disconnected from real-time quos===========================");
    }

    public void onItemChanged(String topic, String item, String data) { //item == R1C?
        item = item.substring(3);
        int itemIndex = Integer.parseInt(item)-2; //0-bid, 1-ask, 2-time
        partedResult[itemIndex] = data.trim();
        if(itemIndex == 2) { // 1/3 times
            double bid = Double.parseDouble(partedResult[0]);
            double ask = Double.parseDouble(partedResult[1]);
            int minute = Integer.parseInt(partedResult[2].substring(4));
            Quotation quo = new Quotation((short)5);
            if((minute%5==0)){
                changingQuo.close = prevBid;
                changingQuo.high = maxBid;
                changingQuo.low = minBid;
                prevBid = bid;
                System.out.println("for 5 minutes: \nmax/minBid: "+ maxBid+"/"+minBid+"\nmax/minAsk: "+ maxAsk+"/"+minAsk);
                if(trueData){
                    System.out.println("\n*** ATTENTION: counting was started between 5-min moments. Unpredictable open, high and low params ***\n");
                    buffer.realTimeEvent(changingQuo);
                }
                trueData = true;
                maxAsk=0;
                maxBid=0;
                minAsk=0;
                minBid=0;
                changingQuo.open = bid;
                //prevMinute = minute;
            }
            else {
                if (bid > maxBid){maxBid=bid;}
                if ((bid < minBid)||(minBid == 0)){minBid=bid;}
                if (ask > maxAsk){maxAsk=ask;}
                if ((ask < minAsk)||(minAsk==0)){minAsk=ask;}
            }
            //System.out.print(minute + ": ");
            System.out.println(partedResult[0] + " " + partedResult[1] + " " + partedResult[2] + " " + itemIndex);
        }
    }
}
