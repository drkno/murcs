package sws.murcs.model.helpers;

import sws.murcs.model.Person;

import java.util.ArrayList;

public class RecentlyUsedHelper {

    private static RecentlyUsedHelper helper;

    private static ArrayList<Person> recentPeople;

    private static final int MAX_SIZE = 6;

    private RecentlyUsedHelper() {
        recentPeople = new ArrayList<Person>();
    }

    public static RecentlyUsedHelper get() {
        if (helper == null) {
            helper = new RecentlyUsedHelper();
        }
        return helper;
    }

    public static ArrayList<Person> getRecentPeople() {
        return recentPeople;
    }

    public static void addToRecentPeople(Person person) {
        if (!recentPeople.contains(person)) {
            recentPeople.add(person);
        }
        if (recentPeople.size() > MAX_SIZE) {
            recentPeople.remove(0);
        }
    }

    public static void clearRecentPeople() {
        recentPeople.clear();
    }
}
