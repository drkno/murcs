package sws.murcs.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import sws.murcs.exceptions.InvalidParameterException;
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
     * @throws InvalidParameterException if the condition is blank or null
     */
    public final void setCondition(final String newCondition) throws InvalidParameterException {
        if (newCondition == null || newCondition.isEmpty()) {
            throw new InvalidParameterException("You can't have an empty AC");
        }
        condition = newCondition;
        commit("edit condition");
    }
}
