Feature: Query incomplete HIGH priority tasks
  As a student, I query all incomplete HIGH priority tasks from all my classes, to identity my short-term goals.

  Background:
    Given the API server is running
    And the following priorities are registered in the system:
      | title  | description          | 
      | HIGH   | High Priority Task   | 
      | MEDIUM | Medium Priority Task | 
      | LOW    | Low Priority Task    |
    And the following courses exist in the system
      | title    | completed | active | description                 |
      | ECSE429  | false     | true   | Software Validation         |
      | COMP360	 | false     | true   |	Algorithm Design            |
      | ECSE416	 | true      | false  |	Telecommunication           |
      | FACC100  | false     | false  | Introduction to Engineering |
    And the following todos are associated with "ECSE429"
      | title                    | doneStatus |  priority   | description                                 |
      | Assignment1 Interview    | true       |   MEDIUM    | Interview some engineers                    |
      | Assignment1 Report       | false      |    HIGH     | Write research paper based on the interview |
      | Project Deliverable 1    | false      |    LOW      | Unit test                                   |
      | Project Deliverable 2    | true       |    HIGH     | Cucumber test                               |
    And the following todos are associated with "COMP360"
      | title               | doneStatus |  priority  |  description             |
      | Assignment1         | true       |   MEDIUM   | Dynamic Programming      |
      | Assignment2         | false      |    HIGH    | Linear Programming       |
    And the following todos are associated with "ECSE416"
      | title            | doneStatus      |  priority  |  description          |
      | Lab1             | true            |    LOW     |  TCP                  |
      | Lab2             | true            |   MEDIUM   |  UDP                  |
    And no todos are associated with 'FACC100'

  
  Scenario Outline: Query incomplete HIGH priority tasks from a course with incomplete HIGH priority tasks (Normal Flow)
    Given <title> is the title of a class in the system
    And the class with title <title> has incomplete tasks
    When the user requests the incomplete HIGH priority tasks for the course with title <title>
    Then <number> todos will be returned
    And each todo returned will be marked as done
    And each todo returned will have a HIGH priority
    Examples: 
      | title    | number | 
      | ECSE429  |   1    | 
      | COMP360  |   1    |

  Scenario Outline: Query incomplete HIGH priority tasks from a course with no incomplete HIGH priority tasks (Alternate flow)
    Given <title> is the title of a class in the system
    And the class with title <title> has no incomplete tasks
    When the user requests the incomplete HIGH priority tasks for the course with title <title>
    Then 0 todos will be returned
    Examples: 
      |   title   | 
      |  ECSE416  | 

  Scenario Outline: Query incomplete HIGH priority tasks from a course with no tasks (Alternate Flow)
    Given <title> is the title of a class in the system
    And the class with title <title> has no tasks
    When the user requests the incomplete HIGH priority tasks for the course with title <title>
    Then 0 todos will be returned
    Examples: 
      | title   | 
      | FACC100 |

  Scenario Outline: Query incomplete HIGH priority tasks from invalid course (Error Flow)
    Given <title> is not a title of a class in the system
    When the user requests the incomplete HIGH priority tasks for the course with title <title>
    Then 0 todos will be returned
    And the user will receive an error telling them that the course doesn't exist in the system
    Examples: 
      | title    | 
      | FACC310  | 
 
