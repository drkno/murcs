package sws.murcs.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A group of people who have peer programmed together.
 */
public class PeerProgrammingGroup {

    /**
     * Members of the group that have programmed together.
     */
    @XmlElement(name = "pairMembers")
    private String groupMembers;

    /**
     * The time spent programming together.
     */
    @XmlElement(name = "timeSpent")
    private float timeSpent;

    /**
     * Total time, used to generate proportions.
     */
    @XmlTransient
    private float totalTime;

    /**
     * Creates a new peer programming group.
     * @param group the group members.
     * @param spent the time spent.
     * @param total the total time spent.
     */
    public PeerProgrammingGroup(final String group, final float spent, final float total) {
        groupMembers = group;
        timeSpent = spent;
        totalTime = total;
    }

    /**
     * Gets the percentage of the total time this group was peer programming.
     * @return percentage.
     */
    @XmlElement(name = "percentageOfTotal")
    public float getProportionTime() {
        final int hundred = 100;
        return timeSpent / totalTime * hundred;
    }

    /**
     * Gets all the group members.
     * @return the group members.
     */
    public String getGroupMembers() {
        return groupMembers;
    }

    /**
     * Gets the time spent in this group.
     * @return the time spent.
     */
    public float getTimeSpent() {
        return timeSpent;
    }

    /**
     * Get the total time spent.
     * @return total time spent.
     */
    public float getTotalTime() {
        return totalTime;
    }
}
