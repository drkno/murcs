package sws.murcs.search.tokens;

public class SearchToken extends Token {

    private String searchTerm;

    public SearchToken(final String theSearchTerm) {
        searchTerm = theSearchTerm;
    }

    @Override
    public boolean matches(final String input) {
        return input.contains(searchTerm);
    }
}
