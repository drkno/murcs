package sws.murcs.unit.reporting;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.model.RelationalModel;
import sws.murcs.reporting.ReportGenerator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
public class ReportGeneratorTest {

    private RelationalModel relationalModel;
    private File tempReport;
    private List<String> sampleReport;

    @Before
    public void setUp() throws Exception {
        String sampleReportPath = "./src/test/resources/sws/murcs/reporting/sampleReport.xml";
        sampleReport = Files.readAllLines(Paths.get(sampleReportPath), StandardCharsets.UTF_8);
        tempReport = Files.createTempFile("", "").toFile();

        relationalModel = new RelationalModel();
        /*
        Build bigger sample relational model
         */
    }

    @After
    public void tearDown() throws Exception {
        tempReport.delete();
    }

    @Test
    public void testGenerate() throws Exception {
        sampleReport.forEach((String s) -> System.out.println(s));

        ReportGenerator.generate(relationalModel, tempReport);

        List<String> testReport = Files.readAllLines(tempReport.toPath(), StandardCharsets.UTF_8);
        for (int i = 0; i < sampleReport.size(); i++) {
            assertEquals(sampleReport.get(i), testReport.get(i));
        }
    }
}