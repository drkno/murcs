package sws.murcs.model;

import sws.murcs.magic.easyedit.Editable;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.magic.tracking.TrackableValue;

import java.time.LocalDate;
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
    private List<WorkAllocation> allocations = new ArrayList<>();

    /**
     * Gets a description of the project
     * @return a description of the project
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the current project
     * @param description The description of the project
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets a list of all ongoing and future work periods
     * @return The list of team allocations
     */
    public List<WorkAllocation> getAllocations() {
        LocalDate currentDate = LocalDate.now();
        List<WorkAllocation> allocations = new ArrayList<>();
        for (WorkAllocation allocation : this.allocations) {
            if (allocation.getEndDate().isBefore(currentDate)) {
                continue;
            }
            allocations.add(allocation);
        }
        return allocations;
    }

    /**
     * Adds a team to this project if the project does not already have that team
     * @param workAllocation The allocation to add
     * @throws sws.murcs.exceptions.DuplicateObjectException if the project already has that team
     */
    public void addAllocation(WorkAllocation workAllocation) throws DuplicateObjectException {
        Team team = workAllocation.getTeam();
        LocalDate startDate = workAllocation.getStartDate();
        LocalDate endDate = workAllocation.getEndDate();

        int index = 0;
        for (WorkAllocation allocation : this.allocations) {
            if (allocation.getTeam() == team) {
                // Check that this team isn't overlapping with itself
                if ((allocation.getStartDate().isBefore(endDate) && allocation.getEndDate().isAfter(startDate))) {
                    // TODO Create my own exception like "OverlappedAllocationException"
                    throw new DuplicateObjectException("Work Dates Overlap");
                }
            }
            if (allocation.getStartDate().isBefore(startDate)) {
                // Increment the index where the allocation will be placed if it does get placed
                index++;
            }
            else if (allocation.getStartDate().isAfter(endDate)) {
                // At this point we've checked all overlapping allocations and haven't found any errors
                break;
            }
        }
        this.allocations.add(index, workAllocation);
    }

    /**
     * Adds a list of allocations to add to the project
     * @param allocations Teams to be added to the project
     * @throws sws.murcs.exceptions.DuplicateObjectException if the project already has a team from allocations to be added
     */
    public void addAllocations(List<WorkAllocation> allocations) throws DuplicateObjectException {
        for (WorkAllocation allocation : allocations) {
            this.addAllocation(allocation);
        }
    }

    /**
     * Remove a teams work period from this project.
     * @param allocation Team to deallocate to remove.
     */
    public void removeAllocation(WorkAllocation allocation) {
        if (this.allocations.contains(allocation)) {
            this.allocations.remove(allocation);
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
