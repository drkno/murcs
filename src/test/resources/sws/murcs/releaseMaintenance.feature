@WIP
Feature: Release Maintenance

  @Manual
  Scenario: Create a new Release from the file menu
    When I click the File menu
    And I click the New selection
    And I click Release
    Then The creation popup shows

  @Manual
  Scenario: Create a new release using the add button
    Given I have selected the release view from the display list type
    When I click on the add button
    Then the create new release popup shows

  Scenario: Remove release using the remove button
    Given I have selected the release view from the display list type
    And a release is selected from the list
    When I click on the remove button
    Then the release is removed from the list

  @WIP
  Scenario: Valid Project
    Given I add all required information
    When I click OK
    Then The popup goes away
    And The release is added to the list