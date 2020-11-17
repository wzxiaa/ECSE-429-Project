Feature: ID_005 Create course to do list

As a student, I want to create a to do list for a new class I am taking, so I can manage course work.

  Background:
    Given the API server is running

  Scenario Outline: Create a new course to do list with title (Normal Flow)
    Given <title> is the title of the new course
     When the user post the request
     Then the new course with <title> will be created
      And the newly created course will be returned to the user

    Examples:
      | title   |
      | ECSE429 |
      | COMP512 |
      | GEOG203 |

  Scenario Outline: Create a new course to do list with title, active status and description (Alternative Flow)
    Given <title> is the title of the new course
      And <isActive> is the active status of the new course
      And <description> is the description of the new course
     When the user post the request
     Then the new course with <title>,<isActive>,<description> will be created
      And the newly created course will be returned to the user

    Examples:
      | title   | isActive | description         |
      | ECSE429 | true     | Software Validation |
      | COMP512 | false    | Distributed System  |
      | ECSE416 | true     | Telecommunication   |

  Scenario Outline: Create a new course to do list with id specified (Error Flow)
    Given <id> is the id of the new course
      And <title> is the title of the new course
     When the user post the request
     Then no new course will be created
      And the user will receive an error message that creating with id is not allowed

    Examples:
      | id | title   |
      | 0  | ECSE429 |
      | 1  | COMP512 |
      | 2  | ECSE416 |
