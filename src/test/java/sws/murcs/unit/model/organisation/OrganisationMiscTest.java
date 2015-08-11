package sws.murcs.unit.model.organisation;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

public class OrganisationMiscTest {
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
    public void testGetVersion() throws Exception {
        String version = Organisation.getVersion();

        // not really much more than this that we can test, given the only way without
        // running through maven to find the version number is from the Organisation
        Assert.assertNotNull("Version numbers should not be null.", version);
        Assert.assertNotEquals("Version numbers should not be empty.", "", version.trim());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNull() throws Exception {
        model.add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() throws Exception {
        model.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullExists() throws Exception {
        UsageHelper.exists(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullInUse() throws Exception {
        UsageHelper.inUse(null);
    }
}
