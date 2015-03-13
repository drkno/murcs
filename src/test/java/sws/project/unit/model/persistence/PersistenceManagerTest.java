package sws.project.unit.model.persistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.model.persistence.loaders.PersistenceLoader;
import sws.project.sampledata.RelationalModelGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class PersistenceManagerTest {

    private class TestLoader implements PersistenceLoader {

        private HashMap<String, RelationalModel> modelMap = new HashMap<String, RelationalModel>();
        private String workingDirectory;

        @Override
        public RelationalModel loadModel(String persistenceName) {
            return modelMap.get(persistenceName);
        }

        @Override
        public void saveModel(String saveName, RelationalModel persistent) throws Exception {
            modelMap.put(saveName, persistent);
        }

        @Override
        public ArrayList<String> getModelList() {
            return new ArrayList<>(modelMap.keySet());
        }

        @Override
        public boolean deleteModel(String persistenceName) {
            try {
                RelationalModel model = modelMap.remove(persistenceName);
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
    private RelationalModelGenerator generator;

    @Before
    public void setUp() throws Exception {
        manager = new PersistenceManager(new TestLoader());
        generator = new RelationalModelGenerator();
    }

    @Test
    public void testCurrentPersistenceManagerExists() throws Exception {
        PersistenceManager.Current = null;
        Assert.assertFalse(PersistenceManager.CurrentPersistenceManagerExists());
        PersistenceManager.Current = manager;
        Assert.assertTrue(PersistenceManager.CurrentPersistenceManagerExists());
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
        RelationalModel curr = generator.generate();
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
        ArrayList<String> models = manager.getModels();
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
        Assert.assertFalse(manager.deleteModel("none"));
        manager.saveModel("temp", generator.generate());
        Assert.assertTrue(manager.modelExists("temp"));
        Assert.assertTrue(manager.deleteModel("temp"));
        Assert.assertFalse(manager.modelExists("temp"));
    }

    @Test
    public void testGetSetCurrentModel() throws Exception {
        Assert.assertNull(manager.getCurrentModel());
        RelationalModel model = generator.generate();
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