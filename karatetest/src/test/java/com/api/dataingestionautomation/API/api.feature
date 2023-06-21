@parallel=true
Feature: Test API functionality scenarios

  Background:
    * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth
    * url apiurl
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * def oldValue = 'LinkLogic'
    * def randomString = java.util.UUID.randomUUID().toString().substring(0, 8)
    * def DbUtils = Java.type('com.api.dataingestionautomation.API.DbUtils')
    * def config = karate.call('classpath:karate-config.js')


@api
  Scenario: Transmit an empty HL7 message via POST method successfully and capture the error response
    Given request ''
    When method POST
    Then status 400
    And match response contains { error: 'Bad Request' }

  @api
  Scenario: Transmit a valid Hl7 message via incorrect endpoint URL and validate the error response
    Given url wrongapiurl
    And request 'abdef'
    When method POST
    Then status 404
    And match response contains { error: 'Not Found' }
    # added comment for demo
    # added comment for demo 2




