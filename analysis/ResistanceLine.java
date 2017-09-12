package analysis;

import static common.ForexConstants.RES_LINE_SENS;

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

    /** returns true if the value is covered by the line with(out) error */
    public boolean isCoveringError(double value){
        if((value<=high+0.00010)&&(value>=low-0.00010)){
            return true;
        } else {
            return false;
        }
    }
}
