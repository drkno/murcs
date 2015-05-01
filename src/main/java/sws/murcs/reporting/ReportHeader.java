package sws.murcs.reporting;

import sws.murcs.model.RelationalModel;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
/**'
 * A class that gets the information needed for the header of the report, such as the product version and date generated
 */
public class ReportHeader {

    private float projectVersion;
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    private LocalDate dateGenerated;
    @XmlElement(name = "content")
    private ReportModel reportModel;

    public ReportHeader(RelationalModel relationalModel) {
        reportModel = new ReportModel(relationalModel);
        projectVersion = RelationalModel.getVersion();
        dateGenerated = LocalDate.now();
    }

    @SuppressWarnings("unused")
    private ReportHeader() {}
}
