package sws.murcs.unit.model;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.TimeEstimate;

import static org.junit.Assert.assertEquals;

public class TimeEstimateTest {
    TimeEstimate estimate;

    @Before
    public void setUp() {
        UndoRedoManager.setDisabled(true);

        estimate = new TimeEstimate();
        estimate.setEstimate(1, LocalDate.of(2001, 1, 1));
    }

    @Test
    public void testAddToEstimate() throws Exception {
        estimate.addToEstimate(1, LocalDate.of(2001, 1, 1));
        assertEquals(2, estimate.getEstimate(LocalDate.of(2001, 1, 1)), 0.1);

        estimate.addToEstimate(1, LocalDate.of(2000, 1, 1));
        assertEquals(1, estimate.getEstimate(LocalDate.of(2000, 1, 1)), 0.1);
        assertEquals(3, estimate.getEstimate(LocalDate.of(2001, 1, 1)), 0.1);
    }

    @Test
    public void setEstimateTest() throws Exception {
        estimate.setEstimate(5);
        assertEquals(5, estimate.getEstimate(), 0.1);
    }

    @Test
    public void setPreviousEstimate() {
        LocalDate date = LocalDate.of(2000, 1, 1);

        //The current estimate should be 1 (as the estimate in 2001 is 1 and today is after 2001)
        assertEquals(1, estimate.getEstimate(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(1, estimate.getEstimate(), 0.1);

        //Should add ten to all estimates Jan 1, 2000
        estimate.setEstimate(10, date);

        assertEquals(10f, estimate.getEstimate(date), 0.1);
        assertEquals(11f, estimate.getEstimate(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(11f, estimate.getEstimate(), 0.1);

        //Shouldn't change anything apart from our estimate for today
        estimate.setEstimate(1);
        assertEquals(10, estimate.getEstimate(date), 0.1);
        assertEquals(11, estimate.getEstimate(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(1, estimate.getEstimate(), 0.1);

        estimate.setEstimate(6, LocalDate.of(2003, 1, 1));
        assertEquals(6, estimate.getEstimate(LocalDate.of(2003, 1, 1)), 0.1);

        //Set this to six, meaning we should subtract (6 - 11) from all estimates after this one
        estimate.setEstimate(6, LocalDate.of(2002, 1, 1));

        assertEquals(6, estimate.getEstimate(LocalDate.of(2002, 1, 1)), 0.1);
        assertEquals(1, estimate.getEstimate(LocalDate.of(2003, 1, 1)), 0.1);

        //This should be 0, as we should never have negative estimates
        assertEquals(0, estimate.getEstimate(), 0.1);
    }

    @Test
    public void testMerge() throws Exception {
        int year = 2000;
        int month = 1;

        LocalDate[] dates = new LocalDate[6];
        dates[0] = LocalDate.of(year, month, 1);
        dates[1] = LocalDate.of(year, month, 2);
        dates[2] = LocalDate.of(year, month, 3);
        dates[3] = LocalDate.of(year, month, 4);
        dates[4] = LocalDate.of(year, month, 5);
        dates[5] = LocalDate.of(year, month, 6);

        TimeEstimate first = new TimeEstimate();
        first.setEstimate(5, dates[0]);
        first.setEstimate(7, dates[1]);
        first.setEstimate(9, dates[4]);

        TimeEstimate second = new TimeEstimate();
        second.setEstimate(7, dates[2]);
        second.setEstimate(6, dates[3]);
        second.setEstimate(8, dates[4]);
        second.setEstimate(3, dates[5]);

        TimeEstimate firstMerged = new TimeEstimate();
        firstMerged.mergeIn(first, second);

        assertEquals(5, firstMerged.getEstimate(dates[0]), 0.1);
        assertEquals(7, firstMerged.getEstimate(dates[1]), 0.1);
        assertEquals(14, firstMerged.getEstimate(dates[2]), 0.1);
        assertEquals(13, firstMerged.getEstimate(dates[3]), 0.1);
        assertEquals(17, firstMerged.getEstimate(dates[4]), 0.1);
        assertEquals(12, firstMerged.getEstimate(dates[5]), 0.1);

        //Test merging statically works
        TimeEstimate secondMerge = TimeEstimate.merge(first, second);
        assertEquals(firstMerged, secondMerge);

        //Test merging into an existing TimeEstimate works
        second.mergeIn(first);
        assertEquals(firstMerged, second);
    }
}