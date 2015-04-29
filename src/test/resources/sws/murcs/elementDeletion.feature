@NOW
@ElementDeletion
Feature: Element Deletion

  Scenario: Deleting a Team
    Given I have a team selected
    And I press the delete button
    Then a confirm dialog is displayed
    And all the places that the object is used are displayed
    And I confirm I want to delete
    Then the model is deleted
    And "Team" deletion can be undone

   Scenario: Deleting a Project
     Given I have a project selected
     And I press the delete button
     Then a confirm dialog is displayed
     And all the places that the object is used are displayed
     And I confirm I want to delete
     Then the model is deleted
     And "Project" deletion can be undone

   Scenario: Deleting a Person
     Given I have a person selected
     And I press the delete button
     Then a confirm dialog is displayed
     And all the places that the object is used are displayed
     And I confirm I want to delete
     Then the model is deleted
     And "People" deletion can be undone

   Scenario: Deleting a Skill
     Given I have a skill selected
     And I press the delete button
     Then a confirm dialog is displayed
     And all the places that the object is used are displayed
     And I confirm I want to delete
     Then the model is deleted
     And "Skills" deletion can be undone

   Scenario: Deleting a Release
     Given I have a release selected
     And I press the delete button
     Then a confirm dialog is displayed
     And all the places that the object is used are displayed
     And I confirm I want to delete
     Then the model is deleted
     And "Release" deletion can be undone