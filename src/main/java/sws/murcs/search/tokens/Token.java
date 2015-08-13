package sws.murcs.search.tokens;

import sws.murcs.search.SearchResult;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

/**
 * Token that is used to check search criteria.
 */
public abstract class Token {

    /**
     * Collection of special tokens to be used when setting up the compiler.
     */
    private static BangCommand[] specialTokens = new BangCommand[] {
        new BangCommand("regex", "reg", "Enables regular expressions.", SearchToken::setIsRegex),
        new BangCommand("case", "cas", "Enables case sensitivity.", SearchToken::setIsCaseSensitive),
        /*new BangCommand("current", "curr", "Searches the current display list.", ),
        new BangCommand("name", "nam", "Searches using display names only.", ),

        new BangCommand("backlog", "ba", "Searches backlogs.", ),
        new BangCommand("person", "pe", "Searches people.", ),
        new BangCommand("project", "pr", "Searches projects.", ),
        new BangCommand("release", "re", "Searches releases.", ),
        new BangCommand("skill", "sk", "Searches skills.", ),
        new BangCommand("story", "st", "Searches stories.", ),
        new BangCommand("team", "te", "Searches teams.", )*/
    };

    //private static boolean

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
        String searchQuery = input;

        // setup special tokens
        for (BangCommand specialToken : specialTokens) {
            String[] commands = specialToken.getCommands();
            System.out.println(".*(^|\\s+)((" + commands[0] + ")|(" + commands[1] + "))($|\\s+).*");
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
