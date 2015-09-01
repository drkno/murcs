package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import sws.murcs.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A controller for the burndown tab on sprints.
 */
public class BurndownController extends GenericEditor<Sprint> {

    /**
     * The chart representing the burndown.
     */
    @FXML
    private LineChart burndownChart;

    /**
     * Burndown line that is aimed for.
     */
    private XYChart.Series<Long, Float> aimedBurndown;

    /**
     * Actual burndown line.
     */
    private XYChart.Series<Long, Float> burndown;

    /**
     * Burnup line showing effort spent.
     */
    private XYChart.Series<Long, Float> burnup;

    @Override
    public void loadObject() {
        NumberAxis xAxis = (NumberAxis) burndownChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(1);
        xAxis.setUpperBound(getDayNumber(getModel().getEndDate()));
        xAxis.setTickUnit(1);

        updateAimedBurndown();
        updateBurnUp();
        updateBurnDown();
    }

    /**
     * Gets the number of the day relative to the start of the sprint.
     * @param date date to get day number of.
     * @return the 1-based day number of the date in the sprint.
     */
    private long getDayNumber(final LocalDate date) {
        return date.toEpochDay() - getModel().getStartDate().toEpochDay() + 1;
    }

    /**
     * Updates the aimed burndown line with the data from the model.
     */
    private void updateAimedBurndown() {
        aimedBurndown.getData().clear();

        EstimateInfo estimateInfo = getModel().getEstimationInfo();

        aimedBurndown.getData().add(new XYChart.Data<>(0L, estimateInfo.getEstimateForDay(getModel().getStartDate())));
        aimedBurndown.getData().add(new XYChart.Data<>(getDayNumber(getModel().getEndDate()), 0f));
    }

    /**
     * Updates the burnup line with the data from the model.
     */
    private void updateBurnUp() {
        burnup.getData().clear();

        Map<LocalDate, Float> dates = new HashMap<>();

        for (Story story : getModel().getStories()) {
            for (Task task : story.getTasks()) {
                for (Effort effort : task.getEffort()) {
                    if (!dates.containsKey(effort.getDate())) {
                        dates.put(effort.getDate(), effort.getEffort());
                    }
                    else {
                        float current = dates.get(effort.getDate());
                        dates.put(effort.getDate(), effort.getEffort() + current);
                    }
                }
            }
        }

        List<XYChart.Data<Long, Float>> orderedDates = new ArrayList<>();
        orderedDates.add(new XYChart.Data<>(0L, 0f));
        for (LocalDate date : dates.keySet()) {
            orderedDates.add(new XYChart.Data<>(getDayNumber(date), dates.get(date)));
        }

        orderedDates.sort((o1, o2) -> Long.compare(o1.getXValue(), o2.getXValue()));

        float cumulativeEffort = 0;
        long offset = 0;
        for (int i = 0; i < orderedDates.size(); i++) {
            XYChart.Data<Long, Float> dataPoint = orderedDates.get(i);
            if (dataPoint.getXValue() != i + offset) {
                orderedDates.add(i, new XYChart.Data<>(dataPoint.getXValue() - 1, cumulativeEffort));
                i++;
                offset = dataPoint.getXValue() - i;
            }
            cumulativeEffort += dataPoint.getYValue();
            dataPoint.setYValue(cumulativeEffort);
        }

        burnup.getData().addAll(orderedDates);
    }

    /**
     * Updates the burndown line with the data from the from the model.
     */
    private void updateBurnDown() {
        List<Task> completedTasks = getModel().getStories().stream()
                .map(Story::getTasks).flatMap(Collection::stream)
                .filter(t -> t.getState() == TaskState.Done)
                .collect(Collectors.toList());

        float incompleteTaskTotal = getModel().getStories().stream()
                .map(Story::getTasks).flatMap(Collection::stream)
                .map(Task::getCurrentEstimate)
                .reduce(0F, (a, b) -> a + b);

        completedTasks.sort((o1, o2) -> o1.getCompletedDate().compareTo(o2.getCompletedDate()));
        List<XYChart.Data<Long, Float>> chartData = new ArrayList<>();
        float accumulator = incompleteTaskTotal;
        for (int i = completedTasks.size() - 1; i >= 0; i--) {
            Task current = completedTasks.get(i);
            if (i == completedTasks.size() - 1
                    || !completedTasks.get(i + 1).getCompletedDate().equals(current.getCompletedDate())) {
                chartData.add(0, new XYChart.Data<>(getDayNumber(current.getCompletedDate()), accumulator));
            }
            accumulator += current.getCurrentEstimate();
        }

        burndown.getData().addAll(chartData);
    }

    @Override
    protected void saveChangesAndErrors() {
        //Do nothing, we never make changes in this tab
    }

    @Override
    protected void initialize() {
        aimedBurndown = new XYChart.Series<>();
        aimedBurndown.setName("Aimed");

        burndown = new XYChart.Series<>();
        burndown.setName("Burndown      ");

        burnup = new XYChart.Series<>();
        burnup.setName("Burnup");

        burndownChart.setCreateSymbols(false);
        burndownChart.getData().addAll(aimedBurndown, burnup, burndown);
    }
}
