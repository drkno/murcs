package sws.murcs.reporting;

import sws.murcs.model.Backlog;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Team;
import sws.murcs.reporting.header.ReportHeader;
import sws.murcs.reporting.header.ReportHeaderAll;
import sws.murcs.reporting.header.ReportHeaderBacklog;
import sws.murcs.reporting.header.ReportHeaderPerson;
import sws.murcs.reporting.header.ReportHeaderProject;
import sws.murcs.reporting.header.ReportHeaderSprint;
import sws.murcs.reporting.header.ReportHeaderStory;
import sws.murcs.reporting.header.ReportHeaderTeam;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains a static method for generating the xml status report from the organisation.
 */
public final class ReportGenerator {

    /**
     * Private default constructor as this is a helper class.
     */
    private ReportGenerator() {
    }

    /**
     * Generates an xml report to file from a organisation.
     * @param organisation the model from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB that are thrown during serialisation.
     */
    public static void generate(final Organisation organisation, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeaderAll(organisation);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from a model.
     * @param model the model from which to generate the report
     * @param file the file to output the report to
     * @throws JAXBException Exceptions from JAXB that are thrown during serialisation.
     */
    public static void generate(final List<Model> model, final File file) throws JAXBException {
        ModelType type = ModelType.getModelType(model.get(0));
        switch (type) {
            case Project:
                List<Project> projects = model
                        .stream()
                        .map(e -> (Project) e)
                        .collect(Collectors.toList());
                generateProjects(projects, file);
                break;
            case Team:
                List<Team> teams = model
                        .stream()
                        .map(e -> (Team) e)
                        .collect(Collectors.toList());
                generateTeams(teams, file);
                break;
            case Person:
                List<Person> people = model
                        .stream()
                        .map(e -> (Person) e)
                        .collect(Collectors.toList());
                generatePeople(people, file);
                break;
            case Backlog:
                List<Backlog> backlogs = model
                        .stream()
                        .map(e -> (Backlog) e)
                        .collect(Collectors.toList());
                generateBacklogs(backlogs, file);
                break;
            case Story:
                List<Story> stories = model
                        .stream()
                        .map(e -> (Story) e)
                        .collect(Collectors.toList());
                generateStories(stories, file);
                break;
            case Sprint:
                List<Sprint> sprints = model
                        .stream()
                        .map(e -> (Sprint) e)
                        .collect(Collectors.toList());
                generateSprints(sprints, file);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Report generation for this model type is yet to be implemented");
        }
    }

    /**
     * Generates an xml report to file from a project.
     * @param projects the models from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB that are thrown during serialisation.
     */
    private static void generateProjects(final List<Project> projects, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeaderProject(projects);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from a team.
     * @param teams the teams from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB that are thrown during serialisation.
     */
    private static void generateTeams(final List<Team> teams, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeaderTeam(teams);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from a person.
     * @param people the people from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB that are thrown during serialisation.
     */
    private static void generatePeople(final List<Person> people, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeaderPerson(people);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from backlogs.
     * @param backlogs the backlogs from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB that are thrown during serialisation.
     */
    private static void generateBacklogs(final List<Backlog> backlogs, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeaderBacklog(backlogs);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from stories.
     * @param stories the stories from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB that are thrown during serialisation.
     */
    private static void generateStories(final List<Story> stories, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeaderStory(stories);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from stories.
     * @param sprints the stories from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB that are thrown during serialisation.
     */
    private static void generateSprints(final List<Sprint> sprints, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeaderSprint(sprints);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }
}
