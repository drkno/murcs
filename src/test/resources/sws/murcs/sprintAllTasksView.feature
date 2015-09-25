@Manual
  Feature: All Tasks View

    Scenario: Switching to the All Tasks Tab
      Given you have a sprint selected
      And you select the all tasks tab
      Then the all tasks tab will load with no filters.

    Scenario: Filtering Tasks
      Given you are in the all task view
      And you change the filter
      Then the tasks will either be filtered by allocated or unallocated tasks

    Scenario: Ordering Tasks
      Given you are in the all tasks view
      And you select one of the ordering options
      Then the tasks will be ordered by the appropriate field (story grouping remains unchanged if applied)

    Scenario: Grouping Tasks by Story
      Given you are in the all task view
      And you group the tasks by their story
      Then the tasks will be grouped into titled panes of their story that are ordered by story priority

    Scenario: Removing Grouping by Story
      Given you are in the all task view
      And you are grouping by story
      And you select group by None
      Then the tasks will stop being grouped and you can see them in a list

    Scenario: Edit a Task to be Allocated while Filtering by Unallocated
      Given you are in the all task view
      And you have filtered by unallocated
      And you edit a task to be allocated
      Then the task will be removed from the view

    Scenario: Edit a Task to be Unallocated while Filtering by Allocated
      Given you are in the all task view
      And you have filtered by allocated
      And you edit a task to be unallocated
      Then the task will be removed from the view

    Scenario: Edit a Tasks State
      Given you are in the all tasks view
      And you have ordered by State
      And you edit a tasks state
      Then the task will be appropriately moved

    Scenario: Edit a Taks Name
      Given you are in the all tasks view
      And you have ordered by alphabetical
      And you edit a tasks name
      Then the task will be appropriately moved

    Scenario: Edit a Taks Estimate
      Given you are in the all tasks view
      And you have ordered by estimate
      And you edit a tasks estimate
      Then the task will be appropriately moved

    Scenario: Change Stories
      Given you have a sprint selected
      And you've already looked at the all tasks view
      Then you change the stories in the sprint
      And go to the all tasks tab
      Then the tasks will refresh
