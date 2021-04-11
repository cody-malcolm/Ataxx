package org.amc;

import java.util.Random;

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
        
        if (username.equals("")||username.equals("-")){
            return false;
        }
        if (username.length()>16||username.contains("\\")) {
            return false;
        }

        return true;

    }

    // Acknowledgement: This is code reused from a previous assignment
    /**
     * Wraps the Random.nextInt method for more straightforward use.
     *
     * @param a an integer
     * @param b an integer
     * @return a random integer between a and b, inclusive
     */
    public static int randInt(int a, int b) {
        int min = Math.min(a, b);
        int range = Math.max(a, b) - min + 1;
        return new Random().nextInt(range) + min;
    }
}
