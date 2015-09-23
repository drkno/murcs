package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sws.murcs.model.*;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;
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
        velocityChart.getStyleClass().add(".chart-bar { -fx-background-insets: 0,1,2; }");
    }

    /**
     * Loads the team into the velocity board.
     */
    protected void loadObject() {
        String velocitySeriesStyle = "";
        String meanSeriesStyle = "-fx-stroke-width: 1px;";
        String medianSeriesStyle = "";
        String pointStyle = "";
        String indicativePointStyle = "";

        velocityChart.getData().clear();
        if (team == null) {
            throw new NullPointerException("Team is null in the velocity board");
        }

        Series<String, Double> velocitySeries = new Series<>();
        velocitySeries.setName("Sprint Velocities");
        List<Sprint> sprints = PersistenceManager.getCurrent().getCurrentModel().getSprints().stream().filter(s -> team.equals(s.getTeam())).sorted((s1, s2) -> {
            if (s1.getStartDate().isEqual(s2.getStartDate())) {
                return 0;
            }
            return s1.getStartDate().isBefore(s2.getStartDate()) ? -1 : 1;
        }).collect(Collectors.toList());
        double sumVelocities = 0;
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
            sumVelocities += velocity;
            String name = sprint.getShortName();
            Data dataPoint = new Data(name, velocity);
            if (sprint.getEndDate().isAfter(LocalDate.now())) {
                dataPoint.setNode(new Circle(5, Color.GRAY));
            }
            else {
                dataPoint.setNode(new Circle(5, Color.RED));
            }
            velocitySeries.getData().add(dataPoint);
        }
        velocityChart.getData().add(velocitySeries);

        // Add the horizontal lines
        if (sprints.size() > 1) {
            String firstSprint = sprints.get(0).getShortName();
            String lastSprint = sprints.get(sprints.size() - 1).getShortName();

            // Get the mean velocity
            Series meanSeries = new Series();
            double averageVelocity = sumVelocities / sprints.size();
            meanSeries.setName("Average Velocity");
            meanSeries.getData().add(new Data(firstSprint, averageVelocity));
            meanSeries.getData().add(new Data(lastSprint, averageVelocity));
            velocityChart.getData().add(meanSeries);

            // Get the median velocity
            Series medianChart = new Series();
            List<Data<String, Double>> sortedVelocities = velocitySeries.getData().stream().sorted((o1, o2) -> Double.compare(o1.getYValue(), o2.getYValue())).collect(Collectors.toList());
            double median = sortedVelocities.get((sortedVelocities.size() - 1) / 2).getYValue();
            medianChart.setName("Median Velocity");
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
