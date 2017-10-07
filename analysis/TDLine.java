package analysis;

import static common.ForexConstants.TD_LINE_SENS;

public class TDLine {
    public RelativeExtreme[] coordinates;
    double a;
    double b;

    TDLine(RelativeExtreme c1, RelativeExtreme c2){
        coordinates = new RelativeExtreme[2];
        coordinates[0] = c1;
        coordinates[1] = c2;

        a = (c1.value - c2.value)/(c1.index - c2.index);
        b = c1.value - c1.index*a;
    }

    public double getY(double x){
        return x*a+b;
    }

    public boolean isCovering(double x, double value){
        boolean covered = false;
        if((value < x*a + b + TD_LINE_SENS) && (value > x*a + b - TD_LINE_SENS)){
            covered = true;
        }
        return covered;
    }
}
