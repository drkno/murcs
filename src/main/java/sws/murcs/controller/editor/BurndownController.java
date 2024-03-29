package sws.murcs.controller.editor;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.model.EstimateInfo;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;
import sws.murcs.controller.GenericPopup;
import sws.murcs.model.persistence.PersistenceManager;

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
     * The axies of the chart.
     */
    @FXML
    private NumberAxis xAxis;

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

    /**
     * Has the warning about generated data been shown?
     */
    private static boolean hasShownGeneratedDataWarning = false;

    @Override
    public void loadObject() {
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(getDayNumber(getModel().getEndDate()));
        xAxis.setTickUnit(1);

        burndownChart.getData().clear();

        // if has any tasks
        if (getModel().getStories().stream().anyMatch(story -> story.getTasks().size() > 0)) {
            burndownChart.setVisible(true);
            // cant use clear due to an IllegalArgumentException when re-adding
            // readding done because of weird issues with graphs
            aimedBurndown = new Series<>(InternationalizationHelper.tryGet("Aimed"),
                    FXCollections.<Data<Long, Float>>observableArrayList());
            burndown = new Series<>(InternationalizationHelper.translatasert("{Burndown}      "),
                    FXCollections.<Data<Long, Float>>observableArrayList());
            burnup = new Series<>(InternationalizationHelper.tryGet("Burnup"), FXCollections.<Data<Long, Float>>observableArrayList());
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
    @SuppressWarnings("checkstyle:magicnumber")
    private void updateAimedBurndown() {
        EstimateInfo estimateInfo = getModel().getEstimationInfo();
        float initialEstimate = estimateInfo.getEstimateForDay(getModel().getStartDate());
        if (initialEstimate == 0) {
            // attempt to find the first non-zero estimate
            initialEstimate = new ArrayList<>(estimateInfo.getEstimates().entrySet()) // get all the keys
                    .stream().sorted((a, b) -> a.getKey().compareTo(b.getKey())) // sort by date
                    .filter(e -> e.getValue() != 0) // find only where estimate is not zero
                    .map(Map.Entry::getValue)   // extract estimate
                    .findFirst().orElse(0F);    // get value or default to 0
        }
        aimedBurndown.getData().add(new Data<>(0L, initialEstimate / 60));
        aimedBurndown.getData().add(new Data<>(getDayNumber(getModel().getEndDate()), 0f));
    }

    /**
     * Updates the burnup line with the data from the model.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void updateBurnUp() {
        burnup.getData().clear();

        Map<LocalDate, Float> dates = new HashMap<>();

        getModel().getStories().stream()
                .map(Story::getTasks).flatMap(Collection::stream)
                .map(Task::getEffort).flatMap(Collection::stream)
                .forEach(effort -> {
                    if (!dates.containsKey(effort.getDate())) {
                        dates.put(effort.getDate(), effort.getEffort());
                    }
                    else {
                        float current = dates.get(effort.getDate());
                        dates.put(effort.getDate(), effort.getEffort() + current);
                    }
        });

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

        orderedDates.forEach(d -> d.setYValue(d.getYValue() / 60));
        burnup.getData().addAll(orderedDates);
    }

    /**
     * Updates the burnDown line with the data from the from the model.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void updateBurnDown() {
        // get all the tasks that have been completed in order
        List<Task> completedTasks = getModel().getStories().stream()
                .map(Story::getTasks).flatMap(Collection::stream)
                .filter(t -> t.getState() == TaskState.Done)
                .sorted((o1, o2) -> o1.getCompletedDate().compareTo(o2.getCompletedDate()))
                .collect(Collectors.toList());

        // get the estimate total for incomplete tasks
        float incompleteTaskTotal = getModel().getStories().stream()
                .map(Story::getTasks).flatMap(Collection::stream)
                .filter(t -> t.getState() != TaskState.Done)
                .map(Task::getCurrentEstimate)
                .reduce(0F, (a, b) -> a + b);

        // detect where estimations have changed so we can add spikes into the graph
        List<Map.Entry<LocalDate, Float>> estimationChange =
                new ArrayList<>(getModel().getEstimationInfo().getEstimates().entrySet()) // get all the keys
                .stream().sorted((a, b) -> a.getKey().compareTo(b.getKey())) // sort by date
                .filter(e -> e.getValue() != 0) // find only where estimate is not zero
                .collect(Collectors.toList());
        int currEstChange = estimationChange.size() - 1;

        List<Data<Long, Float>> chartData = new ArrayList<>();
        // end of graph
        chartData.add(new Data<>(
                Math.min(getDayNumber(LocalDate.now()), getDayNumber(getModel().getEndDate())),
                incompleteTaskTotal));
        float accumulator = incompleteTaskTotal;
        for (int i = completedTasks.size() - 1; i >= 0; i--) {
            Task current = completedTasks.get(i);
            if (i == completedTasks.size() - 1
                    || !completedTasks.get(i + 1).getCompletedDate().equals(current.getCompletedDate())) {
                long currentDay = getDayNumber(current.getCompletedDate());
                // flat lines where no work has been done
                if (chartData.size() > 0 && chartData.get(0).getXValue() - currentDay > 1) {
                    chartData.add(0, new Data<>(chartData.get(0).getXValue() - 1, accumulator));
                }
                chartData.add(0, new Data<>(currentDay, accumulator));
            }
            accumulator += current.getCurrentEstimate();

            // add spike in graph if required due to change in estimation
            if (currEstChange >= 0
                    && estimationChange.get(currEstChange).getKey().compareTo(current.getCompletedDate()) > 0) {
                chartData.add(0, new Data<>(getDayNumber(estimationChange.get(currEstChange).getKey()),
                        estimationChange.get(currEstChange).getValue()));
                float oldEstimate = getModel().getEstimationInfo()
                        .getEstimateForDay(estimationChange.get(currEstChange).getKey().minusDays(1));
                chartData.add(0, new Data<>(getDayNumber(estimationChange.get(currEstChange).getKey()), oldEstimate));
                accumulator += oldEstimate - estimationChange.get(currEstChange).getValue();
                currEstChange--;
            }
        }
        // add beginning of graph
        chartData.add(0, new Data<>(0L, accumulator));

        chartData.forEach(d -> d.setYValue(d.getYValue() / 60));
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

        if (PersistenceManager.getCurrent().getCurrentModel().isUsingGeneratedData() && !hasShownGeneratedDataWarning) {
            hasShownGeneratedDataWarning = true;
            GenericPopup popup = new GenericPopup();
            popup.setTitleText(InternationalizationHelper.tryGet("SampleDataWarningTitle"));
            popup.setMessageText(InternationalizationHelper.tryGet("SampleDataWarning"));
            popup.show();
        }
    }
}
