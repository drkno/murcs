package sws.murcs.debug.sampledata;

import java.util.Random;

/**
 * Generates random names.
 */
public final class NameGenerator {

    /**
     * A black private constructor as this is a utility class.
     */
    private NameGenerator() {
        // Blank constructor as this is a utility class.
    }

    /**
     * The random generator used in this class.
     */
    private static final Random RANDOM = new Random();

    /**
     * A list of first names.
     */
    private static final String[] FIRST_NAMES = {
            "Fred",
            "Dave",
            "Joe",
            "Emma",
            "Violeta",
            "Aleida",
            "Despina",
            "Trevor",
            "Alvera",
            "Doria",
            "Halley",
            "Alessandra",
            "Elsy",
            "Daphne",
            "Artie",
            "Tu",
            "Jeanna",
            "Pura",
            "Jena",
            "Reba",
            "Joetta",
            "Major",
            "Aide",
            "Jacqui",
            "Hannah",
            "Beula",
            "Naomi",
            "Garland",
            "Trudi",
            "Ruby",
            "Annie",
            "Maryland",
            "Shelly",
            "Epifania",
            "Sheri",
            "Yi",
            "Jona",
            "Kasi",
            "Mathew",
            "Phyllis",
            "Flora",
            "So",
            "Mellie",
            "Ron",
            "Diego",
            "Gale",
            "Vita",
            "Earlean",
            "Joana",
            "Autumn",
            "Jenae",
            "Everett",
            "Delta",
            "Dorinda"
    };

    /**
     * A list of last names.
     */
    private static final String[] LAST_NAMES = {
            "Jones",
            "Davidson",
            "Egbert",
            "Fred",
            "Henson",
            "Case",
            "Peters",
            "Holder",
            "Graves",
            "Rush",
            "Maynard",
            "Greer",
            "Hanna",
            "Owen",
            "Bauer",
            "Marshall",
            "Craig",
            "Allison",
            "Wood",
            "Short",
            "Gregory",
            "Davidson",
            "Ballard",
            "Sanford",
            "Harding",
            "Casey",
            "Randolph",
            "Hartman",
            "Hart",
            "Wyatt",
            "Benson",
            "Benton",
            "Burns",
            "Moody",
            "Serrano",
            "Decker",
            "Sims",
            "Moran",
            "Walton",
            "Bruce",
            "Whitney",
            "Rowland",
            "Wolfe",
            "Harris",
            "Bass",
            "Hayden",
            "Sparks",
            "Hutchinson",
            "Whitehead",
            "Bullock",
            "Mccormick",
            "Ross",
            "Sutton",
            "Gonzalez"
    };

    /**
     * A list of titles.
     */
    private static final String[] TITLES = {
            "Mr",
            "Mrs",
            "Miss",
            "Ms",
            "Master"
    };

    /**
     * Generates a random name.
     * @return a new random name.
     */
    public static String randomName() {
        return randomName(FIRST_NAMES, LAST_NAMES);
    }

    /**
     * Generates a random name, given a list of names to select from.
     * @param firstNames possible first names.
     * @param lastNames possible last names.
     * @return a new random name.
     */
    private static String randomName(final String[] firstNames, final String[] lastNames) {
        return randomElement(firstNames) + " " + randomElement(lastNames);
    }

    /**
     * Generates a random title prefix (eg Mr, Mrs,...).
     * @return a random title.
     */
    public static String randomTitle() {
        return randomElement(TITLES);
    }

    /**
     * Generates a random string of lowercase letters.
     * @param length length of the generated string.
     * @return a random string of lowercase letters.
     */
    public static String randomString(final int length) {
        return randomString(length, "abcdefghijklmnopqrstuvwxyz");
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
     * Gets the Lorem Ispum placeholder string.
     * @return the placeholder string.
     */
    public static String getLoremIpsum() {
        return "Lorem ipsum dolor sit amet, consectetur "
                + "adipiscing elit. Donec facilisis ac quam in "
                + "blandit. Donec ut velit erat. Vestibulum nec "
                + "condimentum tellus. Morbi finibus augue vestibulum "
                + "neque vehicula, nec auctor elit maximus. Etiam "
                + "malesuada quis dui vitae luctus. Nulla lacinia "
                + "felis vel tortor finibus, accumsan tempor risus "
                + "pulvinar. Quisque sit amet nulla vitae justo "
                + "imperdiet ornare. Phasellus venenatis mollis "
                + "facilisis. Nullam pretium justo erat. Cras nibh "
                + "velit, maximus pellentesque ex sed, faucibus "
                + "congue neque. Morbi tempus odio nec sapien "
                + "iaculis commodo. Quisque quis cursus elit. Sed "
                + "in leo at purus malesuada vestibulum. Nunc id "
                + "urna varius nibh malesuada vulputate. Nullam "
                + "ultrices congue tortor, ut scelerisque augue.";
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
}
