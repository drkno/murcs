package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Project;
import sws.murcs.debug.sampledata.ProjectGenerator;
import sws.murcs.debug.sampledata.Generator;

public class ProjectTest {

    private static Generator<Project> projectGenerator;
    private Project generatedProject;

    @BeforeClass
    public static void oneTimeSetUp() {
        projectGenerator = new ProjectGenerator();
        UndoRedoManager.setDisabled(true);
    }

    @Before
    public void setUp() {
        generatedProject = projectGenerator.generate();
    }

    @After
    public void tearDown() {
        generatedProject = null;
    }

    @Test(expected = Exception.class)
    public void setShortNameTest1() throws Exception {
        generatedProject.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception {
        generatedProject.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception {
        generatedProject.setShortName("   \n\r\t");
    }
}
