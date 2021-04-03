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

    /**
     * Returns true if the given username is valid, false otherwise.
     *
     * @param username the username to verify
     * @return true if the username is valid, false otherwise
     */
    public static boolean verifyUsername(String username) {
        // TODO Needs to verify username - can't be "", "-", or contain a '\' - also should have a reasonable max characters
        return true;
    }

}
