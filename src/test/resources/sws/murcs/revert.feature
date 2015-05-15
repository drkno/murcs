@Manual
  Feature: Revert

    Scenario: Revert with no unsaved changes
      Given I have not made any changes
      Then I should not have the option to revert

    Scenario: Revert with unsaved changes
      Given I have unsaved changes
      When I Revert
      Then A dialog is shown

    Scenario: Cancel Revert
      Given I have reverted
      And A dialog is shown
      When I cancel
      Then the program state is unchanged

    Scenario: SaveAs Revert
      Given I have reverted
      And A dialog is shown
      When I SaveAs
      And I save as a new save
      Then the program state is reverted to the last saved state

    Scenario: SaveAs cancel Revert
      Given I have reverted
      And A dialog is shown
      When I SaveAs
      And I cancel the save as dialog
      Then the revert dialog is shown

