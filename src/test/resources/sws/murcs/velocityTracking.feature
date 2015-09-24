@Manual
Feature: Velocity Tracking

  Scenario: Display Velocities in a Chart
  Given I have selected the velocity board view
  Then the velocities of sprints are displayed in a chart

  Scenario: Incomplete Sprints
  Given I have selected the velocity board view
  And there are sprints displayed that are ongoing
  Then those values are indicated as estimates

  Scenario: Median Value
  Given: I have selected the velocity board view
  And there are more than two sprints in the chart
  Then the mean value is displayed