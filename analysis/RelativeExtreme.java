package analysis;

import static common.ForexConstants.*;

public class RelativeExtreme {
    static Analyser owner;
    public int index;
    public double value;
    public int n; //extr is in +- width (n)

    public RelativeExtreme(int width, int index, double value){
        this.n = width;
        this.index = index;
        this.value = value;
    }

    static void analyseForRelExtremes(Analyser analyser, int n){
        owner = analyser;
        for (int i = n; i < HIST_COUNT-1-n; i++) { //0--(n-1); (99-n)--99 could not be interpreted as extremes
            boolean max = true;
            for (int j = i - n; j <= i + n; j++) {
                if ((owner.toAnalyse[0][j] > owner.toAnalyse[0][i])&&(i!=j)) {
                    max = false;
                }
            }
            if (max) {
                owner.anBuffer.relExtremes.add(new RelativeExtreme(n, i, owner.toAnalyse[0][i]));
                owner.anBuffer.relHighs.add(new RelativeExtreme(n, i, owner.toAnalyse[0][i]));
                //System.out.println("relative HIGH "+i);
            }

            boolean min = true;
            for (int j = i - n; j <= i + n; j++) {
                if ((owner.toAnalyse[1][j] < owner.toAnalyse[1][i])&&(i!=j)) {
                    min = false;
                }
            }
            if (min) {
                owner.anBuffer.relExtremes.add(new RelativeExtreme(n, i, owner.toAnalyse[1][i]));
                owner.anBuffer.relLows.add(new RelativeExtreme(n, i, owner.toAnalyse[1][i]));
                //System.out.println("relative LOW "+i);
            }
        }
    }
}
