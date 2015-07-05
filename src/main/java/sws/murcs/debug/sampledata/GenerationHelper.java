package sws.murcs.debug.sampledata;

import java.util.Random;

/**
 * Provides helper methods used within the generators.
 */
public final class GenerationHelper {

    /**
     * The random generator used in this class.
     */
    private static final Random RANDOM = new Random();

    /**
     * Private constructor for utility class.
     */
    private GenerationHelper() {
    }

    /**
     * Generates a random number between 0 and a maximum number.
     * @param max maximum number.
     * @return a random number.
     */
    public static int random(final int max) {
        return random(0, max);
    }

    /**
     * Generates a random number between a min and a max value.
     * @param min minimum value.
     * @param max maximum value.
     * @return a random number.
     */
    public static int random(final int min, final int max) {
        return RANDOM.nextInt(max - min) + min;
    }

    /**
     * Selects a random element from an array of Strings.
     * @param from array to select element from.
     * @return a random element.
     */
    public static String randomElement(final String[] from) {
        return from[RANDOM.nextInt(from.length)];
    }

    /**
     * Generates a random string.
     * @param length length of the string to generate.
     * @param from alphabet to generate the RANDOM string from.
     * @return a random string.
     */
    public static String randomString(final int length, final String from) {
        String result = "";
        int i = length;
        while (i-- > 0) {
            result += from.charAt(RANDOM.nextInt(from.length()));
        }
        return result;
    }

    /**
     * Generates a random string of lowercase letters.
     * @param length length of the generated string.
     * @return a random string of lowercase letters.
     */
    public static String randomString(final int length) {
        return GenerationHelper.randomString(length, "abcdefghijklmnopqrstuvwxyz");
    }
}
