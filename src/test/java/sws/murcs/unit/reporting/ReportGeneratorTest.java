package sws.murcs.unit.reporting;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.*;
import sws.murcs.reporting.ReportGenerator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the ReportGenerator class.
 */
public class ReportGeneratorTest {

    private RelationalModel relationalModel;
    private File tempReport;
    private List<String> sampleReport;

    @Before
    public void setUp() throws Exception {
        UndoRedoManager.setDisabled(true);
        String sampleReportPath = "./src/test/resources/sws/murcs/reporting/sampleReport.xml";
        sampleReport = Files.readAllLines(Paths.get(sampleReportPath), StandardCharsets.UTF_8);
        tempReport = Files.createTempFile("", "").toFile();

        relationalModel = new RelationalModel();

        // Skills
        Skill skillC = new Skill();
        skillC.setShortName("C");
        skillC.setLongName("C99");
        skillC.setDescription("C99 is older than the current C standard, namely, C11");

        Skill skillPython = new Skill();
        skillPython.setShortName("Python");
        skillPython.setLongName("Python 2.7");
        skillPython.setDescription("A simple object-oriented language perfect for beginners");

        Skill skillPO = new Skill();
        skillPO.setShortName("PO");
        skillPO.setLongName("Product Owner");
        skillPO.setDescription("Has ability to insult design teams efforts");

        Skill skillSM = new Skill();
        skillSM.setShortName("SM");
        skillSM.setLongName("Scrum Master");
        skillSM.setDescription("Is able to manage the efforts of a team and resolve difficulties");

        // People
        Person person1 = new Person();
        person1.setShortName("Daniel");
        person1.setLongName("Daniel van Wichen");
        person1.setUserId("dpv11");
        person1.addSkill(skillPython);
        person1.addSkill(skillPO);

        Person person2 = new Person();
        person2.setShortName("Dion");
        person2.setLongName("Dion Wooley");
        person2.setUserId("dmw99");
        person2.addSkill(skillC);
        person2.addSkill(skillPython);
        person2.addSkill(skillSM);

        Person person3 = new Person();
        person3.setShortName("Haydon");
        person3.setLongName("Haydon Baddock");
        person3.setUserId("hbk67");
        person3.addSkill(skillPython);
        person3.addSkill(skillC);

        Person person4 = new Person();
        person4.setShortName("Unallocated");
        person4.setUserId("null123");

        // Teams
        Team team1 = new Team();
        team1.setShortName("Sengineers");
        team1.setLongName("Software Engineers");
        team1.setDescription("We are the best software engineers in the world");
        team1.addMember(person1);
        team1.addMember(person2);
        team1.setProductOwner(person1);
        team1.setScrumMaster(person2);

        Team team2 = new Team();
        team2.setShortName("Riding Solo");
        team2.setLongName("One man can conquer the world");
        team2.addMember(person3);

        // Project
        Project project = new Project();
        project.setShortName("FITR");
        project.setLongName("Fitness is Training Right");
        project.setDescription("We are building a fitness tracking application for the world");
        project.addTeam(team1);

        relationalModel.addProject(project);
        relationalModel.addTeam(team2);
        relationalModel.addPerson(person4);
    }

    @After
    public void tearDown() throws Exception {
        tempReport.delete();
    }

    @Test
    public void testGenerate() throws Exception {
        ReportGenerator.generate(relationalModel, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < sampleReport.size(); i++) {
            assertEquals(sampleReport.get(i).trim(), testReport.get(i).trim());
        }
    }
}