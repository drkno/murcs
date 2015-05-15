@Manual
Feature: Hyperlinks

  Scenario: View a team that is allocated to a project
    Given I have project selected from the side bar
    And There is a team allocated to the project
    When I click the name of the team
    Then The window updates to displaying the details of that team

  Scenario: View a person that is a member of a team
    Given I have a team selected from the side bar
    And There is a person in the team
    When I click the name of the person
    Then The window updates to displaying the details of that person

  Scenario: View a skill that is associated with a person
    Given I have a person selected from the side bar
    And That person has a skill associated with him
    When I click on the skills short name
    Then The window updates to displaying the details of that skill