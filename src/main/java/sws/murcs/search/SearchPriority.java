package sws.murcs.search;

/**
 * Priority to search a field with. This will determine
 * the search pass it will be found in.
 */
public enum SearchPriority {
    /**
     * Ultra search priority. Will be found in the first pass.
     */
    Ultra,
    /**
     * High search priority. Will be found in second pass.
     */
    High,
    /**
     * Medium search priority. Will be found in third pass.
     */
    Medium,
    /**
     * Low search priority. Will be found in fourth pass.
     */
    Low
}
