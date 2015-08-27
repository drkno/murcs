package sws.murcs.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;

/**
 * A class representing an estimated time remaining
 * for a task, sprint or story.
 */
public class TimeEstimate extends TrackableObject {
    /**
     * A map of the time remaining on specific days.
     */
    @TrackableValue
    private Map<LocalDate, Float> estimates = new HashedMap();

    /**
     * Creates a new time estimate.
     */
    public TimeEstimate() {
        UndoRedoManager.add(this);
    }

    /**
     * Gets the estimate for the current day.
     * @return The estimate for today
     */
    public final float getEstimate() {
        return getEstimate(LocalDate.now());
    }

    /**
     * Gets the current estimate for the task. This is given in hours.
     * @param day The day to get the estimate for
     * @return The current estimate for the task in hours.
     */
    public final float getEstimate(final LocalDate day) {
        LocalDate lastDate = null;
        for (LocalDate estimateDate : estimates.keySet()) {
            //If the estimate date is after our last date and before the day we're looking for
            if ((estimateDate.isBefore(day)
                    && (lastDate == null || lastDate.isBefore(estimateDate)))
                    || estimateDate.isEqual(day)) {
                lastDate = estimateDate;
            }
        }

        //If this day is before we have any estimates, return 0. Otherwise return the last
        //date before the day we asked for
        if (lastDate != null) {
            return estimates.get(lastDate);
        } else {
            return 0;
        }
    }

    /**
     * Updates the estimate for the current day.
     * @param newEstimate The new estimate for today
     */
    public final void setEstimate(final float newEstimate) {
        setEstimate(newEstimate, LocalDate.now());
    }

    /**
     * Adds an amount to the estimate for a certain day.
     * @param amount The amount to add to the estimate
     * @param day The day to add the amount to
     */
    public final void addToEstimate(final float amount, final LocalDate day) {
        addToEstimate(amount, day, true);
    }

    /**
     * Adds an amount to the estimate for a certain day.
     * @param amount The amount to add to the estimate
     * @param day The day to add the amount to
     * @param propagate Whether entries after this should be updated
     */
    private final void addToEstimate(final float amount, final LocalDate day, boolean propagate) {
        float newAmount = getEstimate(day) + amount;
        setEstimate(newAmount, day, propagate);
    }

    /**
     * Sets the estimate for the task in hours.
     * @param newEstimate The new estimate for the task.
     * @param day The day you want to change the estimate for.
     */
    public final void setEstimate(final float newEstimate, final LocalDate day) {
        setEstimate(newEstimate, day, true);
    }

    /**
     * Sets the estimate for the task in hours.
     * @param newEstimate The new estimate for the task.
     * @param day The day you want to change the estimate for.
     * @param propagate Indicates whether entries after this one should be updated
     */
    private final void setEstimate(final float newEstimate, final LocalDate day, boolean propagate) {
        LocalDate previousEstimateDate = null;

        for (LocalDate estimateDate : estimates.keySet()) {
            if ((estimateDate.isBefore(day)
                    && (previousEstimateDate == null || previousEstimateDate.isBefore(estimateDate)))
                    || estimateDate.isEqual(day)) {
                previousEstimateDate = estimateDate;
            }
        }

        float difference = newEstimate;
        if (previousEstimateDate != null) {
            difference = newEstimate - estimates.get(previousEstimateDate);
        }

        //Either update the estimate or add in the new estimate
        estimates.put(day, newEstimate);

        //If we should update other entries
        if (propagate) {
            //Update all the estimates after our new one
            for (LocalDate estimateDate : estimates.keySet()) {
                if (estimateDate.isAfter(day)) {
                    float currentEstimate = estimates.get(estimateDate);
                    //Make sure we only have positive or zero estimates
                    estimates.put(estimateDate, Math.max(0, currentEstimate + difference));
                }
            }
        }

        commit("edit task");
    }

    /**
     * Returns a list of all estimates for the task and their associated date.
     * @return The estimates
     */
    public Map<LocalDate, Float> getEstimates() {
        return estimates;
    }

    @Override
    public final boolean equals(Object other) {
        if (!(other instanceof TimeEstimate)) return false;

        TimeEstimate otherEstimate = (TimeEstimate)other;

        //Check that all the keys in the other have the same value in this and vice versa
        boolean allInFirst = otherEstimate.getEstimates().keySet().stream().allMatch(d -> otherEstimate.getEstimates().get(d).equals(getEstimates().get(d)));
        boolean allInSecond = getEstimates().keySet().stream().allMatch(d -> getEstimates().get(d).equals(otherEstimate.getEstimates().get(d)));

        return allInFirst && allInSecond;
    }

    @Override
    public final int hashCode() {
        return getEstimates().hashCode();
    }

    /**
     * Merges any number of time estimates into this one.
     * @param estimates The estimates to merge into this one
     */
    public void mergeIn(TimeEstimate...estimates) {
        List<TimeEstimate> estimateList = new ArrayList<>();
        Collections.addAll(estimateList, estimates);

        mergeIn(estimateList);
    }

    /**
     * Merges an array of estimates into this estimate.
     * @param estimates The estimates to merge into this one.
     */
    public void mergeIn(List<TimeEstimate> estimates) {
        //Add this estimate to the list, so we don't lose it's data
        estimates.add(this);

        Map<LocalDate, Float> map = mergeToMap(estimates);
        getEstimates().clear();
        getEstimates().putAll(map);
    }

    /**
     * Merges any number of estimates into a single estimate object.
     * @param estimates The estimates to merge
     * @return The resulting time estimate
     */
    public static final TimeEstimate merge(TimeEstimate...estimates) {
        List<TimeEstimate> estimateList = new ArrayList<>();
        Collections.addAll(estimateList, estimates);

        return merge(estimateList);
    }

    /**
     * Merges any number of estimates into a single estimate object.
     * @param estimates The estimates to merge
     * @return The resulting time estimate
     */
    public static final TimeEstimate merge(List<TimeEstimate> estimates){
        Map<LocalDate, Float> map = mergeToMap(estimates);
        TimeEstimate result = new TimeEstimate();
        result.getEstimates().putAll(map);

        return result;
    }

    /**
     * Merges in a list of time estimates, resulting in an estimate.
     * representing the total.
     * @param estimates The estimates to merge
     * @return The dates and their estimated times
     */
    private static final Map<LocalDate, Float> mergeToMap(List<TimeEstimate> estimates) {
        List<LocalDate> orderedDates = new ArrayList<>();
        for (TimeEstimate estimate : estimates) {
            estimate.getEstimates().keySet().forEach(orderedDates::add);
        }

        orderedDates.sort((o1, o2) -> {
            if (o1.isEqual(o2)) return 0;
            return o1.isBefore(o2) ? -1 : 1;
        });

        Map<LocalDate, Float> result = new HashMap<>();

        for (LocalDate date : orderedDates) {
            float total = 0;
            for (TimeEstimate estimate : estimates) {
                total+= estimate.getEstimate(date);
            }

            result.put(date, total);
        }

        return result;
    }
}
