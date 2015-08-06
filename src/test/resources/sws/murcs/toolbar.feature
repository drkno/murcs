@Manual
Feature: Toolbar

  Background:
    Given you are in a window with a toolbar

  Scenario: Navigate Back
    Given you can go back
    And you click on the back button in the toolbar
    Then you will be navigated back to the previous object in your history

  Scenario: Navigate Back no Back Stack
    Given you can't go back
    Then the back button is disabled
    And you click on the back button in the toolbar
    Then nothing happens
#    This one may need to be adjusted. (For instance display a tooltip when this happens)

  Scenario: Forward
    Given you can go forward
    And you click on the forward button in the toolbar
    Then you will be navigated forward to the next object in your history

  Scenario: Navigate Forward no Forward Stack
    Given you can't go forward
    Then the back forward button is disabled
    And you click on the back button in the toolbar
    Then nothing happens

  Scenario: Revert
    Given you have made changes
    And you click on the revert button in the toolbar
    Then a window appears to make sure you want to revert
    When you choose cancel
    Then nothing changes
    When you choose ok
    Then your changes are reverted to the loading of the file
    When you choose save
    Then you save the project
    Then your changes are reverted to the loading of the file

  Scenario: Undo
    Given you have made changes
    And you click on the undo button in the toolbar
    Then the last thing changed is undone

  Scenario: Redo
    Given you have undone changes
    And you click on the redo button in the toolbar
    Then the last thing you undid is redone

  Scenario: Add
    When you click the button
    Then a new creation window appears for the model type currently selected in the display list
    When you select an item from the drop down menu
    Then a creation window of the corresponding type appears

  Scenario: Remove
    Given you have an item selected
    And you click the remove button
    Then a warning window will appear
    When you click yes
    Then the item is removed
    When you click no
    Then nothing happens

  Scenario: Initial Save
    Given you have made changes
    And haven't already made an initial save
    And you click the save button in the toolbar
    Then a window appears to prompt you for the save location

  Scenario: Save
    Given you have made changes
    And have already initially saved the project
    And you click the save button in the toolbar
    Then the project is saved

  Scenario: Save As
    Given you click the save as button in the toolbar
    Then a window appears to prompt you for the save location

  Scenario: Open
    Given you click the open button in the toolbar
    Then a window appears to prompt you for the location of the saved project

  Scenario: Generate Report
    Given you click the generate report button in the toolbar
    Then a window appears to guide you through generating a report

  Scenario: Report Problem
    Given you click the report a problem button
    Then a window appears to guide you through submitting a problem