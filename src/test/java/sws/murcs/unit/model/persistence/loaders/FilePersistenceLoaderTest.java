package sws.murcs.unit.model.persistence.loaders;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class FilePersistenceLoaderTest {

    private Random random;
    private ArrayList<String> files;
    private FilePersistenceLoader loader;
    private OrganisationGenerator generator;
    private final String testExtension = ".testProject";
    private PrintStream systemErr;

    @Before
    public void setup() throws Exception {
        systemErr = System.err;
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        }));
        loader = new FilePersistenceLoader();
        File file = new File(System.getProperty("user.dir"));
        loader.setCurrentWorkingDirectory(file.getAbsolutePath());
        generator = new OrganisationGenerator(OrganisationGenerator.Stress.Low);
        files = new ArrayList<>();
        random = new Random();
        UndoRedoManager.setDisabled(true);
        if (PersistenceManager.getCurrent() != null) {
            PersistenceManager.getCurrent().setCurrentModel(null);
        }
        else {
            PersistenceManager.setCurrent(new PersistenceManager(loader));
        }
    }

    @After
    public void tearDown() throws Exception {
        files.forEach(file -> new File(file).delete());
        System.setErr(systemErr);
    }

    private String getNewTestFile() throws Exception {
        while (true) {
            String tempFile = "filePersistenceLoaderTest" + random.nextInt() + testExtension;
            if (files.stream().filter(f -> f.equals(tempFile)).findAny().isPresent()) continue;
            files.add(tempFile);
            return tempFile;
        }
    }

    @Test
    public void testLoadModel() throws Exception {
        if (PersistenceManager.getCurrent() != null) {
            PersistenceManager.getCurrent().setCurrentModel(null);
        }
        String testFile = getNewTestFile();
        Organisation model = generator.generate();
        int numProjects = model.getProjects().size();
        loader.saveModel(testFile, model);
        Organisation loadModel = loader.loadModel(testFile);
        Assert.assertNotNull(loadModel);
        Assert.assertEquals(numProjects, loadModel.getProjects().size());
        Assert.assertEquals(loadModel.getProjects().get(0).getShortName(), model.getProjects().get(0).getShortName());
    }

    @Test
    public void testLoadModelFail() throws Exception {
        Organisation model = loader.loadModel(getNewTestFile());
        Assert.assertNull(model);
        model = loader.loadModel(null);
        Assert.assertNull(model);
    }

    @Test
    public void testSaveModel() throws Exception {
        String testFile = getNewTestFile();
        loader.saveModel(testFile, generator.generate()); // on fail it will throw exception
    }

    /**
     * Tests saving using an invalid file name.
     * @throws Exception if the test passes
     */
    @Test(expected = Exception.class)
    public void testInvalidSaveModel() throws Exception {
        String testFile = "test1234/?%\\:*+5678";
        loader.saveModel(testFile, generator.generate());
    }

    @Test
    public void testGetModelList() throws Exception {
        Collection<String> models1 = loader.getModelList(testExtension);
        String testFile = getNewTestFile();
        loader.saveModel(testFile, generator.generate());
        Collection<String> models2 = loader.getModelList(testExtension);
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