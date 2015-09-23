package sws.murcs.model;

public class PeerProgrammingGroup {
    private String groupMembers;

    private float estimation;

    public PeerProgrammingGroup(final String group, final float estimate) {
        groupMembers = group;
        estimation = estimate;
    }

    public String getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }

    public float getEstimation() {
        return estimation;
    }

    public void setEstimation(float estimation) {
        this.estimation = estimation;
    }
}
