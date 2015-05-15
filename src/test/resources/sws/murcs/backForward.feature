@Manuel
#Basil Fawlty: Where's Sybil?
#Manuel: ¿Que?
#Basil Fawlty: Where's Sy-bil?
#Manuel: Where's... the bill?
#Basil Fawlty: No, no, not a bill! I own the place!
  Feature: Back and Forward Buttons

    Scenario: I press the back button with back history
      Given there is backwards history
      And I press the back button
      Then the GUI will change to the last selected item

    Scenario: I press the back button without back history
      Given there is no backwards history
      And I press the back button
      Then nothing will happen

    Scenario: I press the forward button with forward history
      Given there is forwards history
      And I press the forward button
      Then the GUI will change to the next item in the forward history

    Scenario: I press the forward button without forward history
      Given this is no forwards history
      And I press the forward button
      Then nothing will happen

    Scenario: I create a new model then try navigating
      Given I have created