# new feature
# Tags: optional

Feature: Print properties variables

  Background: Perform basic authentiction and set JSESSIONID
    * url 'http://127.0.0.1:9999'
    * def setup = callonce read('pdi-cookies.feature')
    * print setup.jsessionid
    * cookies { 'JSESSIONID': #(setup.jsessionid) }

  @PrintVariables
  Scenario: Reset processes - using 'PROC_INSERT_UPDATE' stored procedure
    Given path 'pdi/execute'
    And params { filename: '/Users/aliakkas/apps/spring/etl/capturing_rows.ktr'}
    When method get
    * print response
    Then status 200
    And match $.pdi-result.result.nr_errors == '0'
