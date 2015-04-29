@Manual
Feature: Status Reports

  Scenario:
    When I generate a status report
    Then a Save File Dialog appears

  Scenario:
    Given I choose to save a report
    Then the report is saved

  Scenario:
    Given I cancel saving a report
    Then the report is not saved