package sws.murcs.search.tokens;

/**
 * Bang command that represents a special command that can be performed.
 */
public class BangCommand {

    /**
     * Commands that will trigger this bang command.
     */
    private String[] commands;

    /**
     * Description of what this command does.
     */
    private String description;

    /**
     * Event that will be fired when this bang command is set.
     */
    private SpecialTokenEvent setEvent;

    /**
     * Creates a new Bang command.
     * @param longCommand the long version of this command.
     * @param shortCommand the short version of this command.
     * @param theDescription the description of what this command does.
     * @param event the event to fire when using this command.
     */
    public BangCommand(final String longCommand, final String shortCommand,
                       final String theDescription, final SpecialTokenEvent event) {
        commands = new String[] {"!" + longCommand, "!" + shortCommand};
        description = theDescription;
        setEvent = event;
    }

    /**
     * Gets an array of commands that can be performed.
     * @return the commands.
     */
    public final String[] getCommands() {
        return commands;
    }

    /**
     * Gets the description associated with this command.
     * @return the description.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the value of this command to the new value provided.
     * @param newValue the new value to set it to.
     */
    public final void setValue(final boolean newValue) {
        setEvent.setValue(newValue);
    }
}
