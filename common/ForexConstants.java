package common;

/** Class of constants.
 * Those constants are used in other classes.
 * Cannot be changed by user.
 */
public class ForexConstants {
    public static final int ADVICE_UP = 2;
    public static final int ADVICE_CLOSE_DOWN = 1;
    public static final int ADVICE_STAY = 0;
    public static final int ADVICE_CLOSE_UP = -1;
    public static final int ADVICE_DOWN = -2;
    public static final int UP_DIRECTION = 1;
    public static final int DOWN_DIRECTION = -1;
    public static final double COMMISSION = 0.01;
    public static final double START_BALANCE = 100;
    public static final int UP_ADVICE_MIN_VALUE = 1; //if the value is bigger advices to open up positions
    public static final int DOWN_ADVICE_MAX_VALUE = -1;

    public static final int WIDTH_OF_BAR = 2*2 + 2;
    public static final int gridPeriod = 18; //period of marks of the grid
}
