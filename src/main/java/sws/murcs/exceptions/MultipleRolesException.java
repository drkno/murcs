package sws.murcs.exceptions;

import sws.murcs.model.Person;
import sws.murcs.model.Team;

/**
 * Exception for when a person is set to multiple roles.
 */
public class MultipleRolesException extends CustomException {

    /**
     * The team affected.
     */
    private Team affectedTeam;

    /**
     * the person affected.
     */
    private Person affectedPerson;

    /**
     * Creates a new MultipleRolesException.
     * @param newRole The role that is being set.
     * @param existingRole The role that the person is being set for.
     * @param person The person.
     * @param team The team.
     */
    public MultipleRolesException(final String newRole, final String existingRole, final Person person,
                                  final Team team) {
        super(person.toString() + " cannot be assigned to both the \"" + newRole
                + "\" and \"" + existingRole + "\" roles within a team.");
        affectedTeam = team;
        affectedPerson = person;
    }

    /**
     * Gets the person affected by this conflict.
     * @return the affected person.
     */
    public final Person getAffectedPerson() {
        return affectedPerson;
    }

    /**
     * Gets the team affected by this conflict.
     * @return the affected team.
     */
    public final Team getAffectedTeam() {
        return affectedTeam;
    }
}
