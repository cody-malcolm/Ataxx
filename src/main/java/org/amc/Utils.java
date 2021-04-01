package org.amc;

/**
 * A static class that provides "utility" methods that are required by multiple classes
 */
public class Utils {

    /**
     * Convenience wrapper for java.lang.Thread.sleep to keep code neater elsewhere
     * @param duration
     */
    public static void sleep(long duration) {
        try {
            java.lang.Thread.sleep(duration);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

}
