package sws.murcs.search.tokens;

import java.util.Collection;
import java.util.LinkedList;

public class AndToken extends Token {
    private Collection<Token> tokenCollection;

    public AndToken() {
        tokenCollection = new LinkedList<>();
    }

    public void addToken(Token token) {
        tokenCollection.add(token);
    }

    @Override
    public boolean matches(final String query) {
        return tokenCollection.stream().allMatch(token -> token.matches(query));
    }
}
