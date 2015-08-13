package sws.murcs.search;

/**
 * Priority to search a field with. This will determine
 * the search pass it will be found in.
 */
public enum SearchPriority {
    /**
     * Ultra search priority. Will be found in the first pass.
     */
    Ultra(0),
    /**
     * High search priority. Will be found in second pass.
     */
    High(1),
    /**
     * Medium search priority. Will be found in third pass.
     */
    Medium(2),
    /**
     * Low search priority. Will be found in fourth pass.
     */
    Low(3);

    /**
     * Priority of the search item.
     */
    private int searchPriority;

    /**
     * Sets up the search priority so it can be reliably compared.
     * @param priority priority of the search item.
     */
    SearchPriority(final int priority) {
        searchPriority = priority;
    }
}
