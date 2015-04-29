@Manual
  Feature: Project Allocation Maintenance

    Scenario: Add an allocation to a project
      Given I have selected the project view from the display list type
      And I have selected a start and end date
      When I select a team from the lise
      Then The team along with the start and end dates are added to the work allocation list

    Scenario: Allocations overlap
      Given There is a team already allocated
      When I allocate that team over the same period
      Then An error is displayed

    Scenario: Unschedule work allocation
      Given There is a team already allocated
      When I press the unschedule work button
      Then The allocation is removed