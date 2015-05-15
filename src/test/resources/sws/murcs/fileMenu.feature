@Manual
  Feature: Actions on the file menu

    Scenario: Create a new model with unsaved changes
      Given I have unsaved changes
      When I create a new organisation
      Then A dialog is shown

    Scenario: Create a new model with saved changes
      Given I have saved changes
      When I create a new organisation
      Then the new model is created

    Scenario: SaveAs
      When I SaveAs
      Then I save as a new file

    Scenario: save an existing model
      When I save
      Then the model is saved

    Scenario: save a new model
      When I save
      Then the saveAs dialog is shown