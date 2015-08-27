package sws.murcs.unit.reporting;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.AcceptanceCondition;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.Release;
import sws.murcs.model.Skill;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.reporting.ReportGenerator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the ReportGenerator class.
 */
public class ReportGeneratorTest {

    private Organisation organisation;
    private File tempReport;
    private Project project;
    private Team team;
    private Person person;
    private Backlog backlog;
    private Story story;
    private Sprint sprint;

    private void adjust(List<String> report) {
        for (int i = 0; i < report.size(); i++) {
            if (report.get(i).matches(".*<projectVersion>(\\d+\\.){2}(\\d+)</projectVersion>.*")) {
                report.set(i, "<projectVersion>" + organisation.getVersion() + "</projectVersion>");
            }
            if (report.get(i).matches(".*<dateGenerated>2015-[0-9]{2}-[0-9]{2}</dateGenerated>.*")) {
                report.set(i, "<dateGenerated>" + LocalDate.now() + "</dateGenerated>");
                break;
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        UndoRedoManager.setDisabled(true);
        if (PersistenceManager.getCurrent() != null) {
            PersistenceManager.getCurrent().setCurrentModel(null);
        }
        organisation = new Organisation();

        tempReport = Files.createTempFile("", "").toFile();

        // Skill
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

        // Person
        person = new Person();
        person.setShortName("Daniel");
        person.setLongName("Daniel van Wichen");
        person.setUserId("dpv11");
        person.addSkill(skillPython);
        person.addSkill(skillPO);

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
        team = new Team();
        team.setShortName("Sengineers");
        team.setLongName("Software Engineers");
        team.setDescription("We are the best software engineers in the world");
        team.addMember(person);
        team.addMember(person2);
        team.setProductOwner(person);
        team.setScrumMaster(person2);

        Team team2 = new Team();
        team2.setShortName("Riding Solo");
        team2.setLongName("One man can conquer the world");
        team2.addMember(person3);

        Team team3 = new Team();
        team3.setShortName("Lonely");
        team3.setLongName("Mr Lonely");

        // Project
        project = new Project();
        project.setShortName("FITR");
        project.setLongName("Fitness is Training Right");
        project.setDescription("We are building a fitness tracking application for the world");

        // Work Allocation
        LocalDate startDate = LocalDate.of(2015, 5, 1);
        LocalDate endDate = startDate.plus(7, ChronoUnit.DAYS);
        WorkAllocation allocation = new WorkAllocation(project, team, startDate, endDate);

        // Release
        Release release = new Release();
        release.setShortName("Now");
        release.setDescription("This current time");
        release.setReleaseDate(LocalDate.of(2015, 4, 22));
        project.addRelease(release);

        //Stories
        story = new Story();
        story.setShortName("1");
        story.setLongName("Revert");
        story.setDescription("Revert to last saved state");
        story.setCreator(person3);
        story.setEstimate(EstimateType.Fibonacci.getEstimates().get(0));
        story.setStoryState(Story.StoryState.Ready);
        AcceptanceCondition condition = new AcceptanceCondition();
        condition.setCondition("This is a condition");
        story.addAcceptanceCondition(condition);
        Task task = new Task();
        task.setName("Become one with the universe");
        task.setCurrentEstimate(100);
        task.setState(TaskState.Blocked);
        task.setDescription("Attempt to absorb the universe, this probably won't work");
        story.addTask(task);

        Story story2 = new Story();
        story2.setShortName("2");
        story2.setLongName("Story Maintenance");
        story2.setDescription("add stories to project");
        story2.setCreator(person4);
        story.addDependency(story2);

        //Backlog
        backlog = new Backlog();
        backlog.setShortName("This is the backlog");
        backlog.setLongName("back log be what");
        backlog.setDescription("high five");
        backlog.setAssignedPO(person);
        backlog.addStory(story, 1);

        //Sprint
        sprint = new Sprint();
        sprint.setShortName("This is the sprint");
        sprint.setLongName("This is the sprint's long name");
        sprint.setDescription("high five");
        sprint.setBacklog(backlog);
        sprint.addStory(backlog.getAllStories().get(0));
        sprint.setAssociatedRelease(release);
        sprint.setTeam(team);

        organisation.add(project);
        organisation.add(team);
        organisation.add(team2);
        organisation.add(team3);
        organisation.add(story);
        organisation.add(story2);
        organisation.add(person);
        organisation.add(person2);
        organisation.add(person3);
        organisation.add(person4);
        organisation.add(skillC);
        organisation.add(skillPython);
        organisation.addAllocation(allocation);
        organisation.add(release);
        organisation.add(backlog);
        organisation.add(sprint);
    }

    @After
    public void tearDown() throws Exception {
        tempReport.delete();
    }

    @Test
    public void testGenerateAll() throws Exception {
        String reportPath = "./src/test/resources/sws/murcs/reporting/sampleFullReport.xml";
        List<String> report = Files.readAllLines(Paths.get(reportPath), StandardCharsets.UTF_8);
        adjust(report);
        ReportGenerator.generate(organisation, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < testReport.size(); i++) {
            assertEquals(testReport.get(i).trim(), report.get(i).trim());
        }
    }

    @Test
    public void testGenerateProject() throws Exception {
        String reportPath = "./src/test/resources/sws/murcs/reporting/sampleProjectReport.xml";
        List<String> report = Files.readAllLines(Paths.get(reportPath), StandardCharsets.UTF_8);
        adjust(report);
        List<Model> projects = new ArrayList<>();
        projects.add(project);
        ReportGenerator.generate(projects, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < testReport.size(); i++) {
            assertEquals(testReport.get(i).trim(), report.get(i).trim());
        }
    }

    @Test
    public void testGenerateTeam() throws Exception {
        String reportPath = "./src/test/resources/sws/murcs/reporting/sampleTeamReport.xml";
        List<String> report = Files.readAllLines(Paths.get(reportPath), StandardCharsets.UTF_8);
        adjust(report);
        List<Model> teams = new ArrayList<>();
        teams.add(team);
        ReportGenerator.generate(teams, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < testReport.size(); i++) {
            assertEquals(testReport.get(i).trim(), report.get(i).trim());
        }
    }

    @Test
    public void testGeneratePerson() throws Exception {
        String reportPath = "./src/test/resources/sws/murcs/reporting/samplePersonReport.xml";
        List<String> report = Files.readAllLines(Paths.get(reportPath), StandardCharsets.UTF_8);
        adjust(report);
        List<Model> people = new ArrayList<>();
        people.add(person);
        ReportGenerator.generate(people, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < testReport.size(); i++) {
            assertEquals(testReport.get(i).trim(), report.get(i).trim());
        }
    }

    @Test
    public void testGenerateBacklog() throws Exception {
        String reportPath = "./src/test/resources/sws/murcs/reporting/sampleBacklogReport.xml";
        List<String> report = Files.readAllLines(Paths.get(reportPath), StandardCharsets.UTF_8);
        adjust(report);
        List<Model> backlogs = new ArrayList<>();
        backlogs.add(backlog);
        ReportGenerator.generate(backlogs, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < testReport.size(); i++) {
            assertEquals(testReport.get(i).trim(), report.get(i).trim());
        }
    }

    @Test
    public void testGenerateStory() throws Exception {
        String reportPath = "./src/test/resources/sws/murcs/reporting/sampleStoryReport.xml";
        List<String> report = Files.readAllLines(Paths.get(reportPath), StandardCharsets.UTF_8);
        adjust(report);
        List<Model> stories = new ArrayList<>();
        stories.add(story);
        ReportGenerator.generate(stories, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < testReport.size(); i++) {
            assertEquals(testReport.get(i).trim(), report.get(i).trim());
        }
    }

    @Test
    public void testGenerateSprint() throws Exception {
        String reportPath = "./src/test/resources/sws/murcs/reporting/sampleSprintReport.xml";
        List<String> report = Files.readAllLines(Paths.get(reportPath), StandardCharsets.UTF_8);
        adjust(report);
        List<Model> sprints = new ArrayList<>();
        sprints.add(story);
        ReportGenerator.generate(sprints, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < testReport.size(); i++) {
            assertEquals(testReport.get(i).trim(), report.get(i).trim());
        }
    }
}
