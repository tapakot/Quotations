package common;

public class AdjustValue {
    public String section;
    public String name;
    public double defValue;

    public AdjustValue(String section, String name, double value){
        this.section = section;
        this.name = name;
        this.defValue = value;
    }
}
