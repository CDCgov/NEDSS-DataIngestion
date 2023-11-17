@parallel=true
Feature: Test the API functionality scenarios

  Background:
    * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth
    * header Content-Type = 'text/plain'


@api
  Scenario: Transmit an empty HL7 message via POST method successfully and capture the error response.
    * header msgType = 'HL7'
    * header validationActive = 'true'
    Given url apiurl
    And request ''
    When method POST
    Then status 400
    Then match response.detail == "Failed to read request"


  @api
  Scenario: Transmit a valid Hl7 message via incorrect endpoint URL and validate the error response
    * header msgType = 'HL7'
    * header validationActive = 'true'
    Given url wrongapiurl
    And request 'abdef'
    When method POST
    Then status 404
    Then match response.error == "Not Found"

  @api
  Scenario: System should not let users transmit an HL7 message with missing msgType header information
    Given url apiurl
    And request 'abdef'
    When method POST
    Then status 400
    Then match response.detail == "Required header 'msgType' is not present."

  @api
  Scenario: System should not let users transmit an HL7 message with missing validationActive header information
    * header msgType = 'HL7'
    Given url apiurl
    And request 'abdef'
    When method POST
    Then status 400
    Then match response.detail == "Required header 'validationActive' is not present."

  @api
  Scenario: System should not let users transmit an HL7 message with missing validationActive and msgType header information
    Given url apiurl
    And request 'abdef'
    When method POST
    Then status 400
    Then match response.detail == "Required header 'msgType' is not present."
    #dummycomment