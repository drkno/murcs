package sws.murcs.search;

import sws.murcs.model.Model;

import java.util.*;

/**
 * Object that represents a SearchResult.
 */
public class SearchResult {

    /**
     * Maximum length of a search result as returned result text.
     */
    private static final int SEARCH_RESULT_MAX_LENGTH = 45;

    /**
     * Model object that this search result points to.
     */
    private Model model;

    /**
     * Matches that were found.
     */
    private List<String> matches;

    /**
     * Collection of start indexes for a match.
     */
    private List<Integer> matchIndexes;

    /**
     * The text before a match.
     */
    private String contextBefore;

    /**
     * The text after the match.
     */
    private String contextAfter;

    /**
     * The type of model this match occurred on.
     */
    private String modelType;

    /**
     * The name of the field that the match occurred on.
     */
    private String fieldName;

    /**
     * The priority with which this result was found.
     */
    private SearchPriority priority;

    /**
     * Creates a new object that represents a search result.
     * @param startIndex index of the beginning of the match.
     * @param endIndex index of the end of the match.
     * @param input the text that the match was found in.
     */
    public SearchResult(final int startIndex, final int endIndex, final String input) {
        matches = new ArrayList<>();
        matchIndexes = new ArrayList<>();
        matches.add(input.substring(startIndex, endIndex));
        matchIndexes.add(startIndex);
        fromQuery(startIndex, endIndex, input);
    }

    /**
     * Sets the model that this search result is associated with.
     * @param theModel the model to associate this result with.
     * @param theFieldName the name of the field that the match occurred on.
     * @param searchPriority the priority of the search for this item/
     */
    public final void setModel(final Model theModel, final String theFieldName, final SearchPriority searchPriority) {
        model = theModel;
        modelType = toTitleCase(model.getClass().getSimpleName());
        fieldName = toTitleCase(theFieldName);
        priority = searchPriority;
    }

    /**
     * Gets the model that this result was found on.
     * @return the model.
     */
    public final Model getModel() {
        return model;
    }

    @Override
    public final String toString() {
        return String.join(", ", matches);
    }

    /**
     * Gets the context of the selection before the match.
     * Can be a 0 length string if the match is long.
     * @return the text before the match.
     */
    public final String selectionBefore() {
        return contextBefore;
    }

    /**
     * Gets the context of the selection after the match.
     * Can be a 0 length string if the match is long.
     * @return the text after the match.
     */
    public final String selectionAfter() {
        return contextAfter;
    }

    /**
     * Gets the type of model the match occurred on.
     * @return the model type.
     */
    public final String getModelType() {
        return modelType;
    }

    /**
     * Gets the name of the field that the match occurred on.
     * @return the name of the field.
     */
    public final String getFieldName() {
        return fieldName;
    }

    /**
     * Gets all the matches found and surrounded substrings.
     * Every second match is a string that is in-between a match.
     * @return the matches found and surrounded substrings.
     */
    public final List<String> getMatches() {
        return Collections.unmodifiableList(matches);
    }

    /**
     * Gets the priority that this result was found with.
     * @return the search priority.
     */
    public final SearchPriority getPriorityProperty() {
        return priority;
    }

    /**
     * Combines multiple search matches into one.
     * @param searchResult the search result to combine with this one.
     * @param query the query associated with this search result.
     */
    public final void addMatch(final SearchResult searchResult, final String query) {
        List<Map.Entry<Integer, String>> entries = new ArrayList<>();

        for (int i = 0; i < matches.size(); i += 2) {
            Map.Entry<Integer, String> kvp = new AbstractMap.SimpleEntry<>(matchIndexes.get(i), matches.get(i));
            entries.add(kvp);
        }

        for (int j = 0; j < searchResult.matches.size(); j += 2) {
            Map.Entry<Integer, String> kvp = new AbstractMap.SimpleEntry<>(
                    searchResult.matchIndexes.get(j), searchResult.matches.get(j));
            entries.add(kvp);
        }

        entries.sort((o1, o2) -> {
            int k = o1.getKey().compareTo(o2.getKey());
            if (k == 0) {
                return -Integer.compare(o1.getValue().length(), o2.getValue().length());
            }
            return k;
        });

        List<String> newMatches = new ArrayList<>();
        List<Integer> newIndexes = new ArrayList<>();

        int currIndex;
        for (int i = 0; i < entries.size(); i++) {
            int index = entries.get(i).getKey();
            currIndex = index;
            while (currIndex <= query.length() && i < entries.size() && currIndex >= entries.get(i).getKey()) {
                Map.Entry<Integer, String> entry = entries.get(i);
                int change = entry.getKey() + entry.getValue().length();
                if (currIndex < change) {
                    currIndex = change;
                }
                i++;
            }
            i--;

            newMatches.add(query.substring(index, currIndex));
            newIndexes.add(index);

            if (i + 1 < entries.size()) {
                newMatches.add(query.substring(currIndex, entries.get(i + 1).getKey()));
                newIndexes.add(currIndex);
            }
        }

        matches = newMatches;
        matchIndexes = newIndexes;

        int lastIndex = matchIndexes.get(matchIndexes.size() - 1) + matches.get(matches.size() - 1).length();
        fromQuery(matchIndexes.get(0), lastIndex, query);
    }

    /**
     * Converts programmatic case to "Title Case".
     * @param input camelCaseString or ObjectNameString.
     * @return Title Case String.
     */
    private String toTitleCase(final String input) {
        String result =
                input.replaceAll("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])", " ");
        if (Character.isLowerCase(result.charAt(0)) && input.length() > 1) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }
        return result;
    }

    /**
     * Gets data from an input match for this search result.
     * @param start start index of the match.
     * @param end end index of the match.
     * @param input input that match occurred in.
     */
    private void fromQuery(final int start, final int end, final String input) {
        contextBefore = "";
        contextAfter = "";
        if (end - start < SEARCH_RESULT_MAX_LENGTH) {
            int newStart = start, newEnd = end;
            int difference = SEARCH_RESULT_MAX_LENGTH - (newEnd - newStart);
            if (newStart != 0 && newEnd != input.length()) {
                difference /= 2;
                newStart -= difference;
                newEnd += difference;
            }
            else if (newStart != 0) {
                newStart -= difference;
            }
            else if (end != input.length()) {
                newEnd += difference;
            }

            if (newStart < 0) {
                newEnd += 0 - newStart;
                newStart = 0;
            }
            if (newEnd > input.length()) {
                int diff = newEnd - input.length();
                if (newStart - diff < 0) {
                    newStart = 0;
                }
                else {
                    newStart -= diff;
                }
                newEnd = input.length();
            }
            contextBefore = input.substring(newStart, start);
            contextAfter = input.substring(end, newEnd);
        }
    }
}
