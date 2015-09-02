package sws.murcs.model;

import sws.murcs.debug.errorreporting.ErrorReporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type of estimation that you are using for stuff and things.
 */
public enum EstimateType {

    /**
     * Numbers. Or the Fibonacci numbers from 1 to 13.
     */
    Fibonacci,

    /**
     * Shirt sizes, XS, S, M, L, XL, XXL.
     */
    ShirtSize,

    /**
     * NZ ratings for movies (e.g. PG).
     */
    MovieClassification;

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    private static final long serialVersionUID = 0L;

    /**
     * A list of all the already loaded estimate types.
     */
    private static Map<EstimateType, List<String>> estimates = new HashMap<>();

    /**
     * Used when something is not estimated.
     */
    public static final String NOT_ESTIMATED = "Not Estimated";

    /**
     * Used when something has an estimate of 0.
     */
    public static final String ZERO = "0";

    /**
     * Used when something is estimated as infinite i.e. if it's using and unknown technology and therefore
     * it isn't clear how long it will take.
     */
    public static final String INFINITE = "Infinite";

    /**
     * Gets the list of estimates for the estimate type.
     * @return A list of the estimates.
     */
    public final List<String> getEstimates() {
        if (estimates.containsKey(this)) {
            return estimates.get(this);
        }

        String path = "estimates/" + nonDisplayToString() + ".csv";
        try {
            InputStream input = getClass().getResourceAsStream(path);
            List<String> currentEstimates = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            String line = br.readLine();
            while (line != null) {
                currentEstimates.add(line);
                line = br.readLine();
            }
            //Trim the string and remove commas and stuff
            for (int i = 0; i < currentEstimates.size(); i++) {
                currentEstimates.set(i, currentEstimates.get(i).replace(",", "").trim());
            }

            estimates.put(this, currentEstimates);
            return currentEstimates;
        } catch (IOException e) {
            //This will never happen
            ErrorReporter.get().reportError(e, "No such file as " + path);
        }
        return new ArrayList<>();
    }

    /**
     * Converts from the current estimate type to a new estimate type. If the estimate is not a valid option for the
     * estimate type then the original "estimate" value is returned.
     * @param newType The type to convert to.
     * @param estimate The current estimate (as it appears in the getEstimates() list).
     * @return The estimate in the newType type....
     */
    public final String convert(final EstimateType newType, final String estimate) {
        List<String> currentEstimates = getEstimates();
        int currentIndex = currentEstimates.indexOf(estimate);
        if (currentIndex == -1) {
            return estimate;
        }

        List<String> newEstimates = newType.getEstimates();

        float percent = currentIndex / (float) currentEstimates.size();
        int newIndex = (int) (newEstimates.size() * percent);

        return newEstimates.get(newIndex);
    }

    /**
     * The normal toString method without spaces to make it readable.
     * @return the normal toString method.
     */
    public String nonDisplayToString() {
        return super.toString();
    }

    @Override
    public String toString() {
        return super.toString().replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }
}
