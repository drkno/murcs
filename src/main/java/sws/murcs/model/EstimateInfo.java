package sws.murcs.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;

/**
 * A class representing an estimated time remaining
 * for a task, sprint or story.
 */
public class EstimateInfo extends TrackableObject implements Serializable {
    /**
     * The serialization UID, for serialization.
     */
    private static final long serialVersionUID = 42L;

    /**
     * A map of the time remaining on specific days.
     */
    @TrackableValue
    private Map<LocalDate, Float> estimates = new HashedMap();

    /**
     * Creates a new time estimate.
     */
    public EstimateInfo() {

    }

    /**
     * Gets the estimate for the current day.
     * @return The estimate for today
     */
    public final float getCurrentEstimate() {
        return getEstimateForDay(LocalDate.now());
    }

    /**
     * Gets the current estimate for the task. This is given in hours.
     * @param day The day to get the estimate for
     * @return The current estimate for the task in hours.
     */
    public final float getEstimateForDay(final LocalDate day) {
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
    public final void setCurrentEstimate(final float newEstimate) {
        setEstimateForDay(newEstimate, LocalDate.now());
    }

    /**
     * Adds an amount to the estimate for a certain day.
     * @param amount The amount to add to the estimate
     * @param day The day to add the amount to
     */
    public final void addToEstimateForDay(final float amount, final LocalDate day) {
        float newEstimate = getEstimateForDay(day) + amount;
        setEstimateForDay(newEstimate, day);
    }

    /**
     * Sets the estimate for the task in hours.
     * @param newEstimate The new estimate for the task.
     * @param day The day you want to change the estimate for.
     */
    public final void setEstimateForDay(final float newEstimate, final LocalDate day) {
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

        //Update all the estimates after our new one
        for (LocalDate estimateDate : estimates.keySet()) {
            if (estimateDate.isAfter(day)) {
                float currentEstimate = estimates.get(estimateDate);
                //Make sure we only have positive or zero estimates
                estimates.put(estimateDate, Math.max(0, currentEstimate + difference));
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
        if (!(other instanceof EstimateInfo)) return false;

        EstimateInfo otherEstimate = (EstimateInfo)other;

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
    public void mergeIn(EstimateInfo...estimates) {
        List<EstimateInfo> estimateList = new ArrayList<>();
        Collections.addAll(estimateList, estimates);

        mergeIn(estimateList);
    }

    /**
     * Merges an array of estimates into this estimate.
     * @param estimates The estimates to merge into this one.
     */
    public void mergeIn(List<EstimateInfo> estimates) {
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
    public static final EstimateInfo merge(EstimateInfo...estimates) {
        List<EstimateInfo> estimateList = new ArrayList<>();
        Collections.addAll(estimateList, estimates);

        return merge(estimateList);
    }

    /**
     * Merges any number of estimates into a single estimate object.
     * @param estimates The estimates to merge
     * @return The resulting time estimate
     */
    public static final EstimateInfo merge(List<EstimateInfo> estimates){
        Map<LocalDate, Float> map = mergeToMap(estimates);
        EstimateInfo result = new EstimateInfo();
        result.getEstimates().putAll(map);

        return result;
    }

    /**
     * Merges in a list of time estimates, resulting in an estimate.
     * representing the total.
     * @param estimates The estimates to merge
     * @return The dates and their estimated times
     */
    private static final Map<LocalDate, Float> mergeToMap(List<EstimateInfo> estimates) {
        List<LocalDate> orderedDates = new ArrayList<>();
        for (EstimateInfo estimate : estimates) {
            estimate.getEstimates().keySet().forEach(orderedDates::add);
        }

        orderedDates.sort((o1, o2) -> {
            if (o1.isEqual(o2)) return 0;
            return o1.isBefore(o2) ? -1 : 1;
        });

        Map<LocalDate, Float> result = new HashMap<>();

        for (LocalDate date : orderedDates) {
            float total = 0;
            for (EstimateInfo estimate : estimates) {
                total+= estimate.getEstimateForDay(date);
            }

            result.put(date, total);
        }

        return result;
    }
}
