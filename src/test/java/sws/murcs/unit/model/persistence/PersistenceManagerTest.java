package sws.murcs.unit.model.persistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.PersistenceLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class PersistenceManagerTest {

    private class TestLoader implements PersistenceLoader {

        private HashMap<String, Organisation> modelMap = new HashMap<String, Organisation>();
        private String workingDirectory;

        @Override
        public Organisation loadModel(String persistenceName) {
            return modelMap.get(persistenceName);
        }

        @Override
        public void saveModel(String saveName, Organisation persistent) throws Exception {
            modelMap.put(saveName, persistent);
        }

        @Override
        public ArrayList<String> getModelList() {
            return new ArrayList<>(modelMap.keySet());
        }

        @Override
        public boolean deleteModel(String persistenceName) {
            try {
                Organisation model = modelMap.remove(persistenceName);
                return model != null;
            }
            catch (Exception e) {
                return false;
            }
        }

        @Override
        public String getCurrentWorkingDirectory() throws Exception {
            return workingDirectory;
        }

        @Override
        public void setCurrentWorkingDirectory(String directory) throws Exception {
            workingDirectory = directory;
        }
    }

    private PersistenceManager manager;
    private OrganisationGenerator generator;

    @Before
    public void setUp() throws Exception {
        PersistenceManager.setCurrent(null);
        manager = new PersistenceManager(new TestLoader());
        PersistenceManager.setCurrent(manager);
        generator = new OrganisationGenerator(OrganisationGenerator.Stress.Low);
        UndoRedoManager.get().setDisabled(true);
    }

    @Test
    public void testCurrentPersistenceManagerExists() throws Exception {
        PersistenceManager.setCurrent(null);
        Assert.assertFalse(PersistenceManager.currentPersistenceManagerExists());
        PersistenceManager.setCurrent(manager);
        Assert.assertTrue(PersistenceManager.currentPersistenceManagerExists());
    }

    @Test
    public void testGetSetPersistenceLoader() throws Exception {
        PersistenceLoader loader = manager.getPersistenceLoader();
        Assert.assertNotNull(loader);
        Assert.assertTrue(loader instanceof TestLoader);
        PersistenceLoader loaderNew = new TestLoader();
        manager.setPersistenceLoader(loaderNew);
        Assert.assertNotEquals(loader, manager.getPersistenceLoader());
    }

    @Test
    public void testLoadSaveModel() throws Exception {
        manager.saveModel("test", generator.generate());
        Assert.assertNotNull(manager.loadModel("test"));
        Assert.assertNull(manager.loadModel("test1"));
        Organisation curr = generator.generate();
        manager.setCurrentModel(curr);
        manager.saveModel("current");
        Assert.assertEquals(curr, manager.loadModel("current"));
    }

    @Test
    public void testModelExists() throws Exception {
        Assert.assertFalse(manager.modelExists("none"));
        manager.saveModel("none", generator.generate());
        Assert.assertTrue(manager.modelExists("none"));
        Assert.assertFalse(manager.modelExists(null));
        Assert.assertFalse(manager.modelExists(""));
    }

    @Test
    public void testGetModels() throws Exception {
        Collection<String> models = manager.getModels();
        Assert.assertTrue(models.size() == 0);
        manager.saveModel("1", generator.generate());
        manager.saveModel("2", generator.generate());
        manager.saveModel("3", generator.generate());
        manager.saveModel("4", generator.generate());
        models = manager.getModels();
        Assert.assertTrue(models.size() == 4);
        String[] expected = {"1", "2", "3", "4"};
        String[] actual = new String[models.size()];
        models.toArray(actual);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testDeleteModel() throws Exception {
        // Clear the Persistence Manager as it may still contain stuff from previous tests
        if (PersistenceManager.getCurrent() != null) {
            PersistenceManager.getCurrent().setCurrentModel(null);
        }
        Assert.assertFalse(manager.deleteModel("none"));
        Organisation model = null;
        model = generator.generate();
        manager.saveModel("temp", model);
        Assert.assertTrue(manager.modelExists("temp"));
        Assert.assertTrue(manager.deleteModel("temp"));
        Assert.assertFalse(manager.modelExists("temp"));
    }

    @Test
    public void testGetSetCurrentModel() throws Exception {
        Assert.assertNull(manager.getCurrentModel());
        Organisation model = generator.generate();
        manager.setCurrentModel(model);
        Assert.assertNotNull(manager.getCurrentModel());
        Assert.assertEquals(model, manager.getCurrentModel());
    }

    @Test
    public void testGetSetCurrentWorkingDirectory() throws Exception {
        manager.setCurrentWorkingDirectory("test123");
        Assert.assertEquals(manager.getCurrentWorkingDirectory(), "test123");
        manager.setCurrentWorkingDirectory(null);
        Assert.assertNull(manager.getCurrentWorkingDirectory());
    }
}