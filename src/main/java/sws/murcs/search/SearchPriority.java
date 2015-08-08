package sws.murcs.search;

/**
 * Priority to search a field with. This will determine
 * the search pass it will be found in.
 */
public enum SearchPriority {
    /**
     * High search priority. Will be found in first pass.
     */
    High,
    /**
     * Medium search priority. Will be found in second pass.
     */
    Medium,
    /**
     * Low search priority. Will be found in third pass.
     */
    Low
}
