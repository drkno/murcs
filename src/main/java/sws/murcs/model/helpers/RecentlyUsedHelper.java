package sws.murcs.model.helpers;

import sws.murcs.model.Person;

import java.util.ArrayList;

/**
 * A helper to be used for getting recently used items.
 */
public final class RecentlyUsedHelper {

    /**
     * The instance of the recently used helper.
     */
    private static RecentlyUsedHelper instance;

    /**
     * The recently used people.
     */
    private static ArrayList<Person> recentPeople;

    /**
     * The maximum number of recently used items to keep a track of.
     */
    private static final int MAX_SIZE = 6;

    /**
     * A private constructor to make sure that you can only have once instance.
     */
    private RecentlyUsedHelper() {
        recentPeople = new ArrayList<Person>();
    }

    /**
     * Gets the current recently used helper. If one doesn't exist it creates a new one.
     * @return The current recently used helper.
     */
    public static RecentlyUsedHelper get() {
        if (instance == null) {
            instance = new RecentlyUsedHelper();
        }
        return instance;
    }

    /**
     * Gets the list of recently used people.
     * @return the list of recently used people.
     */
    public static ArrayList<Person> getRecentPeople() {
        return recentPeople;
    }

    /**
     * Adds a person to the recently used people and makes sure that there aren't
     * too many people in the recently used people list. If there are it removes the first one
     * put in the list.
     * @param person the person to add.
     */
    public static void addToRecentPeople(final Person person) {
        if (!recentPeople.contains(person)) {
            recentPeople.add(person);
        }
        if (recentPeople.size() > MAX_SIZE) {
            recentPeople.remove(0);
        }
    }

    /**
     * Clears all the recently used people.
     */
    public static void clearRecentPeople() {
        recentPeople.clear();
    }
}
