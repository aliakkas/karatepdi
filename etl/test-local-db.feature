Feature: Verify database schema changes

  Background: Perform basic authentiction and set JSESSIONID
    * url 'http://127.0.0.1:9999'
    * def setup = callonce read('pdi-cookies.feature')
    * print setup.jsessionid
    * cookies { 'JSESSIONID': #(setup.jsessionid) }


  @RunQuery
  Scenario: Get proccess instances details
  Run jb-test-execute-sql.kjb
    Given path 'pdi/execute'
    And params { P_SQL_QUERY:'select count(*) from expenses',P_REPORT_TRANS_NAME: 'tr-query-local-db', filename: '/Users/aliakkas/apps/spring/etl/jb-test-query-db.kjb' }
    When method get
    * print response
    Then status 200
    And match $.pdi-result.result.nr_errors == '0'
