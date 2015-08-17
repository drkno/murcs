package sws.murcs.unit.model.organisation;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.Project;
import sws.murcs.model.Release;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.util.ArrayList;
import java.util.List;

public class OrganisationReleaseTest {
    private static OrganisationGenerator generator;
    private Organisation model;

    @BeforeClass
    public static void classSetup() {
        generator = new OrganisationGenerator(OrganisationGenerator.Stress.Medium);
        UndoRedoManager.setDisabled(true);
        if (PersistenceManager.getCurrent() == null) {
            PersistenceManager.setCurrent(new PersistenceManager(new FilePersistenceLoader()));
        }
    }

    @AfterClass
    public static void classTearDown() {
        UndoRedoManager.setDisabled(false);
        PersistenceManager.getCurrent().setCurrentModel(null);

    }

    /**
     * Generates a organisation, and sets it to the currently in use
     * model in the current persistence manager instance.
     * @throws NullPointerException if no persistence manager exists.
     * @return a new organisation.
     */
    private static Organisation getNeworganisation() {
        PersistenceManager.getCurrent().setCurrentModel(null);
        Organisation model = generator.generate();
        PersistenceManager.getCurrent().setCurrentModel(model);
        return model;
    }

    @Before
    public void setup() throws Exception {
        model = getNeworganisation();
    }

    @Test
    public void testGetReleasesNotNullOrEmpty() throws Exception {
        Organisation model = getNeworganisation();
        List<Release> releases = model.getReleases();

        Assert.assertNotNull("getReleases() should return releases but is null.", releases);
        Assert.assertNotEquals("getReleases() should return releases but is empty.", 0, releases.size());
    }

    @Test
    public void testGetReleasesReleaseRemoved() throws Exception {
        List<Release> releases = model.getReleases();
        int size = releases.size();
        Release removedRelease = releases.get(0);
        model.remove(removedRelease);
        releases = model.getReleases();

        Assert.assertFalse("Releases should not contain the removed release.", releases.contains(removedRelease));
        Assert.assertNotEquals("Releases should not be the same size as before removing a release.", size, releases.size());
    }

    @Test
    public void testGetReleasesAdded() throws Exception {
        List<Release> releases = model.getReleases();
        Release releaseToAdd = releases.get(0);
        model.remove(releaseToAdd);
        releases = model.getReleases();
        int size = releases.size();

        model.add(releaseToAdd);

        Assert.assertTrue("Releases should contain the added release.", releases.contains(releaseToAdd));
        Assert.assertNotEquals("Releases should not be the same size as before adding a release.", size, releases.size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testReleasesNoShortNameAdded() throws Exception {
        Release release = new Release();
        model.add(release);
    }

    @Test
    public void testReleasesNoShortNameNotAddedRemoved() throws Exception {
        List<Release> releases = model.getReleases();
        int size = releases.size();
        Release release = new Release();
        model.remove(release);
        Assert.assertEquals("Removing release that isn't in the model should not change the releases collection.", size, releases.size());
    }

    @Test(expected = DuplicateObjectException.class)
    public void testReleasesDuplicateAdded() throws Exception {
        List<Release> releases = model.getReleases();
        model.add(releases.get(0));
    }

    @Test
    public void testGetReleasesNoDuplicates() throws Exception {
        List<Release> releases = model.getReleases();

        List<Release> releaseDuplicates = new ArrayList<>();
        releases.stream().filter(release -> !releaseDuplicates.add(release)).forEach(release -> {
            Assert.fail("There cannot be duplicate releases returned by getReleases().");
        });
    }

    @Test
    public void testReleaseExists() throws Exception {
        List<Release> releases = model.getReleases();
        Assert.assertTrue("Release exists but was not found.", UsageHelper.exists(releases.get(0)));
    }

    @Test
    public void testReleaseDoesNotExist() throws Exception {
        Release release = new Release();
        release.setShortName("testing1234");
        Assert.assertFalse("Release exists when it should not.", UsageHelper.exists(release));
    }

    @Test
    public void testReleaseFindUsagesDoesNotExist() throws Exception {
        Release release = new Release();
        release.setShortName("testing1234");
        List<Model> usages = UsageHelper.findUsages(release);

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertEquals("Usages were found for release not in model.", 0, usages.size());
    }

    @Test
    public void testReleaseFindUsages() throws Exception {
        List<Release> releases = model.getReleases();
        List<Project> projects = model.getProjects();
        try {
            projects.get(0).addRelease(releases.get(0));
            model.add(releases.get(0));
        }
        catch (DuplicateObjectException e) {
            // ignore, we just want to ensure release is attached to a project
        }
        List<Model> usages = UsageHelper.findUsages(releases.get(0));

        Assert.assertNotNull("The returned usages was null.", usages);
        // TODO: Releases are not in use anywhere.
        Assert.assertEquals("Usages were found for a release.", 0, usages.size());
        Assert.assertFalse("Item should not be in use.", UsageHelper.inUse(releases.get(0)));
    }
}
