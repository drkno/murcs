@Manual
Feature: Burndown chart

  Scenario: Viewing a burndown graph
    Given I have a sprint selected
    And the sprint can be represented as a burndown
    When I select the burndown tab
    Then a burndown is generated for the current sprint

  Scenario: Cannot show a burndown
    Given I have a sprint selected
    And the sprint cannot be represented as a burndown
    When I select the burndown tab
    Then an error message is shown

  Scenario: Target line
    Given I have a sprint selected
    And the sprint can be represented as a burndown
    When I select the burndown tab
    And a burndown graph is generated
    Then a target line should be shown

  Scenario: Burndown line
    Given I have a sprint selected
    And the sprint can be represented as a burndown
    When I select the burndown tab
    And a burndown graph is generated
    Then a burndown line should be shown

  Scenario: Burnup line
    Given I have a sprint selected
    And the sprint can be represented as a burndown
    When I select the burndown tab
    And a burndown graph is generated
    Then a burnup line should be shown