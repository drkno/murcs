package sws.murcs.unit.model;

import org.junit.Test;
import sws.murcs.model.EstimateType;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the estimate type enum
 */
public class EstimateTypeTest {

    @Test
    public void testGetEstimates() throws Exception {
        List<String> estimates = EstimateType.Fibonacci.getEstimates();

        assertEquals("1", estimates.get(0));
        assertEquals(6, estimates.size());

        estimates = EstimateType.MovieClassification.getEstimates();
        assertEquals("M", estimates.get(2));
        assertEquals(6, estimates.size());

        estimates = EstimateType.ShirtSize.getEstimates();
        assertEquals("XXXL", estimates.get(5));
    }

    @Test
    public void testConvert() throws Exception {
        assertEquals("1", EstimateType.ShirtSize.convert(EstimateType.Fibonacci, "XS"));
        assertEquals("XXXL", EstimateType.MovieClassification.convert(EstimateType.ShirtSize, "Banned"));

        assertEquals("M", EstimateType.ShirtSize.convert(EstimateType.ShirtSize, "M"));
        assertEquals("Not Estimated", EstimateType.ShirtSize.convert(EstimateType.Fibonacci, EstimateType.NOT_ESTIMATED));
        assertEquals("1", EstimateType.ShirtSize.convert(EstimateType.Fibonacci, "XS"));
    }

    @Test
    public void testConvertFixedValues() throws Exception {
        assertEquals("When converting the EstimateType.ZERO it should keep it the same.",
                "0", EstimateType.ShirtSize.convert(EstimateType.Fibonacci, EstimateType.ZERO));
        assertEquals("When converting the EstimateType.INFINITE it should keep it the same",
                "Infinite", EstimateType.MovieClassification.convert(EstimateType.Fibonacci, EstimateType.INFINITE));
        assertEquals("When converting the EstimateType.NOT_ESTIMATED it should keep it the same",
                "Not Estimated",
                EstimateType.Fibonacci.convert(EstimateType.MovieClassification, EstimateType.NOT_ESTIMATED));
    }
}
