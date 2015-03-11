package sws.project.model;

import java.util.ArrayList;

/**
 * Model of a Team.
 */
public class Team extends Model{
    private String description;
    private ArrayList<Person> members = new ArrayList<>();
    private Person scrumMaster;
    private Person productOwner;

    /**
     * Returns a list of members in the team. Following the
     * Java method of adding to lists, the preferred method
     * of adding is to use getMembers().add(person);
     * @return A list of the team members
     */
    public ArrayList<Person> getMembers() {
        return members;
    }

    /**
     * A description of the team
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the team
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the scrum master
     * @return the scrum master
     */
    public Person getScrumMaster() {
        return scrumMaster;
    }

    /**
     * Sets the scrum master
     * @param scrumMaster The new scrum master
     */
    public void setScrumMaster(Person scrumMaster) {
        this.scrumMaster = scrumMaster;
    }

    /**
     * Gets the PO
     * @return the PO
     */
    public Person getProductOwner() {
        return productOwner;
    }

    /**
     * Sets the PO
     * @param productOwner the new PO
     */
    public void setProductOwner(Person productOwner) {
        this.productOwner = productOwner;
    }
}
