@Manual
  Feature: ScrumBoard

    Scenario: Dragging Tasks
      Given you are in the scrum board view of the sprint editor
      Then you can drag tasks between in progress, not started and done
      And this changes the task state

    Scenario: Marking story as done
      Given you are in the scrum board view of the sprint editor
      And you move all the tasks in a story to the done column
      Then you can mark the story as done

    Scenario: Add assignees to a task
      Given you are in the scrum board view of the sprint editor
      And there are stories with tasks
      Then you can assign people to the tasks

    Scenario: Adjust effort spent on a task
      Given you are in the scrum board view of the sprint editor
      And there are stories with tasks
      Then you can log effort