Feature: Cliente CRUD operations

  Scenario: Insert a new client
    Given I have a new client with CPF
    When I insert the new client
    Then I should receive a 200 status code
