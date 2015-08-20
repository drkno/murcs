package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.MultipleRolesException;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.search.Searchable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Model of a Team.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Team extends Model {

    /**
     * A list of members in the team.
     */
    @Searchable
    @TrackableValue
    @XmlElementWrapper(name = "members")
    @XmlElement(name = "person")
    @XmlIDREF
    private List<Person> members = new ArrayList<>();

    /**
     * The scrum master of the team.
     */
    @Searchable
    @TrackableValue
    @XmlIDREF
    private Person scrumMaster;

    /**
     * The product owner of the team.
     */
    @Searchable
    @TrackableValue
    @XmlIDREF
    private Person productOwner;

    /**
     * Returns a list of members in the team. Following the
     * Java method of adding to lists, the preferred method
     * of adding is to use getMembers().add(person);
     * @return A list of the team members
     */
    public final List<Person> getMembers() {
        return this.members;
    }

    /**
     * Gets the scrum master.
     * @return the scrum master
     */
    public final Person getScrumMaster() {
        return this.scrumMaster;
    }

    /**
     * Sets the scrum master.
     * @param newScrumMaster The new scrum master.
     * @throws MultipleRolesException if the new Scrum
     * Master is already performing another role.
     */
    public final void setScrumMaster(final Person newScrumMaster) throws MultipleRolesException {
        if (newScrumMaster == getProductOwner() && productOwner != null && getProductOwner() != null) {
            throw new MultipleRolesException("Scrum Master", "Product Owner", newScrumMaster, this);
        }
        this.scrumMaster = newScrumMaster;
        commit("edit team");
    }

    /**
     * Gets the PO.
     * @return the PO
     */
    public final Person getProductOwner() {
        return this.productOwner;
    }

    /**
     * Sets the Product Owner.
     * @param newProductOwner the new Product Owner.
     * @throws MultipleRolesException if the new
     * Product Owner is already performing another role.
     */
    public final void setProductOwner(final Person newProductOwner) throws MultipleRolesException {
        if (newProductOwner == getScrumMaster() && newProductOwner != null) {
            throw new MultipleRolesException("Product Owner", "Scrum Master", newProductOwner, this);
        }
        this.productOwner = newProductOwner;
        commit("edit team");
    }

    /**
     * Adds a person to the project members only
     * if that person is not already a member.
     * @param person to be added
     * @throws DuplicateObjectException
     * if the person is already in the team
     */
    public final void addMember(final Person person) throws DuplicateObjectException {
        if (!members.contains(person)) {
            this.members.add(person);
            commit("edit team");
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of people to the team.
     * @param membersToAdd Person to be added to the team
     * @throws DuplicateObjectException
     * if a person is already in a team
     */
    public final void addMembers(final List<Person> membersToAdd) throws DuplicateObjectException {
        for (Person member : membersToAdd) {
            this.addMember(member);
        }
    }

    /**
     * Removes a person from the project members.
     * @param person to be removed
     */
    public final void removeMember(final Person person) {
        if (this.members.contains(person)) {
            if (person.equals(scrumMaster)) {
                scrumMaster = null;
            }
            if (person.equals(productOwner)) {
                productOwner = null;
            }
            this.members.remove(person);
            commit("edit team");
        }
    }

    @Override
    public final boolean equals(final Object object) {
        if (object == null || !(object instanceof Team)) {
            return false;
        }
        String shortName = getShortName();
        String shortNameO = ((Team) object).getShortName();
        if (shortName == null || shortNameO == null) {
            return shortName == shortNameO;
        }
        return shortName.toLowerCase().equals(shortNameO.toLowerCase());
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c = getShortName().hashCode();
        }
        return getHashCodePrime() + c;
    }
}
