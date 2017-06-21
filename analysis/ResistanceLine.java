package analysis;

public class ResistanceLine {
    public double high;
    public double low;
    public double middle;
    public double height;

    ResistanceLine(double middle){
        this.middle = middle;
        high = middle + 0.00015; //20?+ 15?~
        low = middle - 0.00015;
        height = high-low;
    }

    public boolean isCovering(double value){
        if((value<=high)&&(value>=low)){
            return true;
        } else {
            return false;
        }
    }

    public boolean isCoveringError(double value){
        if((value<=high+0.00010)&&(value>=low-0.00010)){
            return true;
        } else {
            return false;
        }
    }
}
