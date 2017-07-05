package common;

import common.Worker;

/** main class. entry point.
 * needed only to start Worker.
 */
public class MainClass {
    /** entry point. starts worker.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args){
        Worker worker = new Worker();
        worker.work();
    }
}
