package sws.murcs.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;

/**
 * A class representing a single
 * acceptance condition on a story.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AcceptanceCondition extends TrackableObject implements Serializable {

    /**
     * The text representing the condition.
     */
    @TrackableValue
    private String condition;

    /**
     * Gets a string describing this condition.
     * @return The condition
     */
    public final String getCondition() {
        return condition;
    }

    /**
     * Sets the condition that this acceptance condition
     * describes.
     * @param newCondition The new condition
     */
    public final void setCondition(final String newCondition) {
        this.condition = newCondition;
        commit("edit condition");
    }
}
