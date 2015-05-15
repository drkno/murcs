package sws.murcs.reporting;

import sws.murcs.model.RelationalModel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * Contains a static method for generating the xml status report from the relational model.
 */
public abstract class ReportGenerator {

    /**
     * Generates an xml report to file from a relational model.
     * @param relationalModel the model from which to create the report
     * @param file the file to output the report
     * @throws JAXBException Exceptions fro JAXB
     */
    public static void generate(final RelationalModel relationalModel, final File file) throws JAXBException {
        ReportHeader reportModel = new ReportHeader(relationalModel);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportHeader.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }
}
