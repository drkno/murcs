package sws.murcs.controller.editor;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import sws.murcs.model.Effort;
import sws.murcs.model.EstimateInfo;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;

/**
 * A controller for the burndown tab on sprints
 */
public class BurndownController extends GenericEditor<Sprint> {
    /**
     * The chart representing the burndown.
     */
    @FXML
    private LineChart burndownChart;

    private XYChart.Series<Long, Float> aimedBurndown;
    private XYChart.Series<Long, Float> burndown;
    private XYChart.Series<Long, Float> burnup;

    @Override
    public void loadObject() {
        NumberAxis xAxis = (NumberAxis)burndownChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(1);
        xAxis.setUpperBound(getDayNumber(getModel().getEndDate()));
        xAxis.setTickUnit(1);

        updateAimedBurndown();
        updateBurnUp();
        updateBurnDown();
    }

    private long getDayNumber(LocalDate date) {
        return date.toEpochDay() - getModel().getStartDate().toEpochDay() + 1;
    }

    private void updateAimedBurndown() {
        aimedBurndown.getData().clear();

        EstimateInfo estimateInfo = getModel().getEstimationInfo();

        aimedBurndown.getData().add(new XYChart.Data<>(0L, estimateInfo.getEstimateForDay(getModel().getStartDate())));
        aimedBurndown.getData().add(new XYChart.Data<>(getDayNumber(getModel().getEndDate()), 0f));
    }

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

    private void updateBurnDown() {

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
