package analysis;

/** Represents a resistance line indicator */
public class ResistanceLine {
    public double high;
    public double low;
    public double middle;
    public double height;

    /** creates new one */
    ResistanceLine(double middle){
        this.middle = middle;
        high = middle + 0.00013; //20?+ 15?~+ 13?
        low = middle - 0.00013;
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
