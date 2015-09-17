package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import sws.murcs.model.*;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
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
     * Loads the team into the velocity board.
     */
    protected void loadObject() {
        velocityChart.getData().clear();

        if (team == null) {
            throw new NullPointerException("Team is null in the velocity board");
        }

        XYChart.Series series = new XYChart.Series();
        series.setName("Sprint Velocity");

        List<Sprint> sprints = PersistenceManager.getCurrent().getCurrentModel().getSprints().stream().filter(s -> team.equals(s.getTeam())).sorted((s1, s2) -> {
            if (s1.getStartDate().isEqual(s2.getStartDate())) {
                return 0;
            }
            return s1.getStartDate().isBefore(s2.getStartDate()) ? -1 : 1;
        }).collect(Collectors.toList());

        double total = 0;
        for (Model model : sprints) {
            Sprint sprint = (Sprint) model;
            double sum = 0;
            for (Story story : sprint.getStories()) {
                int estimate = sprint.getBacklog().getEstimateType().getEstimates().indexOf(story.getEstimate()) + 1;
                if (estimate >= 0) {
                    sum += estimate;
                }
            }
            double days = sprint.getEndDate().toEpochDay() - sprint.getStartDate().toEpochDay() + 1;
            Double velocity = sum / days;
            total += velocity;
            String name = sprint.getShortName();
            series.getData().add(new XYChart.Data(name, velocity));
        }
        double averageVelocity = total / sprints.size();
        velocityChart.getData().add(series);
        if (sprints.size() > 1) {
            String firstSprint = sprints.get(0).getShortName();
            String lastSprint = sprints.get(sprints.size() - 1).getShortName();

            // Get the mean velocity
            XYChart.Series meanChart = new XYChart.Series();
            meanChart.setName("Average Velocity");
            meanChart.getData().add(new XYChart.Data(firstSprint, averageVelocity));
            meanChart.getData().add(new XYChart.Data(lastSprint, averageVelocity));
            velocityChart.getData().add(meanChart);

            // Get the median velocity
            XYChart.Series<String, Double> medianChart = new XYChart.Series<String, Double>();
            List<XYChart.Data<String, Double>> sortedVelocities = medianChart.getData().stream().sorted((o1, o2) -> Double.compare(o1.getYValue(), o2.getYValue())).collect(Collectors.toList());
            double median = sortedVelocities.get((sortedVelocities.size() - 1) / 2).getYValue();

            medianChart.setName("Median Velocity");
            meanChart.getData().add(new XYChart.Data(firstSprint, median));
            meanChart.getData().add(new XYChart.Data(lastSprint, median));

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
