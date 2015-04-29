package sws.murcs.reporting;

import sws.murcs.model.RelationalModel;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
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
