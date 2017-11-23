package analysis;

import static common.ForexConstants.*;

public class RelativeExtreme {
    public int index;
    public double value;
    public int n; //extr is in +- width (n)

    public RelativeExtreme(int width, int index, double value){
        this.n = width;
        this.index = index;
        this.value = value;
    }

    static void analyseForRelExtremes(double[][] toAnalyse, AnalyserBuffer anBuffer){
        int n = WIDTH_OF_REL_EX;
        for (int i = n; i < HIST_COUNT-1-n; i++) { //0--(n-1); (99-n)--99 could not be interpreted as extremes
            boolean max = true;
            for (int j = i - n; j <= i + n; j++) {
                if ((toAnalyse[0][j] > toAnalyse[0][i])&&(i!=j)) {
                    max = false;
                }
            }
            if (max) {
                anBuffer.relExtremes.add(new RelativeExtreme(n, i, toAnalyse[0][i]));
                anBuffer.relHighs.add(new RelativeExtreme(n, i, toAnalyse[0][i]));
                //System.out.println("relative HIGH "+i);
            }

            boolean min = true;
            for (int j = i - n; j <= i + n; j++) {
                if ((toAnalyse[1][j] < toAnalyse[1][i])&&(i!=j)) {
                    min = false;
                }
            }
            if (min) {
                anBuffer.relExtremes.add(new RelativeExtreme(n, i, toAnalyse[1][i]));
                anBuffer.relLows.add(new RelativeExtreme(n, i, toAnalyse[1][i]));
                //System.out.println("relative LOW "+i);
            }
        }
    }
}
