package analysis;

import static common.ForexConstants.*;

public class Extreme {
    static Analyser owner;

    public double value;
    public boolean max;
    public int index;


    public Extreme(double value, boolean max, int index){
        this.value = value;
        this.max = max;
        this.index = index;
    }

    static void analyseForExtremes(Analyser analyser) {
        owner = analyser;
        for (int i = 5; i < HIST_COUNT-6; i++) { //0-4; 95-99 could not be interpreted as extremes
            boolean max = true;
            if (owner.toAnalyse[0][i - 4] * EX_SENS_4 > owner.toAnalyse[0][i]) {
                max = false;
            }
            if (owner.toAnalyse[0][i + 4] * EX_SENS_4 > owner.toAnalyse[0][i]) {
                max = false;
            }
            if (owner.toAnalyse[0][i - 2] * EX_SENS_2 < owner.toAnalyse[0][i]) {
                if (owner.toAnalyse[0][i + 2] * EX_SENS_2 < owner.toAnalyse[0][i]) {
                    max = true;
                }
            }
            if (owner.toAnalyse[0][i - 5] * EX_SENS_5 < owner.toAnalyse[0][i]) { //stop coming from down
                max = true;
            }
            for (int j = i - 5; j < i + 6; j++) {
                if (owner.toAnalyse[0][j] > owner.toAnalyse[0][i]) {
                    max = false;
                }
            }
            if (max) {
                owner.anBuffer.extremes.add(new Extreme(owner.toAnalyse[0][i], true, i));
                owner.anBuffer.maximums.add(owner.toAnalyse[0][i]);
                boolean covered = false;
                for (ResistanceLine line : owner.anBuffer.exLines) {
                    if (line.isCoveringError(owner.toAnalyse[0][i])) {
                        covered = true;
                    }
                }
                if (!covered) {
                    owner.anBuffer.exLines.add(new ResistanceLine(owner.toAnalyse[0][i], i));
                }
            }

            boolean min = true;
            if (owner.toAnalyse[1][i - 4] / EX_SENS_4 < owner.toAnalyse[1][i]) {
                min = false;
            }
            if (owner.toAnalyse[1][i + 4] / EX_SENS_4 < owner.toAnalyse[1][i]) {
                min = false;
            }
            if (owner.toAnalyse[1][i - 2] / EX_SENS_2 > owner.toAnalyse[1][i]) {
                if (owner.toAnalyse[1][i + 2] / EX_SENS_2 > owner.toAnalyse[1][i]) {
                    min = true;
                }
            }
            if (owner.toAnalyse[1][i - 2] / EX_SENS_5 > owner.toAnalyse[1][i]) { //stop coming from up
                min = true;
            }
            for (int j = i - 5; j < i + 6; j++) {
                if (owner.toAnalyse[1][j] < owner.toAnalyse[1][i]) {
                    min = false;
                }
            }
            if (min) {
                owner.anBuffer.extremes.add(new Extreme(owner.toAnalyse[1][i], false, i));
                owner.anBuffer.minimums.add(owner.toAnalyse[1][i]);
                boolean covered = false;
                for (ResistanceLine line : owner.anBuffer.exLines) {
                    if (line.isCoveringError(owner.toAnalyse[1][i])) {
                        covered = true;
                    }
                }
                if (!covered) {
                    owner.anBuffer.exLines.add(new ResistanceLine(owner.toAnalyse[1][i], i));
                }
            }
        }
    }
}
