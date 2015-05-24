package sws.murcs.model;

import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.observable.ModelObjectProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Contains the basic model for each object type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Model extends TrackableObject implements Serializable {

    /**
     * The short name of a model object.
     */
    @TrackableValue
    @XmlAttribute
    private String shortName;
    /**
     * The long name of a model object.
     */
    @TrackableValue
    private String longName;
    /**
     * Listenable property for the short name.
     */
    private transient ModelObjectProperty<String> shortNameProperty;
    /**
     * Hashcode prime number.
     */
    @XmlTransient
    private final int hashCodePrime = 37;

    /**
     * Gets the short name.
     * @return the short name.
     */
    public final String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name.
     * @param newShortName the new short name.
     * @throws CustomException if the short name is invalid.
     */
    public final void setShortName(final String newShortName) throws CustomException {
        validateShortName(newShortName);
        shortName = newShortName.trim();
        if (shortNameProperty != null) {
            shortNameProperty.notifyChanged();
        }
        commit("edit " + getClass().getSimpleName().toLowerCase());
    }

    /**
     * Indicates whether a value is a valid value for 'shortName' to hold.
     * @param value The value.
     * @throws CustomException if the short name is invalid.
     */
    private void validateShortName(final String value) throws CustomException {
        ModelType type = ModelType.getModelType(getClass());
        Model model = UsageHelper.findBy(type, m -> m.getShortName().equalsIgnoreCase(value));
        if (model != null) {
            throw new DuplicateObjectException("A " + type + " with this name already exists.");
        }
        InvalidParameterException.validate("Short Name", value);
    }

    /**
     * Gets the long name.
     * @return the long name.
     */
    public final String getLongName() {
        return longName;
    }

    /**
     * Sets the long name.
     * @param newLongName the new long name
     */
    public final void setLongName(final String newLongName) {
        longName = newLongName;
        commit("edit " + getClass().getSimpleName().toLowerCase());
    }

    /**
     * Listenable property for the shortName.
     * @return property for the shortName.
     */
    public final ModelObjectProperty<String> getShortNameProperty() {
        if (shortNameProperty == null) {
            try {
                shortNameProperty = new ModelObjectProperty<>(this, Model.class, "shortName");
            } catch (Exception e) {
                System.err.println("Couldn't create property for shortName. Failed with error:");
                e.printStackTrace();
            }
        }
        return shortNameProperty;
    }

    /**
     * Get the hash code prime.
     * @return the hash code prime.
     */
    public final int getHashCodePrime() {
        return hashCodePrime;
    }

    @Override
    public final String toString() {
        return getShortName();
    }
}
