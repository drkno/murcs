package sws.project.model;

import java.util.ArrayList;

/**
 * Model of a Project.
 */
public class Project extends Model {
    private String description;
    private ArrayList<Team> teams = new ArrayList<>();

    /**
     * Gets a description of the project
     * @return a description of the project
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the current project
     * @param description The description of the project
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets a list of teams working on the project
     * @return The teams working on this project
     */
    public ArrayList<Team> getTeams() {
        return teams;
    }
}
