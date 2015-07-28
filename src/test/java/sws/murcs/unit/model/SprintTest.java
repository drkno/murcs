package sws.murcs.unit.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.debug.sampledata.ReleaseGenerator;
import sws.murcs.debug.sampledata.TeamGenerator;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.model.Release;
import sws.murcs.model.Sprint;
import sws.murcs.model.Team;

import static org.junit.Assert.assertEquals;

/**
 * Tests the sprint class lol
 */
public class SprintTest {
    private Sprint sprint;

    @Before
    public void setup() throws Exception{
        sprint = new Sprint();
        //TODO use generators which we very cleverly wrote before this test..
        sprint.setShortName("name");
        sprint.setLongName("long name");
        sprint.setDescription("description");
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plus(10, ChronoUnit.DAYS));
        sprint.setTeam((new TeamGenerator()).generate());

        Release release = (new ReleaseGenerator()).generate();
        release.setReleaseDate(sprint.getEndDate().plus(1, ChronoUnit.DAYS));
        sprint.setAssociatedRelease(release);
    }


    @Test
    public void testModifyName() throws Exception{
        sprint.setShortName("new name");
        assertEquals("Short name should be new name!", "new name", sprint.getShortName());
    }

    @Test
    public void testModifyLongName() throws Exception{
        sprint.setLongName("new long name");
        assertEquals("Long name should be new long name!", "new long name", sprint.getLongName());
    }

    @Test
    public void testModifyDescription() throws Exception{
        sprint.setDescription("new description");
        assertEquals("Description should be new description!", "new description", sprint.getDescription());
    }

    @Test
    public void testModifyTeam() {
        Team team = (new TeamGenerator()).generate();
        sprint.setTeam(team);

        assertEquals("Team should be team", team, sprint.getTeam());
    }

    @Test
    public void testModifyRelease() throws InvalidParameterException {
        Release release = (new ReleaseGenerator()).generate();
        sprint.setAssociatedRelease(release);

        assertEquals("Release should be release", release, sprint.getAssociatedRelease());
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidRelease() throws InvalidParameterException {
        Release release = (new ReleaseGenerator()).generate();
        release.setReleaseDate(sprint.getStartDate().plus(1, ChronoUnit.DAYS));

        sprint.setAssociatedRelease(release);
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidStartDate() throws InvalidParameterException {
        LocalDate start = sprint.getEndDate().plus(1, ChronoUnit.DAYS);
        sprint.setStartDate(start);
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidEndDate1() throws InvalidParameterException {
        LocalDate end = sprint.getStartDate().minus(1, ChronoUnit.DAYS);
        sprint.setEndDate(end);
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidEndDate2() throws InvalidParameterException {
        LocalDate end = sprint.getAssociatedRelease().getReleaseDate().plus(10, ChronoUnit.DAYS);
        sprint.setEndDate(end);
    }
}