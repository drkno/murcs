@Manual
Feature: Pair Programming

  Scenario: Pairs are identified
    Given effort has been logged on a task as a pair or group
    When I go to the team view
    Then the pair or group is shown in the pair programming section
    And the total time spent for that group or pair is shown

  Scenario: Effort is logged
    Given I create an effort entry as a pair or group
    When I press add/save
    Then the amount of effort logged is multiplied by the number of people in the pair/group

  Scenario: Reporting pair programming
    Given effort has been logged on a task as a pair or group
    When I generate a report
    Then the report contains information about the groups that have pair programmed