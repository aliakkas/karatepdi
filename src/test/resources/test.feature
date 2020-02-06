Feature: test pdi server
  Scenario: call pdi server login page
    Given url 'http://127.0.0.1:9999/login'
    When method get
    Then status 200