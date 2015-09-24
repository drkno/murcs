package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.model.Model;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Team;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for VelocityBoard.
 */
public class VelocityBoard {

    /**
     * The chart on which to display velocities.
     */
    @FXML
    private LineChart velocityChart;

    /**
     * The team to display the velocity for.
     */
    private Team team;

    /**
     * Sets the team of this velocity board.
     * @param pTeam The team to set to.
     */
    protected void setTeam(final Team pTeam) {
        team = pTeam;
    }

    /**
     * Initialises the fxml elements.
     */
    @FXML
    private void initialize() {
        velocityChart.setCreateSymbols(false);
    }

    /**
     * Loads the team into the velocity board.
     */
    protected void loadObject() {
        velocityChart.getData().clear();
        if (team == null) {
            throw new NullPointerException("Team is null in the velocity board");
        }

        Series<String, Double> realSeries = new Series<>();
        Series indicatorySeries = new Series();
        realSeries.setName(InternationalizationHelper.tryGet("SprintVelocities"));
        indicatorySeries.setName(InternationalizationHelper.tryGet("EstimatedVelocities"));
        List<Sprint> sprints = PersistenceManager.getCurrent().getCurrentModel().getSprints().stream().filter(
                s -> team.equals(s.getTeam())).sorted((s1, s2) -> {
            if (s1.getStartDate().isEqual(s2.getStartDate())) {
                return 0;
            }
            return s1.getStartDate().isBefore(s2.getStartDate()) ? -1 : 1;
        }).collect(Collectors.toList());
        List<Double> velocities = new ArrayList<>();
        double sumVelocities = 0;
        boolean real = true;
        for (Model model : sprints) {
            Sprint sprint = (Sprint) model;
            double storyTotal = 0;
            for (Story story : sprint.getStories()) {
                int estimate = sprint.getBacklog().getEstimateType().getEstimates().indexOf(story.getEstimate()) + 1;
                if (estimate >= 0) {
                    storyTotal += estimate;
                }
            }
            double days = sprint.getEndDate().toEpochDay() - sprint.getStartDate().toEpochDay() + 1;
            Double velocity = storyTotal / days;
            velocities.add(velocity);
            sumVelocities += velocity;
            String name = sprint.getShortName();
            Data dataPoint = new Data(name, velocity);
            if (sprint.getEndDate().isBefore(LocalDate.now())) {
                realSeries.getData().add(dataPoint);
            }
            else {
                if (real && realSeries.getData().size() > 0) {
                    Data point = realSeries.getData().get(realSeries.getData().size() - 1);
                    indicatorySeries.getData().add(new Data(point.getXValue(), point.getYValue()));
                }
                real = false;
                indicatorySeries.getData().add(dataPoint);
            }
        }
        velocityChart.getData().add(realSeries);
        velocityChart.getData().add(indicatorySeries);

        // Add the horizontal lines
        if (sprints.size() > 2) {
            String firstSprint = sprints.get(0).getShortName();
            String lastSprint = sprints.get(sprints.size() - 1).getShortName();

            // Get the mean velocity
            Series meanSeries = new Series();
            double averageVelocity = sumVelocities / sprints.size();
            meanSeries.setName(InternationalizationHelper.tryGet("AverageVelocity"));
            meanSeries.getData().add(new Data(firstSprint, averageVelocity));
            meanSeries.getData().add(new Data(lastSprint, averageVelocity));
            velocityChart.getData().add(meanSeries);

            // Get the median velocity
            Series medianChart = new Series();
            List<Double> sortedVelocities = velocities.stream().sorted((o1, o2) -> Double.compare(o1, o2)).collect(Collectors.toList());
            double median = sortedVelocities.get((sortedVelocities.size() - 1) / 2);

            medianChart.setName(InternationalizationHelper.tryGet("MedianVelocity"));
            medianChart.getData().add(new Data(firstSprint, median));
            medianChart.getData().add(new Data(lastSprint, median));
            velocityChart.getData().add(medianChart);
        }
    }

    /**
     * Clears data related to this burndown chart.
     */
    public void dispose() {
        team = null;
        velocityChart.getData().clear();
    }
}
