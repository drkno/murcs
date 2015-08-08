package sws.murcs.search.tokens;

import sws.murcs.search.SearchResult;

/**
 * Token that is used to check search criteria.
 */
public abstract class Token {

    /**
     * Checks for matches on a given string with the current search criteria.
     * @param query string to use as a search target.
     * @return null if no match found, or a SearchResult if a match was found.
     */
    public abstract SearchResult matches(final String query);

    public static final Token parse(final String input) {
        String searchQuery = input;

        // check if we want case sensitivity
        boolean caseSensitive = searchQuery.matches(".*(^|\\s+)!case($|\\s+).*");
        SearchToken.setIsCaseSensitive(caseSensitive);
        if (caseSensitive) {
            searchQuery = searchQuery.replaceAll("(^|\\s+)!case($|\\s+)", "");
        }

        // check if we want to force regex
        boolean regex = searchQuery.matches(".*(^|\\s+)!regex($|\\s+).*");
        SearchToken.setIsRegex(regex);
        if (regex) {
            searchQuery = searchQuery.replaceAll("(^|(\\s+))!regex($|(\\s+))", "");
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
