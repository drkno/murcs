@Manual
Feature: Search for model objects

  Scenario: Search button
    Given I want to perform a search
    When I click on the search button
    Then the search UI appears

  Scenario: Perform a basic search
    Given the search UI is shown
    When I type a basic search query
    Then appropriate search results are shown from the entire application

  Scenario: Limit search to current list
    Given the search UI is shown
    And I want to search the current project
    When I type "!current" as part of the search query
    Then appropriate search results are shown from the current display list

  Scenario: Limit search to short name (display labels)
    Given the search UI is shown
    And I want to search by short name only
    When I type "!name" as part of the search query
    Then appropriate search results are shown where only the short name was searched

  Scenario: Select a search result
    Given the search UI is shown
    And I have search results
    When I click on a search result
    Then I should be taken to the selected item
    And the display list type should be changed if necessary

  Scenario: Wildcard search
    Given the search UI is shown
    When I type a wildcard search query
    Then appropriate search results are shown from the entire application

  Scenario: Regex search
    Given the search UI is shown
    When I type "!regex" as part of the search regex
    Then appropriate search results are shown from the entire application

  Scenario: Complex expressions "&&"
    Given the search UI is shown
    When I type "&&" as a part of the search query
    Then appropriate search results are shown from the entire application
    And search results match both sides of the "&&" symbol

  Scenario: Complex expressions "||"
    Given the search UI is shown
    When I type "||" as a part of the search query
    Then appropriate search results are shown from the entire application
    And search results march either side of the "||" symbol