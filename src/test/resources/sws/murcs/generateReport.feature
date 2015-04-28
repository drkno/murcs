@Manual
Feature: Status Reports

  Scenario:
    When I click the File menu
    And I click "Generate Report" from the menu
    Then A Save File Dialog appears

  Scenario:
    Given The Save File Dialog appears
    When I choose an appropriate save location
    And I specify a filename
    And I click the "Save" button in the dialog
    Then The report is saved to the given file location

  Scenario:
    Given The Save File Dialog appears
    When I click the cancel button
    Then Generate Report operation is canceled smoothly
