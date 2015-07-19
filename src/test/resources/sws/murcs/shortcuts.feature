@Manual
  Feature: Shortcuts

    Scenario: Delete a selected item
      Given that I have selected an item
      And I press delete item shortcut
      Then a confirm dialog is displayed
      # The rest of this should be taken by care of by element deletion

    Scenario: Add a item of the type in display list
      Given that I have selected the type I want in the type list
      And I press new item shortcut
      Then a creation dialog of the appropriate type appears
      # Everything else should be covered by the individual maintenance feature files

    Scenario: Generate a report
      Given that I press generate report shortcut
      Then a save dialog appears
      # The rest should be covered in the generate report feature file

    Scenario: Hide the display list
      Given that the display list is showing
      And I press hide/show shortcut
      Then the display list is hidden

    Scenario: Show the display list
      Given that the display list isn't showing
      And I press hide/show shortcut
      Then the display list is shown

    Scenario: Open a Project
      Given that I press the open Project shortcut
      Then an open dialog appears

    Scenario: Save a Project
      Given that I press the save Project shortcut
      Then a save dialog appears

    Scenario: Create a Project
      Given that I press the new Project shortcut
      Then a create dialog appears for Project

    Scenario: Create a Team
      Given that I press the new Team shortcut
      Then a create dialog appears for a Team

    Scenario: Create a Person
      Given that I press the new Person shortcut
      Then a create dialog appears for Person

    Scenario: Create a Skill
      Given that I press the new Skill shortcut
      Then a create dialog appears for a Skill

    Scenario: Create a Release
      Given that I press the new Release shortcut
      Then a create dialog appears for a Release

    Scenario: Create a Backlog
      Given that I press the new Backlog shortcut
      Then a create dialog appears for a Release

    Scenario: Create a Story
      Given that I press the new Story shortcut
      Then a create dialog appears for a Release
