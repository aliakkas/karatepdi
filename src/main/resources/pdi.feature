Feature: testing pdi scripts
#Usage: java -jar ~/apps/jenkins/karate.jar ~/apps/jenkins/pdi.feature

	Background: Perform basic authentiction and set JSESSIONID
		* url 'http://127.0.0.1:9999'
		* def setup = callonce read('pdi-cookies.feature')
		* print setup.jsessionid
		* cookies { 'JSESSIONID': #(setup.jsessionid) }

# process XML
Scenario: run test-release-xml.ktr
	Given path 'pdi/execute'
	And params { filename: '/Users/aliakkas/apps/hv/aa/test-release-xml.ktr' }
	When method get
	Then status 200

# test database
Scenario: run test-release-xml.ktr
	Given path 'pdi/execute'
	And params { filename: '/Users/aliakkas/apps/jenkins/pdi-sdk-plugins/kettle-sdk-embedding-samples/etl/test-db.ktr' }
	When method get
	Then status 200

# processing JSON	
Scenario: run tr-json-xml.ktr
	Given path 'pdi/execute'
	And params { filename: '/Users/aliakkas/apps/hv/aa/tr-json-xml.ktr' }
	When method get
	Then status 200

# sanity check	
Scenario: run jb-test-process-stats.kjb
	Given path 'pdi/execute'
	And params { filename: '/Users/aliakkas/apps/hv/UMP-v2.1.0/mmt/tests/jb-test-process-stats.kjb' }
	When method get
	Then status 200
