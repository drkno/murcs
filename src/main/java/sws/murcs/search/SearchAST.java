package sws.murcs.search;

public class SearchAST {
    private enum Token {
        And,
        Or,
        Regex,
        Text
    }

    public SearchAST(final String searchQuery) {

    }



    private Token nextToken(String searchSegment) {
        if (searchSegment.equals("&&")) {
            return Token.And;
        }
        else if (searchSegment.equals("||")) {
            return Token.Or;
        }

        if ()

    }

    private
}
