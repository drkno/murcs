package sws.murcs.reporting;

import sws.murcs.model.RelationalModel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
public abstract class ReportGenerator {

    public static void generate(RelationalModel relationalModel, File file) throws JAXBException {
        ReportModel reportModel = new ReportModel(relationalModel);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportModel.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, file);
    }

    public static void generate(RelationalModel relationalModel) throws JAXBException {
        ReportModel reportModel = new ReportModel(relationalModel);

        JAXBContext jaxbContext = JAXBContext.newInstance(ReportModel.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(reportModel, System.out);
    }
}
