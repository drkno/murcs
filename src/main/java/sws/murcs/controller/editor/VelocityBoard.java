package sws.murcs.controller.editor;

import sws.murcs.model.Team;

/**
 * Controller for VelocityBoard.
 */
public class VelocityBoard {

    /**
     * The team to display the velocity for.
     */
    private Team team;

    /**
     * Sets the team of this velocity board.
     * @param pTeam The team to set to.
     */
    protected void setTeam(final Team pTeam) {
        team = pTeam;
    }

    protected void loadObject() {
        if (team == null) {
            throw new NullPointerException("Team is null in the velocity board");
        }
    }

    public void dispose() {
        team = null;
    }
}
