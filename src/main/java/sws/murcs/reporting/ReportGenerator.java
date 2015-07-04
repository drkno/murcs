package sws.murcs.reporting;

import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.Team;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * Contains a static method for generating the xml status report from the organisation.
 */
public abstract class ReportGenerator {

    /**
     * Generates an xml report to file from a organisation.
     * @param organisation the model from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB
     */
    public static void generate(final Organisation organisation, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeader(organisation);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from a model.
     * @param model the model from which to generate the report
     * @param file the file to output the report to
     * @throws JAXBException Exceptions from JAXB
     */
    public static void generate(final Model model, final File file) throws JAXBException {
        ModelType type = ModelType.getModelType(model);
        switch (type) {
            case Project:
                generate((Project) model, file);
                break;
            case Team:
                generate((Team) model, file);
                break;
            case Person:
                generate((Person) model, file);
                break;
            default:
                throw new UnsupportedOperationException("Report generation for this model type is yet to be implemented");
        }
    }

    /**
     * Generates an xml report to file from a project.
     * @param project the model from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB
     */
    private static void generate(final Project project, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeader(project);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from a team.
     * @param team the model from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB
     */
    private static void generate(final Team team, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeader(team);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    /**
     * Generates an xml report to file from a person.
     * @param person the model from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions from JAXB
     */
    private static void generate(final Person person, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeader(person);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }
}
