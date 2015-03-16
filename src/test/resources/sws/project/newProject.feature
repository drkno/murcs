@Manual @WIP
  Feature: Create a new project

    Scenario: Create new project from file menu
      Given I have no unsaved changes
      When I click the file menu
      And I click the new