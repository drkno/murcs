@Manual
Feature: Element Deletion
  Scenario: Confirm Project Deletion
    Given I have a project selected
    And I press the delete button
    Then a confirm dialog is displayed

   Scenario: Deleting a Project
     Given a confirm dialog is displayed for deleting a project
     And I confirm I want to delete
     Then the project is deleted

   Scenario: Confirm Team Deletion
     Given I have a team selected
     And I press the delete button
     Then a confirm dialog is displayed
     And all the places that the team is used are displayed

   Scenario: Deleting a Team
     Given a confirm dialog is displayed for deleting a team
     And I confirm I want to delete
     Then the team is deleted

   Scenario: Confirm Person Deletion
     Given I have a person selected
     And I press the delete button
     Then a confirm dialog is displayed
     And all the places the person is used are listed

   Scenario: Deleting a Person
     Given a confirm dialog is displayed for deleting a person
     And I confirm I want to delete
     Then the person is deleted

   Scenario: Confirm Skill Deletion
     Given I have a skill selected
     And I press the delete button
     Then a confirm dialog is displayed
     And all the places the skill is used are displayed

   Scenario: Deleting a Skill
     Given a confirm dialog is displayed for deleting a person
     And I confirm I want to delete
     Then the skill is deleted