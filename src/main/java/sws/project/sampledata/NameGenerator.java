package sws.project.sampledata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 *
 */
public class NameGenerator {
    private static Random random = new Random();

    private static String[] firstNames = new String[]{"Fred", "Dave", "Joe", "Emma"};
    private static String[] lastNames = new String[]{"Jones", "Davidson", "Egbert", "Fred"};
    private static String[] titles = new String[]{"Mr", "Mrs", "Miss", "Ms", "Master"};

    public static String randomName(){
        return randomName(firstNames, lastNames);
    }

    public static String randomName(String[] firstNames, String[] lastNames){
        return randomElement(firstNames) + " " + randomElement(lastNames);
    }

    public static String randomTitle(){
        return randomElement(titles);
    }

    public static String randomString(int length){
        return randomString(length, "abcdefghijklmnopqrstuvwxyz");
    }

    public static String randomElement(String[] from){
        return from[random.nextInt(from.length)];
    }

    public static String randomString(int length, String from){
        String result = "";

        while (length-- > 0){
            result += from.charAt(random.nextInt(from.length()));
        }

        return result;
    }

    public static String getLoremIpsum(){
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec facilisis ac quam in blandit. Donec ut velit erat. Vestibulum nec condimentum tellus. Morbi finibus augue vestibulum neque vehicula, nec auctor elit maximus. Etiam malesuada quis dui vitae luctus. Nulla lacinia felis vel tortor finibus, accumsan tempor risus pulvinar. Quisque sit amet nulla vitae justo imperdiet ornare. Phasellus venenatis mollis facilisis. Nullam pretium justo erat. Cras nibh velit, maximus pellentesque ex sed, faucibus congue neque. Morbi tempus odio nec sapien iaculis commodo. Quisque quis cursus elit. Sed in leo at purus malesuada vestibulum. Nunc id urna varius nibh malesuada vulputate. Nullam ultrices congue tortor, ut scelerisque augue.";
    }

    public static int random(int max){
        return random(0, max);
    }

    public static int random(int min, int max){
        return random.nextInt(max - min) + min;
    }

    public static double random(){
        return random.nextDouble();
    }
}
