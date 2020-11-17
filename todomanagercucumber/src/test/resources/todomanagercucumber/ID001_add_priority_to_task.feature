Feature: ID_001 Add priority to task

As a student, I categorize tasks as HIGH, MEDIUM or LOW priority, so I can better manage my time.

  Background:
    Given the API server is running
    And the following categories exist in the system
      | title  | description          |
      | HIGH   | High Priority Task   |
      | MEDIUM | Medium Priority Task |
      | LOW    | Low Priority Task    |

  Scenario Outline: Categorize Task as specified priority (Normal Flow)
    Given the todo with name "<todoTitle>", done status "<todoDoneStatus>" and description "<todoDescription>" is registered in the system
    When user requests to categorize todo with title "<todoTitle>" as "<priorityToAssign>" priority
    Then the "<todoTitle>" should be classified as a "<priorityToAssign>" priority task

    Examples:
      | todoTitle          | todoDoneStatus | todoDescription       | priorityToAssign |
      | ECSE 429 Project B | false          | ECSE 429 User Stories | HIGH             |
      | ECSE 420 Lab 3     | false          | ECSE 420 Programming  | MEDIUM           |

  Scenario Outline: Change Task priority to another priority (Alternate Flow)
    Given the todo with name "<todoTitle>", done status "<todoDoneStatus>" and description "<todoDescription>" is registered in the system
    And the todo "<todoTitle>" is assigned as a "<oldPriority>" priority task
    When user requests to remove "<oldPriority>" priority categorization from "<todoTitle>"
    And user requests to categorize todo with title "<todoTitle>" as "<newPriority>" priority
    Then the "<todoTitle>" should be classified as a "<newPriority>" priority task

    Examples:
      | todoTitle          | todoDoneStatus | todoDescription       | oldPriority | newPriority |
      | ECSE 429 Project B | false          | ECSE 429 User Stories | HIGH        | MEDIUM      |
      | ECSE 420 Lab 3     | false          | ECSE 420 Programming  | MEDIUM      | LOW         |

  Scenario Outline: Categorize non-existing task as priority (Error Flow)
    Given the following todos exist in the system
      | title                 | doneStatus | description                  |
      | ECSE 420 Lab 3        | false      | Parallel Computing           |
    When user requests to categorize todo with title "<todoTitle>" as "<priorityToAssign>" priority
    Then the system should output an error message

    Examples:
      | todoTitle          | todoDoneStatus | todoDescription       | priorityToAssign |
      | ECSE 429 Project B | false          | ECSE 429 User Stories | HIGH             |
      | ECSE 444 Project 1 | false          | ECSE 444 ADC          | MEDIUM           |
