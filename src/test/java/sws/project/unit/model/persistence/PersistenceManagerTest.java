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
                modelMap.remove(persistenceName);
                return true;
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
    public void testLoadModel() throws Exception {

    }

    @Test
    public void testSaveModel() throws Exception {

    }

    @Test
    public void testSaveModel1() throws Exception {

    }

    @Test
    public void testModelExists() throws Exception {

    }

    @Test
    public void testGetModels() throws Exception {

    }

    @Test
    public void testDeleteModel() throws Exception {

    }

    @Test
    public void testGetCurrentModel() throws Exception {

    }

    @Test
    public void testSetCurrentModel() throws Exception {

    }

    @Test
    public void testGetSetCurrentWorkingDirectory() throws Exception {
        manager.setCurrentWorkingDirectory("test123");
        Assert.assertEquals(manager.getCurrentWorkingDirectory(), "test123");
        manager.setCurrentWorkingDirectory(null);
        Assert.assertNull(manager.getCurrentWorkingDirectory());
    }
}