package sws.murcs.search.tokens;

public abstract class Token {
    public abstract boolean matches(String query);

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
}
