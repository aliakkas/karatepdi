Feature: convert a date to ISO8601 date format
will receive a date field as input, convert it to the ISO8601 standard format and output it as xml tag.

  Background: Perform basic authentiction and set JSESSIONID
    * url 'http://127.0.0.1:9999'
    * def setup = callonce read('pdi-cookies.feature')
    * cookies { 'JSESSIONID': #(setup.jsessionid) }
	  * def transformer = callonce read('test-transformer-details.feature')

* print transformer

  @GetenrichedData
  Scenario: Get enriched data
    Given path 'pdi/execute'
    And params { P_TRANSFORMER: 'dateiso8601', filename: '/Users/aliakkas/apps/spring/etl/jb-acceptancetest.kjb' }
    When method get
    Then status 200
    And match $.pdi-result.result.nr_errors == '0'
    * def result = $.pdi-result.result.result-rows.row-data
	 * print result
	 * def log = $.pdi-result.result.log_text
	 * print log
	 * def data = $result[*].value-data[*]
	 * print data
	 
	 Given def row = result
	 Then print row[0]
	 * string r_1 = row[2]
	 * string r_2 = r_1.split(',')[2].replace("]}","")
	 * string r_3 = r_1.split(',')[1]
	 * print r_2,r_3
	 
	 Given def row = result
	 Then string r_1 = row[1]
	 * string r_2 = r_1.split(',')[2].replace("]}","")
	 * string r_3 = r_1.split(',')[1]
	 * print r_2,r_3	 
	 
	   * text t11 = 
	 """ 
	 SELECT id, zlmsgtype, parsestring, record_series, retention_class, record_class, record_type, aa_number, at_number
	 FROM lookup.ecomms_record_type
	 ORDER BY zlmsgtype ASC, parsestring ASC
	 """
	 * print t11