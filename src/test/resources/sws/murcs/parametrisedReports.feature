@Manual
  Feature: Parametrised Reports

    Scenario: Generate a full report
      Given I have an organisation
      When I opt to generate a report
      And I select the full organisation
      And I generate the report
      Then A report for the whole organisation is generated

    Scenario: Generate report for a project
      Given I have data about a specific project
      When I opt to generate a report
      And I select a project from the available projects
      And I generate the report
      Then A report for that project is generated

    Scenario: Generate report for a team
      Given I have data about a specific team
      When I opt to generate a report
      And I select a team from the available teams
      And I generate the report
      Then A report for that team is generated

    Scenario: Generate report for a person
      Given I have data about a specific person
      When I opt to generate a report
      And I select a person from the available people
      And I generate the report
      Then A report for that person is generated