@Manual
Feature: Status Reports

  Scenario: Generating Reports
    When I generate a status report
    Then a Save File Dialog appears

  Scenario: Saving Reports
    Given I choose to save a report
    Then the report is saved

  Scenario: Cancelling Saves
    Given I cancel saving a report
    Then the report is not saved