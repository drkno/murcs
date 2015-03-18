Feature: Team Maintenance

  Scenario: Create a Team from file menu
    Given I have selected Team from the display list type
    When I click the File menu
    And I click the new sub menu
    And I click on Team
    Then a create team popup is shown

  Scenario: Create a Team using the add button
    Given I have selected Team from the display list type
    When I click the add button
    Then a create team popup is shown

  Scenario: Remove a Team using the remove button
    Given I have selected Team from the display list type
    And I have selected a team from the display list
    When I click the remove button
    Then the team is deleted

  Scenario: Invalid credentials
    Given I am creating a new team
    And the Short Name field is blank
    When I click OK
    Then an error is displayed

  Scenario: Add a Peron to the team
    Given I have a Team
    And I have a Person
    And that Person is not already in a Team
    And I have the Team selected
    When I add the Person to the Team
    Then the Person should be added to the Team

  Scenario: Remove a Person from the team
    Given I have a Person in a Team
    And I have that Person selected
    When I click the remove button
    Then the Person is removed from the Team

  Scenario: Person is already in a Team
    Given a person is already in a Team
    When I add them to another Team
    Then A pop up should tell me that I cannot