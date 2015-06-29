package sws.murcs.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
     * Shirt sizes, XS, S, M, L, XL, American.
     */
    ShirtSize,

    /**
     * NZ ratings for movies (e.g. PG).
     */
    MovieClassification;

    /**
     * A list of all the already loaded estimate types.
     */
    private static Map<EstimateType, List<String>> estimates = new HashMap<>();

    /**
     * Gets the list of estimates for the estimate type.
     * @return A list of the estimates.
     */
    public final List<String> getEstimates() {
        if (estimates.containsKey(this)) {
            return estimates.get(this);
        }

        String path = "/sws/murcs/estimates/" + toString() + ".csv";
        try {
            List<String> currentEstimates = Files.readAllLines(Paths.get(getClass().getResource(path).toURI()),
                    StandardCharsets.UTF_8);
            //Trim the string and remove commas and stuff
            for (int i = 0; i < currentEstimates.size(); i++) {
                currentEstimates.set(i, currentEstimates.get(i).replace(",", "").trim());
            }

            estimates.put(this, currentEstimates);
            return currentEstimates;
        } catch (URISyntaxException e) {
            //This shouldn't happen unless you add an estimation method with a really weird name
            e.printStackTrace();
        } catch (IOException e){
            //This will never happen as long as you're not an idiot
            System.err.println("No such file as " + path);
        }
        return new ArrayList<>();
    }

    /**
     * Converts from the current estimate type to a new estimate type.
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
}
