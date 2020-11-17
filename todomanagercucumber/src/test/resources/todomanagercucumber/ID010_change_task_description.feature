Feature: ID_010 Change a task description
As a student, I want to change a task description, to better represent the work to do.

  Background:
    Given the API server is running
      And the following todos exist in the system
      | title                  | doneStatus | description                                          |
      | Finish feature files   | false      | Create 3 feature files (including this one)          |
      | Complete ECSE homework | true       | Need to write up a lab and finish challenge question |
      | Finish this semester   | false      | Can we finish this?                                  |

  Scenario Outline: Change description for existing todo (Normal Flow)
    Given <selectedTitle> is the title of a todo registered on the system
     When the user requests to set the description of the todo with title "<selectedTitle>" to "<newDescription>"
     Then the description of the todo with title "<selectedTitle>" will be changed to "<newDescription>"
      And the user will be given the updated version of the todo where the description is <newDescription>
    Examples:
      | selectedTitle          | newDescription                             |
      | Finish feature files   | Create 2 feature files                     |
      | Complete ECSE homework | Finish quiz                                |
      | Finish this semester   | This to do exists to test different titles |

  Scenario Outline: Remove description from existing todo (Alternate Flow)
    Given <selectedTitle> is the title of a todo registered on the system
     When the user requests to remove the description of the todo with title <selectedTitle>
     Then the description of the todo with title <selectedTitle> will be removed
      And the user will be given the update version of the todo with an empty description
    Examples:
      | selectedTitle          |
      | Finish feature files   |
      | Complete ECSE homework |
      | Finish this semester   |

  Scenario Outline: Change description for non-existent todo (Error Flow)
    Given <selectedTitle> is not a title of a todo registered on the system
     When the user requests to change the description of the todo with title <selectedTitle>
     Then no todo on the system will be modified
      And the user will receive an error message that the specified todo does not exist
    Examples:
      | selectedTitle        |
      | Fake title!          |
      |                      |
      | finish feature files |

