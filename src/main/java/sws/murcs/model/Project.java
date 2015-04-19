package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.magic.easyedit.Editable;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Model of a Project.
 */
public class Project extends Model {
    @Editable()
    @TrackableValue
    private String description;
    @Editable(sort = 99)
    @TrackableValue
    @XmlElementWrapper(name = "teams")
    @XmlElement(name = "team")
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

    /**
     * Adds a team to this project if the project does not already have that team
     * @param team team to add.
     * @throws sws.murcs.exceptions.DuplicateObjectException if the project already has that team
     */
    public void addTeam(Team team) throws DuplicateObjectException {
        if (!this.teams.contains(team) &&
                !this.teams
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(team.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.teams.add(team);
        } else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of teams to add to the project
     * @param teams Teams to be added to the project
     * @throws sws.murcs.exceptions.DuplicateObjectException if the project already has a team from teams to be added
     */
    public void addTeams(List<Team> teams) throws DuplicateObjectException {
        for (Team team : teams) {
            this.addTeam(team);
        }
    }

    /**
     * Remove a team from this project.
     * @param team team to remove.
     */
    public void removeTeam(Team team) {
        if (this.teams.contains(team)) {
            teams.remove(team);
        }
    }

    /**
     * Returns the short name of the project
     * @return Short name of the project
     */
    @Override
    public String toString() {
        return getShortName();
    }
}
