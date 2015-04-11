package sws.murcs.debug.sampledata;

import java.util.Random;

/**
 * Generates random names.
 */
public class NameGenerator {
    private static final Random random = new Random();
    private static final String[] firstNames = {"Fred", "Dave", "Joe", "Emma"};
    private static final String[] lastNames = {"Jones", "Davidson", "Egbert", "Fred"};
    private static final String[] titles = {"Mr", "Mrs", "Miss", "Ms", "Master"};

    /**
     * Generates a random name.
     * @return a new random name.
     */
    public static String randomName(){
        return randomName(firstNames, lastNames);
    }

    /**
     * Generates a random name, given a list of names to select from.
     * @param firstNames possible first names.
     * @param lastNames possible last names.
     * @return a new random name.
     */
    private static String randomName(String[] firstNames, String[] lastNames){
        return randomElement(firstNames) + " " + randomElement(lastNames);
    }

    /**
     * Generates a random title prefix (eg Mr, Mrs,...).
     * @return a random title.
     */
    public static String randomTitle(){
        return randomElement(titles);
    }

    /**
     * Generates a random string of lowercase letters.
     * @param length length of the generated string.
     * @return a random string of lowercase letters.
     */
    public static String randomString(int length){
        return randomString(length, "abcdefghijklmnopqrstuvwxyz");
    }

    /**
     * Selects a random element from an array of Strings.
     * @param from array to select element from.
     * @return a random element.
     */
    public static String randomElement(String[] from){
        return from[random.nextInt(from.length)];
    }

    /**
     * Generates a random string.
     * @param length length of the string to generate.
     * @param from alphabet to generate the random string from.
     * @return a random string.
     */
    public static String randomString(int length, String from){
        String result = "";

        while (length-- > 0){
            result += from.charAt(random.nextInt(from.length()));
        }

        return result;
    }

    /**
     * Gets the Lorem Ispum placeholder string.
     * @return the placeholder string.
     */
    public static String getLoremIpsum(){
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec facilisis ac quam in blandit. Donec ut velit erat. Vestibulum nec condimentum tellus. Morbi finibus augue vestibulum neque vehicula, nec auctor elit maximus. Etiam malesuada quis dui vitae luctus. Nulla lacinia felis vel tortor finibus, accumsan tempor risus pulvinar. Quisque sit amet nulla vitae justo imperdiet ornare. Phasellus venenatis mollis facilisis. Nullam pretium justo erat. Cras nibh velit, maximus pellentesque ex sed, faucibus congue neque. Morbi tempus odio nec sapien iaculis commodo. Quisque quis cursus elit. Sed in leo at purus malesuada vestibulum. Nunc id urna varius nibh malesuada vulputate. Nullam ultrices congue tortor, ut scelerisque augue.";
    }

    /**
     * Generates a random number between 0 and a maximum number.
     * @param max maximum number.
     * @return a random number.
     */
    public static int random(int max){
        return random(0, max);
    }

    /**
     * Generates a random number between a min and a max value.
     * @param min minimum value.
     * @param max maximum value.
     * @return a random number.
     */
    public static int random(int min, int max){
        return random.nextInt(max - min) + min;
    }
}
