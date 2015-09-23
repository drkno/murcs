package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.search.Searchable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model of a person.
 */
@XmlRootElement
@XmlType(propOrder = {"userId", "skills"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Person extends Model {

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    private static final long serialVersionUID = 0L;

    /**
     * The user id of a person.
     */
    @Searchable
    @TrackableValue
    @XmlElement(name = "id")
    private String userId;

    /**
     * The list of skills the person has.
     */
    @Searchable
    @TrackableValue
    @XmlElementWrapper(name = "skills")
    @XmlElement(name = "skill")
    @XmlIDREF
    private List<Skill> skills = new ArrayList<>();

    /**
     * Gets a list of the skills the person has. Following the
     * pattern Java uses with JavaFX, changing the returned
     * list is the preferred way to add items to the list.
     * @return The person's skills.
     */
    public final List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    /**
     * Indicates if this person has the skill to take on
     * a particular role.
     * @param role The role
     * @return Whether the person can take on a role.
     */
    public final boolean canBeRole(final String role) {
        return getSkills()
                .stream()
                .filter(skill -> skill.getShortName().equals(role))
                .findFirst()
                .isPresent();
    }

    /**
     * Gets the user id.
     * @return The user id
     */
    public final String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     * @param newUserID The new user id
     * @throws InvalidParameterException User id is invalid
     * @throws DuplicateObjectException User id already exists
     */
    public final void setUserId(final String newUserID) throws InvalidParameterException, DuplicateObjectException {
        validateUserId(newUserID);
        this.userId = newUserID.trim();
        commit("edit person");
    }

    /**
     * Indicates whether a value is a valid value for 'userId' to hold.
     * @param value The value.
     * @throws InvalidParameterException if the user id is invalid.
     * @throws DuplicateObjectException if the user id already exists.
     */
    private void validateUserId(final String value) throws InvalidParameterException, DuplicateObjectException {
        Person model = UsageHelper.findBy(ModelType.Person, m -> m.getUserId().equalsIgnoreCase(value));
        if (model != null) {
            throw new DuplicateObjectException("A person with this ID already exists.");
        }
        InvalidParameterException.validate("User Id", value);
    }

    /**
     * Adds a skill to skills only if the person does not
     * already have that skill.
     * @param skill The skill to add
     * @throws DuplicateObjectException if
     * the person already has that skill
     */
    public final void addSkill(final Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill)) {
            this.skills.add(skill);
            commit("edit person");
        }
        else {
            throw new DuplicateObjectException("This is actually the same skill");
        }
    }

    /**
     * Adds a list of skills to the persons skills.
     * @param skillsToAdd Skill to be added to person
     * @throws DuplicateObjectException if the
     * person has any of the skills in the list
     */
    public final void addSkills(final List<Skill> skillsToAdd) throws DuplicateObjectException {
        for (Skill skill : skillsToAdd) {
            this.addSkill(skill);
        }
    }

    /**
     * Removes a skill from skills.
     * @param skill The skill to remove
     */
    public final void removeSkill(final Skill skill) {
        if (skills.contains(skill)) {
            this.skills.remove(skill);
            commit("edit person");
        }
    }

    /**
     * Checks to see if to people are equal.
     * @param object Person to compare
     * @return boolean state
     */
    @Override
    public final boolean equals(final Object object) {
        if (object == null || !(Person.class == object.getClass())) {
            return false;
        }
        Person person = (Person) object;
        String shortName1 = person.getShortName();
        String shortName2 = getShortName();
        if (shortName1 == null || shortName2 == null) {
            return shortName1 == shortName2;
        }
        return shortName1.equalsIgnoreCase(shortName2) || person.getUserId().equals(getUserId());
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c += getShortName().hashCode();
        }
        if (getUserId() != null) {
            c += getUserId().hashCode();
        }

        return getHashCodePrime() + c;
    }

    /**
     * Clears the skill that a person has.
     */
    public final void clearSkills() {
        skills.clear();
    }
}
