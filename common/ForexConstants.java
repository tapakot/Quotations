package common;

/** Class of constants.
 * Those constants are used in other classes.
 * Cannot be changed by user.
 */
public class ForexConstants {
    public static final int UP_COUNTER = 1; //to not make positions too frequently. bigger value - less pos
    public static final int DOWN_COUNTER = 1;
    public static final int ADVICE_UP = 2;
    public static final int ADVICE_CLOSE_DOWN = 1;
    public static final int ADVICE_STAY = 0;
    public static final int ADVICE_CLOSE_UP = -1;
    public static final int ADVICE_DOWN = -2;
    public static final int UP_DIRECTION = 1;
    public static final int DOWN_DIRECTION = -1;
    public static final double COMMISSION = 0.04;
    public static final double TAKE_PROFIT = 1;
    public static final double START_BALANCE = 1000;
    public static final int UP_ADVICE_MIN_VALUE = 2; //if the value is bigger advices to open up positions
    public static final int DOWN_ADVICE_MAX_VALUE = -2;
    public static final double CLOSE_DOWN_MIN_VALUE = 1;
    public static final double CLOSE_UP_MAX_VALUE = -1;

    public static final double OVER_RES_LINE = 1.001; //1.001;
    public static final double OVER_TREND_LINE = 1.001;

    public static final double EX_SENS_4 = 1.00013;// 35?+ 20?+ 13?~
    public static final double EX_SENS_2 = 1.00019;//defines sensitivity to extremes (for finding)
    public static final double EX_SENS_5 = 1.00019;

    public static final double RES_LINE_SENS = 0.00013; //for covering 20?+ 15?~+ 13?
    public static final double TREND_LINE_SENS = 0.00013; //for covering 17?

    //coefficient of ind for adviser
    public static final double ADV_EX_LINES = 1;
    public static final double ADV_TREND_LINES = 0;

    public static final int WIDTH_OF_BAR = 2*2 + 2;
    public static final int gridPeriod = 18; //period of marks of the grid
}
