@Manual
  Feature: Parametrised Reports

    Scenario: Generate a full report
      Given I have an organisation
      When I opt to generate a report
      And I select the full organisation
      And I generate the report
      Then A report for the whole organisation is generated

    Scenario: Generate report for projects
      Given I have data about one or more projects
      When I opt to generate a report
      And I select one or more projects from the available projects
      And I generate the report
      Then A report for that project(s) is generated

    Scenario: Generate report for teams
      Given I have data about one or more teams
      When I opt to generate a report
      And I select one or more teams from the available teams
      And I generate the report
      Then A report for that team(s) is generated

    Scenario: Generate report for people
      Given I have data about some people
      When I opt to generate a report
      And I select one or more people from the available people
      And I generate the report
      Then A report for that person / people is generated