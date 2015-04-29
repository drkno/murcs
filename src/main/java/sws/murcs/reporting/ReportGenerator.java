package sws.murcs.reporting;

import sws.murcs.model.RelationalModel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.time.LocalDate;

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
    public static void generate(RelationalModel relationalModel, File file) throws JAXBException {
        ReportModel reportModel = new ReportModel(relationalModel);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportModel.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }
}
