package sws.murcs.search.tokens;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a single piece of a search (e.g. "Foo")
 */
public class SearchToken extends Token {

    /**
     * New search tokens will be case insensitive?
     */
    private static boolean caseInsensitive = true;

    /**
     * If true, assumes all input is a trimmed regular expression, excluding special tokens.
     */
    private static boolean assumeRegex = false;

    /**
     * Pattern for excapting special regex characters.
     */
    private static final Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

    /**
     * Regular expression to match while searching.
     */
    private Pattern searchRegex;

    /**
     * Creates a new search token with the specified query
     * @param searchTerm The term to search for.
     */
    public SearchToken(final String searchTerm) {
        String regexExp;
        if (assumeRegex) {
            regexExp = searchTerm;
        }
        else {
            regexExp = wildcardToRegex(searchTerm);
        }

        int flags = Pattern.MULTILINE | Pattern.DOTALL;
        if (caseInsensitive) {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        searchRegex = Pattern.compile(regexExp, flags);
    }

    /**
     * Sets whether searches are sensitive to case.
     * @param isCaseSensitive whether to account for case.
     */
    public static void setIsCaseSensitive(final boolean isCaseSensitive) {
        caseInsensitive = !isCaseSensitive;
    }

    /**
     * Sets whether searches will be parsed as regex instead of wildcard.
     * @param isRegex the expression is a regex expression.
     */
    public static void setIsRegex(final boolean isRegex) {
        assumeRegex = isRegex;
    }

    /**
     * Converts a wildcard expression to a regular expression.
     * @param wildcardExpression wildcard expression to convert.
     * @return the regular expression equivalent.
     */
    private String wildcardToRegex(final String wildcardExpression) {
        String regex = SPECIAL_REGEX_CHARS.matcher(wildcardExpression).replaceAll("\\\\$0");
        regex = regex.replaceAll("(?<=(^|[^\\\\]))(\\\\)(\\*)", ".*");
        regex = regex.replaceAll("(\\\\){2}(\\*)", "\\*");
        regex = regex.replaceAll("(?<=(^|[^\\\\]))(\\\\)(\\?)", ".");
        regex = regex.replaceAll("(\\\\){2}(\\?)", "\\?");
        return regex;
    }

    @Override
    public boolean matches(final String input) {
        if (input == null) {
            return false;
        }
        Matcher matcher = searchRegex.matcher(input);
        return matcher.find();
    }
}
