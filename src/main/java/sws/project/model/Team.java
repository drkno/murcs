package sws.project.model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.management.relation.Role;
import java.util.ArrayList;

/**
 *
 */

public class Team extends Model{
    private Project project;
    private String description;

    private ArrayList<Person> members = new ArrayList<>();

    /**
     * Gets the project the team is associated with
     * @return The teams project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project the team is associated with
     * @param project The new project for the team to work on
     */
    public void setProject(Project project) {
        this.project = project;
    }

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
}
