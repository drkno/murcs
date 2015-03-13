package sws.project.unit.model.persistence.loaders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.loaders.FilePersistenceLoader;
import sws.project.model.persistence.loaders.PersistenceLoader;
import sws.project.sampledata.RelationalModelGenerator;
import java.io.File;
import java.util.ArrayList;

public class FilePersistenceLoaderTest {

    private PersistenceLoader loader;
    private RelationalModelGenerator generator;

    @Before
    public void setup() throws Exception {
        loader = new FilePersistenceLoader();
        File file = new File(System.getProperty("user.dir"));
        loader.setCurrentWorkingDirectory(file.getAbsolutePath());
        generator = new RelationalModelGenerator();
    }

    private String getNewTestFile() throws Exception {
        File tempFile = File.createTempFile("persistenceManagerTest", ".project", new File(loader.getCurrentWorkingDirectory()));
        tempFile.deleteOnExit();
        return tempFile.getName();
    }

    @Test
    public void testLoadModel() throws Exception {
        String testFile = getNewTestFile();
        RelationalModel model = generator.generate();
        loader.saveModel(testFile, model);
        RelationalModel loadModel = loader.loadModel(testFile);
        Assert.assertNotNull(loadModel);
        Assert.assertEquals(loadModel.getProject().getShortName(), model.getProject().getShortName());
    }

    @Test
    public void testLoadModelFail() throws Exception {
        RelationalModel model = loader.loadModel(getNewTestFile());
        Assert.assertNull(model);
        model = loader.loadModel(null);
        Assert.assertNull(model);
    }

    @Test
    public void testSaveModel() throws Exception {
        String testFile = getNewTestFile();
        loader.saveModel(testFile, generator.generate()); // on fail it will throw exception
    }

    @Test
    public void testGetModelList() throws Exception {
        ArrayList<String> models1 = loader.getModelList();
        String testFile = getNewTestFile();
        loader.saveModel(testFile, generator.generate());
        ArrayList<String> models2 = loader.getModelList();
        Assert.assertNotNull(models2);
        Assert.assertTrue(models1.size() < models2.size());
    }

    @Test
    public void testDeleteModel() throws Exception {
        String testFile = getNewTestFile();
        loader.saveModel(testFile, generator.generate());
        Assert.assertTrue(loader.deleteModel(testFile));
    }

    @Test
    public void testDeleteModelNone() throws Exception {
        String testFile = getNewTestFile();
        loader.deleteModel(testFile);   // make sure it is deleted, java creates a temp file
        Assert.assertFalse(loader.deleteModel(testFile));
        Assert.assertFalse(loader.deleteModel(null));
    }

    @Test
    public void testGetCurrentWorkingDirectory() throws Exception {
        File file = new File(System.getProperty("user.dir"));
        Assert.assertEquals(file.getAbsolutePath(), loader.getCurrentWorkingDirectory());
    }
}