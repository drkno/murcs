package sws.murcs.search.tokens;

import sws.murcs.search.SearchPriority;
import sws.murcs.search.SearchResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Token that is used to check search criteria.
 */
public abstract class Token {

    /**
     * Should only search display names.
     */
    private static boolean displayNamesOnly;

    /**
     * Collection of the types that should be searched.
     */
    private static Collection<Integer> searchTypes;

    /**
     * Collection of special tokens to be used when setting up the compiler.
     */
    private static BangCommand[] specialTokens = new BangCommand[] {
        new BangCommand("regex", "reg", "Enables regular expressions.", SearchToken::setIsRegex),
        new BangCommand("case", "cas", "Enables case sensitivity.", SearchToken::setIsCaseSensitive),
        //new BangCommand("current", "curr", "Searches the current display list.", ),
        new BangCommand("name", "nam", "Searches using display names only.", v -> displayNamesOnly = v),
        new BangCommand("backlog", "ba", "Searches backlogs.", v -> { if (v) searchTypes.add(0); }),
        new BangCommand("people", "pe", "Searches people.", v -> { if (v) searchTypes.add(1); }),
        new BangCommand("project", "pr", "Searches projects.", v -> { if (v) searchTypes.add(2); }),
        new BangCommand("release", "re", "Searches releases.", v -> { if (v) searchTypes.add(3); }),
        new BangCommand("skill", "sk", "Searches skills.", v -> { if (v) searchTypes.add(4); }),
        new BangCommand("story", "st", "Searches stories.", v -> { if (v) searchTypes.add(5); }),
        new BangCommand("team", "te", "Searches teams.", v -> { if (v) searchTypes.add(6); })
    };

    /**
     * Gets the maximum search priority that should be searched.
     * @return the search priority.
     */
    public static SearchPriority getMaxSearchPriority() {
        return displayNamesOnly ? SearchPriority.Ultra : SearchPriority.Low;
    }

    /**
     * Gets the types that should be searched.
     * @return a list of indexes of the types that should be searched.
     * These are as follows:
     *  0. Backlog
     *  1. People
     *  2. Project
     *  3. Release
     *  4. Skill
     *  5. Story
     *  6. Team
     */
    public static Collection<Integer> getSearchTypes() {
        return Collections.unmodifiableCollection(searchTypes);
    }

    /**
     * Gets the special tokens that can be used while searching.
     * @return the special tokens.
     */
    public static BangCommand[] getSpecialTokens() {
        return specialTokens;
    }

    /**
     * Checks for matches on a given string with the current search criteria.
     * @param query string to use as a search target.
     * @return null if no match found, or a SearchResult if a match was found.
     */
    public abstract SearchResult matches(final String query);

    /**
     * Pareses input into a Token that can be used for search queries.
     * @param input the input string to be parsed.
     * @return a token to be used for searching, or null if no Token can be created.
     */
    public static Token parse(final String input) {
        searchTypes = new ArrayList<>();
        String searchQuery = input;

        // setup special tokens
        for (BangCommand specialToken : specialTokens) {
            String[] commands = specialToken.getCommands();
            boolean enabled = searchQuery.matches(".*(^|\\s+)((" + commands[0] + ")|(" + commands[1] + "))($|\\s+).*");
            specialToken.setValue(enabled);
            if (enabled) {
                searchQuery
                        = searchQuery.replaceAll("(^|\\s+)((" + commands[0] + ")|(" + commands[1] + "))($|\\s+)", "");
            }
        }

        searchQuery = searchQuery.trim();
        if (searchQuery.isEmpty()) {
            return new BlankToken();
        }

        OrToken orToken = new OrToken();
        for (String or : searchQuery.split("\\|\\|")) {
            AndToken andToken = new AndToken();
            for (String and : or.split("&&")) {
                andToken.addToken(new SearchToken(and.trim()));
            }
            orToken.addToken(andToken);
        }
        return orToken;
    }

    /**
     * Determines if the token is empty (no search query).
     * @return if the entered search query was empty.
     */
    public boolean isEmpty() {
        return false;
    }
}
