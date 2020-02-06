@ignore
Feature: Login to PDI server

Background: Basic Authentication
	* url 'http://127.0.0.1:9999/'
	* header Authorization = callonce read('/Users/aliakkas/apps/jenkins/basic-auth.js') { username: 'admin', password: 'hello123' }

Scenario: Get JSESSIONID
  Given path 'pdi/sessionid'
  When method get
  And status 200
   
  * def jsessionid = responseCookies.JSESSIONID.value
  #end
  