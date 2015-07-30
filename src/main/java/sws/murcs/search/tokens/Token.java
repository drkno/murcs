package sws.murcs.search.tokens;

public abstract class Token {
    public abstract boolean matches(String query);

    public static final Token parse(final String input) {
        OrToken orToken = new OrToken();
        for (String or : input.split("\\|\\|")) {
            AndToken andToken = new AndToken();
            for (String and : or.split("&&")) {
                andToken.addToken(new SearchToken(and.trim()));
            }
            orToken.addToken(andToken);
        }
        return orToken;
    }
}
