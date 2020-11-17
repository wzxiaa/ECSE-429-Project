Feature: ID_009 Adjust task priority
As a student, I want to adjust the priority of a task, to help better manage my time.

  Background:
    Given the API server is running
      And the following priorities are registered in the system:
      | title  | description          |
      | HIGH   | High Priority Task   |
      | MEDIUM | Medium Priority Task |
      | LOW    | Low Priority Task    |
      And the following courses exist in the system
      | title   | completed | active | description                 |
      | ECSE429 | false     | true   | Software Validation         |
      | COMP360 | false     | true   | Algorithm Design            |
      | ECSE416 | true      | false  | Telecommunication           |
      | COMP202 | true      | true   | Introduction to Programming |
      And the following todos are associated with "ECSE429"
      | title                 | doneStatus | priority | description                                 |
      | Assignment1 Interview | true       | MEDIUM   | Interview some engineers                    |
      | Assignment1 Report    | false      | HIGH     | Write research paper based on the interview |
      | Project Deliverable 1 | false      | LOW      | Unit test                                   |
      | Project Deliverable 2 | false      | HIGH     | Cucumber test                               |
      And the following todos are associated with "COMP360"
      | title       | doneStatus | priority | description         |
      | Assignment1 | true       | MEDIUM   | Dynamic Programming |
      | Assignment2 | false      | HIGH     | Linear Programming  |
      And the following todos are associated with "ECSE416"
      | title | doneStatus | priority | description |
      | Lab1  | true       | LOW      | TCP         |
      | Lab2  | true       | MEDIUM   | UDP         |

  Scenario Outline: Adjust task priority from "<priority>" priority to "<updatedPriority>" priority (Normal Flow)
    Given the todo with name "<title>", status "<doneStatus>" and description "<description>" is registered in the system
      And the todo "<title>" is assigned as a "<priority>"
     When user requests to adjust the priority category of the todo with title "<title>" from "<priority>" to "<updatedPriority>"
     Then the todo "<title>" should be classified as a "<updatedPriority>" priority task
    Examples:
      | title                 | doneStatus | description              | priority | updatedPriority |
      | Assignment1 Interview | true       | Interview some engineers | MEDIUM   | LOW             |
      | Project Deliverable 1 | false      | Unit test                | LOW      | MEDIUM          |
      | Project Deliverable 2 | false      | Cucumber test            | HIGH     | MEDIUM          |
      | Lab1                  | true       | TCP                      | LOW      | MEDIUM          |
      | Assignment2           | false      | Linear Programming       | HIGH     | MEDIUM          |
      | Assignment1           | true       | Dynamic Programming      | MEDIUM   | HIGH            |

  Scenario Outline: Adjust task priority to the same priority (Alternate Flow)
    Given the todo with name "<title>", status "<doneStatus>" and description "<description>" is registered in the system
      And the todo "<title>" is assigned as a "<priority>"
     When user requests to adjust the priority category of the todo with title "<title>" from "<priority>" to "<priority>"
     Then the todo "<title>" should be classified as a "<priority>" priority task
    Examples:
      | title                 | doneStatus | description              | priority |
      | Assignment1 Interview | true       | Interview some engineers | MEDIUM   |
      | Project Deliverable 1 | false      | Unit test                | LOW      |
      | Project Deliverable 2 | false      | Cucumber test            | HIGH     |

  Scenario Outline: Adjust task priority to multiple priorities (Error Flow)
    Given the todo with name "<title>", status "<doneStatus>" and description "<description>" is registered in the system
      And the todo "<title>" is assigned as a "<priority>"
     When user requests to add a priority category of "<newPriority>" to the todo with title "<title>"
     Then an error code "<errorCode>" should be returned
    Examples:
      | title                 | doneStatus | description              | priority | newPriority | errorCode |
      | Assignment1 Interview | true       | Interview some engineers | MEDIUM   | LOW         | 400       |
      | Project Deliverable 1 | false      | Unit test                | LOW      | HIGH        | 400       |
      | Project Deliverable 2 | false      | Cucumber test            | HIGH     | MEDIUM      | 400       |

  Scenario Outline: Adjust task priority to a non-existing category (Error Flow)
    Given the todo with name "<title>", status "<doneStatus>" and description "<description>" is registered in the system
      And the todo "<title>" is assigned as a "<priority>"
     When user requests to adjust the priority category of the todo with title "<title>" from "<priority>" to "<invalidPriority>"
     Then an error code "<errorCode>" should be returned
    Examples:
      | title                 | doneStatus | description              | priority | invalidPriority | errorCode |
      | Assignment1 Interview | true       | Interview some engineers | MEDIUM   | INVALID1        | 400       |
      | Project Deliverable 1 | false      | Unit test                | LOW      | INVALID2        | 400       |
      | Project Deliverable 2 | false      | Cucumber test            | HIGH     | INVALID3        | 400       |

