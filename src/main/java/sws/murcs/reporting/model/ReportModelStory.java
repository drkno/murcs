package sws.murcs.reporting.model;

import sws.murcs.model.Story;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelStory extends ReportModel {
    /**
     * The stories in the report.
     */
    @XmlElementWrapper(name = "stories")
    @XmlElement(name = "story")
    private List<Story> stories;

    /**
     * Constructor.
     * @param pStories stories
     */
    public ReportModelStory(final List<Story> pStories) {
        this.stories = pStories;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelStory() {
    }
}
