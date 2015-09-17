package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import sws.murcs.model.*;
import sws.murcs.model.helpers.UsageHelper;

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

    protected void loadObject() {
        velocityChart.getData().clear();

        if (team == null) {
            throw new NullPointerException("Team is null in the velocity board");
        }

        //Map<String, Double> velocities = new HashMap<>();
        for (Model model : UsageHelper.findUsages(team, m -> m instanceof Sprint)) {
            Sprint sprint = (Sprint) model;
            int total = 0;
            for (Story story : sprint.getStories()) {
                int estimate = sprint.getBacklog().getEstimateType().getEstimates().indexOf(story.getEstimate());
                if (estimate >= 0) {
                    total += estimate;
                }
            }
            double days = sprint.getEndDate().toEpochDay() - sprint.getStartDate().toEpochDay() + 1;
            Double velocity = total / days;
            String name = sprint.getShortName();
            XYChart.Series series = new XYChart.Series();
            series.getData().add(new XYChart.Data(name, velocity));
            velocityChart.getData().add(series);
            //velocities.put(sprint.getShortName(), velocity);
        }


    }

    public void dispose() {
        team = null;
        velocityChart.getData().clear();
    }
}
