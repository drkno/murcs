package sws.murcs.model;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
     * Gets the list of estimates for the estimate type.
     * @return A list of the estimates.
     */
    public List<String> getEstimates() {
        String path = "./src/main/resources/sws.murcs/estimates/" + toString();
        try {
            return Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        } catch (Exception e) {
            //This will never happen as long as you're not an idiot
            System.err.println("No such file as " + path);
        }
        return new ArrayList<>();
    }
}
