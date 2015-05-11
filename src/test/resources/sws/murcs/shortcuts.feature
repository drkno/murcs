@Manual
  Feature: ShortCuts

    Scenario: Delete a selected item
      Given that I have selected an item
      And I press CTRL + delete
      Then a confirm dialog is displayed
      # The rest of this should be taken by care of by element deletion

    Scenario: Add a item of the type in display list
      Given that I have selected the type I want in the type list
      And I press CTRL + SHIFT + =
      Then a creation dialog of the appropriate type appears
      # Everything else should be covered by the individual maintenance feature files

    Scenario: Generate a report
      Given that I press CTRL + G
      Then a save dialog appears
      # The rest should be covered in the generate report feature file

    Scenario: Hide the display list
      Given that the display list is showing
      And I press CTRL + H
      Then the display list is hidden

    Scenario: Show the display list
      Given that the display list isn't showing
      And I press CTRL + H
      Then the display list is shown

    Scenario: Open a project
      Given that I press CTRL + O
      Then an open dialog appears

    Scenario: Save a project
      Given that I press CTRL + S
      Then a save dialog appears

    Scenario: Create a Project
      Given that I press CTRL + P
      Then a create dialog appears for Project

    Scenario: Create a Person
      Given that I press CTRL + SHIFT + P
      Then a create dialog appears for Person

    Scenario: Create a Team
      Given that I press CTRL + T
      Then a create dialog appears for a Team

    Scenario: Create a Skill
      Given that I press CTRL + SHIFT + S
      Then a create dialog appears for a Skill

    Scenario: Create a Release
      Given that I press CTRL + R
      Then a create dialog appears for a Release