package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.MultipleRolesException;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

/**
 * Model of a Team.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Team extends Model {

    @TrackableValue
    private String description;
    @TrackableValue
    @XmlElementWrapper(name = "members")
    @XmlElement(name = "person")
    private ArrayList<Person> members = new ArrayList<>();
    @TrackableValue
    private Person scrumMaster;
    @TrackableValue
    private Person productOwner;

    /**
     * Returns a list of members in the team. Following the
     * Java method of adding to lists, the preferred method
     * of adding is to use getMembers().add(person);
     * @return A list of the team members
     */
    public ArrayList<Person> getMembers() {
        return this.members;
    }

    /**
     * A description of the team
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the team
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
        commit("edit team");
    }

    /**
     * Gets the scrum master
     * @return the scrum master
     */
    public Person getScrumMaster() {
        return this.scrumMaster;
    }

    /**
     * Sets the scrum master.
     * @param scrumMaster The new scrum master.
     * @throws MultipleRolesException if the new Scrum Master is already performing another role.
     */
    public void setScrumMaster(Person scrumMaster) throws MultipleRolesException {
        if (scrumMaster == getProductOwner() && productOwner != null && getProductOwner() != null) {
            throw new MultipleRolesException("Scrum Master", "Product Owner", scrumMaster, this);
        }
        this.scrumMaster = scrumMaster;
        commit("edit team");
    }

    /**
     * Gets the PO
     * @return the PO
     */
    public Person getProductOwner() {
        return this.productOwner;
    }

    /**
     * Sets the Product Owner.
     * @param productOwner the new Product Owner.
     * @throws MultipleRolesException if the new Product Owner is already performing another role.
     */
    public void setProductOwner(Person productOwner) throws MultipleRolesException {
        if (productOwner == getScrumMaster() && productOwner != null && getScrumMaster() != null) {
            throw new MultipleRolesException("Product Owner", "Scrum Master", productOwner, this);
        }
        this.productOwner = productOwner;
        commit("edit team");
    }

    /**
     * Adds a person to the project members only if that person is not already a member
     * @param person to be added
     * @throws sws.murcs.exceptions.DuplicateObjectException if the person is already in the team
     */
    public void addMember(Person person) throws DuplicateObjectException {
        if (!members.contains(person)) {
            this.members.add(person);
            commit("edit team");
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of people to the team
     * @param members People to be added to the team
     * @throws sws.murcs.exceptions.DuplicateObjectException if a person is already in a team
     */
    public void addMembers(ArrayList<Person> members) throws DuplicateObjectException {
        for (Person member : members) {
            this.addMember(member);
        }
    }

    /**
     * Removes a person from the project members
     * @param person to be removed
     */
    public void removeMember(Person person) {
        if (this.members.contains(person)) {
            this.members.remove(person);
            commit("edit team");
        }
    }

    /**
     * Returns the string of the short name
     * @return short name string
     */
    @Override
    public String toString() {
        return getShortName();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Team)) return false;
        String shortName = getShortName();
        String shortNameO = ((Team) object).getShortName();
        if (shortName == null || shortNameO == null) return shortName == shortNameO;
        return shortName.toLowerCase().equals(shortNameO.toLowerCase());
    }
}
