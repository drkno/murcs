package sws.project.model;

import sws.project.exceptions.DuplicateObjectException;
import sws.project.magic.easyedit.Editable;
import sws.project.magic.easyedit.fxml.BasicPaneGenerator;
import sws.project.magic.easyedit.fxml.FxmlPaneGenerator;
import sws.project.magic.tracking.TrackValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Model of a Project.
 */
public class Project extends Model {
    @Editable(editPaneGenerator = BasicPaneGenerator.class, sort = 0)
    @TrackValue
    private String description;
    @Editable(editPaneGenerator = BasicPaneGenerator.class, sort = 99)
    @TrackValue
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
        saveCurrentState("Description change");
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
     * @throws sws.project.exceptions.DuplicateObjectException if the project already has that team
     */
    public void addTeam(Team team) throws DuplicateObjectException{
        if (!this.teams.contains(team) &&
                !this.teams
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(team.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.teams.add(team);
            saveCurrentState("Team added");
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of teams to add to the project
     * @param teams Teams to be added to the project
     * @throws sws.project.exceptions.DuplicateObjectException if the project already has a team from teams to be added
     */
    public void addTeams(List<Team> teams) throws DuplicateObjectException {
        for (Team team: teams) {
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
            saveCurrentState("Team removed");
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
