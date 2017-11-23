package analysis;

import static common.ForexConstants.*;

public class Extreme {
    public double value;
    public boolean max;
    public int index;


    public Extreme(double value, boolean max, int index){
        this.value = value;
        this.max = max;
        this.index = index;
    }

    static void analyseForExtremes(double toAnalyse[][], AnalyserBuffer anBuffer) {
        for (int i = 5; i < HIST_COUNT-6; i++) { //0-4; 95-99 could not be interpreted as extremes
            boolean max = true;
            if (toAnalyse[0][i - 4] * EX_SENS_4 > toAnalyse[0][i]) {
                max = false;
            }
            if (toAnalyse[0][i + 4] * EX_SENS_4 > toAnalyse[0][i]) {
                max = false;
            }
            if (toAnalyse[0][i - 2] * EX_SENS_2 < toAnalyse[0][i]) {
                if (toAnalyse[0][i + 2] * EX_SENS_2 < toAnalyse[0][i]) {
                    max = true;
                }
            }
            if (toAnalyse[0][i - 5] * EX_SENS_5 < toAnalyse[0][i]) { //stop coming from down
                max = true;
            }
            for (int j = i - 5; j < i + 6; j++) {
                if (toAnalyse[0][j] > toAnalyse[0][i]) {
                    max = false;
                }
            }
            if (max) {
                anBuffer.extremes.add(new Extreme(toAnalyse[0][i], true, i));
                anBuffer.maximums.add(toAnalyse[0][i]);
                boolean covered = false;
                for (ResistanceLine line : anBuffer.exLines) {
                    if (line.isCovering(toAnalyse[0][i])) {
                        covered = true;
                    }
                }
                if (!covered) {
                    anBuffer.exLines.add(new ResistanceLine(toAnalyse[0][i], i));
                }
            }

            boolean min = true;
            if (toAnalyse[1][i - 4] / EX_SENS_4 < toAnalyse[1][i]) {
                min = false;
            }
            if (toAnalyse[1][i + 4] / EX_SENS_4 < toAnalyse[1][i]) {
                min = false;
            }
            if (toAnalyse[1][i - 2] / EX_SENS_2 > toAnalyse[1][i]) {
                if (toAnalyse[1][i + 2] / EX_SENS_2 > toAnalyse[1][i]) {
                    min = true;
                }
            }
            if (toAnalyse[1][i - 2] / EX_SENS_5 > toAnalyse[1][i]) { //stop coming from up
                min = true;
            }
            for (int j = i - 5; j < i + 6; j++) {
                if (toAnalyse[1][j] < toAnalyse[1][i]) {
                    min = false;
                }
            }
            if (min) {
                anBuffer.extremes.add(new Extreme(toAnalyse[1][i], false, i));
                anBuffer.minimums.add(toAnalyse[1][i]);
                boolean covered = false;
                for (ResistanceLine line : anBuffer.exLines) {
                    if (line.isCovering(toAnalyse[1][i])) {
                        covered = true;
                    }
                }
                if (!covered) {
                    anBuffer.exLines.add(new ResistanceLine(toAnalyse[1][i], i));
                }
            }
        }
    }
}
