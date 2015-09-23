# Basil Fawlty: [about Sybil's laugh] Sounds like somebody machine-gunning a seal.
@Manual
Feature: Hyperlinks

  Scenario: View a team that is allocated to a project
    Given I am viewing a project
    When I click on a work allocation
    Then The window updates to displaying the details of that team

  Scenario: Opening Hyperlinks in new tabs
    When I CTRL + Click on a hyperlink
    Then it should open in a new tab.

  Scenario: View a person that is a member of a team
    Given I am viewing a team
    When I click on the name of a person
    Then The window updates to displaying the details of that person

  Scenario: View a skill that is associated with a person
    Given I am viewing a person
    When I click on a skills short name
    Then The window updates to displaying the details of that skill