@parallel=true
Feature: Test the Authorization logic implemented on Rest API end point URL

  Background:
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * header validationActive = 'true'
    * url apiurl
    * def basicAuth = read('classpath:basic-auth.js')
    * header Authorization = basicAuth

  @auth
  Scenario: Validate if user cannot post a valid HL7 message when incorrect  password is entered
    * def configauth = { username: '#(apiusername)', password: 'dummypassword' }
      And request 'abdef'
      When method POST
      Then status 401
     Then match response.details == "Full authentication is required to access this resource"
     Then match response.message == "Unauthorized"

  @auth
  Scenario: Validate if user cannot post a valid HL7 message when incorrect username is entered
    * def configauth = { username: 'dummyusername', password: '#(apipassword)' }
    And request 'abdef'
    When method POST
    Then status 401
    Then match response.details == "Full authentication is required to access this resource"
    Then match response.message == "Unauthorized"


  @auth
  Scenario: Validate if user cannot post a valid HL7 message when incorrect password and username are entered.
    * def configauth = { username: 'dummyusername', password: 'dummypassword' }
    And request 'abdef'
    When method POST
    Then status 401
    Then match response.details == "Full authentication is required to access this resource"
    Then match response.message == "Unauthorized"

  @auth
  Scenario: Do no let users transmit an HL7 message when authorization is missing
    * header Authorization = null
    * header msgType = 'HL7'
    * header validationActive = 'true'
    Given url apiurl
    And request 'abdef'
    When method POST
    Then status 401
    Then match response.details == "Full authentication is required to access this resource"
    Then match response.message == "Unauthorized"
    Then match response.statusCode == 401