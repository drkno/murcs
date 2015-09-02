package sws.murcs.controller.editor;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import sws.murcs.model.Effort;
import sws.murcs.model.EstimateInfo;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Series<Long, Float> aimedBurndown;

    /**
     * Actual burndown line.
     */
    private Series<Long, Float> burndown;

    /**
     * Burnup line showing effort spent.
     */
    private Series<Long, Float> burnup;

    @Override
    public void loadObject() {
        NumberAxis xAxis = (NumberAxis) burndownChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(getDayNumber(getModel().getEndDate()));
        xAxis.setTickUnit(1);

        burndownChart.getData().clear();

        long taskCount = getModel().getStories().stream().map(Story::getTasks).flatMap(Collection::stream).count();
        if (getDayNumber(LocalDate.now()) >= 0 && taskCount > 0) {
            burndownChart.setVisible(true);
            // cant use clear due to an IllegalArgumentException when re-adding
            // readding done because of weird issues with graphs
            aimedBurndown = new Series<>("Aimed", FXCollections.<Data<Long, Float>>observableArrayList());
            burndown = new Series<>("Burndown      ", FXCollections.<Data<Long, Float>>observableArrayList());
            burnup = new Series<>("Burnup", FXCollections.<Data<Long, Float>>observableArrayList());
            updateAimedBurndown();
            updateBurnUp();
            updateBurnDown();
            burndownChart.getData().setAll(aimedBurndown, burnup, burndown);
        }
        else {
            burndownChart.setVisible(false);
        }
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
        EstimateInfo estimateInfo = getModel().getEstimationInfo();
        float initialEstimate = estimateInfo.getEstimateForDay(getModel().getStartDate());
        aimedBurndown.getData().add(new Data<>(0L, initialEstimate));
        aimedBurndown.getData().add(new Data<>(getDayNumber(getModel().getEndDate()), 0f));
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

        List<Data<Long, Float>> orderedDates = new ArrayList<>();
        orderedDates.add(new Data<>(0L, 0f));
        for (LocalDate date : dates.keySet()) {
            orderedDates.add(new Data<>(getDayNumber(date), dates.get(date)));
        }

        orderedDates.sort((o1, o2) -> Long.compare(o1.getXValue(), o2.getXValue()));

        float cumulativeEffort = 0;
        long offset = 0;
        for (int i = 0; i < orderedDates.size(); i++) {
            Data<Long, Float> dataPoint = orderedDates.get(i);
            if (dataPoint.getXValue() != i + offset) {
                orderedDates.add(i, new Data<>(dataPoint.getXValue() - 1, cumulativeEffort));
                i++;
                offset = dataPoint.getXValue() - i;
            }
            cumulativeEffort += dataPoint.getYValue();
            dataPoint.setYValue(cumulativeEffort);
        }

        long currentNumber = Math.min(getDayNumber(LocalDate.now()), getDayNumber(getModel().getEndDate()));
        long lastNumber = orderedDates.get(orderedDates.size() - 1).getXValue();
        if (lastNumber < currentNumber) {
            orderedDates.add(new Data<>(currentNumber, orderedDates.get(orderedDates.size() - 1).getYValue()));
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
                .filter(t -> t.getState() != TaskState.Done)
                .map(Task::getCurrentEstimate)
                .reduce(0F, (a, b) -> a + b);

        completedTasks.sort((o1, o2) -> o1.getCompletedDate().compareTo(o2.getCompletedDate()));
        List<Data<Long, Float>> chartData = new ArrayList<>();
        float accumulator = incompleteTaskTotal;
        for (int i = completedTasks.size() - 1; i >= 0; i--) {
            Task current = completedTasks.get(i);
            if (i == completedTasks.size() - 1
                    || !completedTasks.get(i + 1).getCompletedDate().equals(current.getCompletedDate())) {
                chartData.add(0, new Data<>(getDayNumber(current.getCompletedDate()), accumulator));
            }
            accumulator += current.getCurrentEstimate();
        }
        chartData.add(0, new Data<>(0L, accumulator));

        long currentNumber = Math.min(getDayNumber(LocalDate.now()), getDayNumber(getModel().getEndDate()));
        long lastNumber = chartData.get(chartData.size() - 1).getXValue();
        if (lastNumber < currentNumber) {
            chartData.add(new Data<>(currentNumber, chartData.get(chartData.size() - 1).getYValue()));
        }

        burndown.getData().addAll(chartData);
    }

    @Override
    protected void saveChangesAndErrors() {
        // Do nothing, we never make changes in this tab, errors should never occur in this tab and
        // we do not need to listen for updates in this tab (they will be forced with a loadObject())
    }

    @Override
    protected void initialize() {
        burndownChart.setCreateSymbols(false);
    }
}
