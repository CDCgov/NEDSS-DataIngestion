@parallel=true
Feature: Test the Authorization logic implemented on Rest API end point URL

  Background:
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * url apiurl
    * def config = karate.call('classpath:karate-config.js')


    @auth
  Scenario: Validate if user cannot post a valid HL7 message when incorrect username and password credentials are entered for basic authorization of API
      * def configauth = { username: 'negtest', password: 'negtest'}
      * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
      * header Authorization = basicAuth
      And request 'abdef'
      When method POST
      Then status 401

  @auth
  Scenario: Validate if user cannot post a valid HL7 message when incorrect username is entered for basic authorization of API
    * def configauth = { username: 'negtest', password: 'negtest'}
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth
    And request 'abdef'
    When method POST
    Then status 401

  @auth
  Scenario: Validate if user cannot post a valid HL7 message when incorrect password is entered for basic authorization of API
    * def configauth = { username: 'negtest', password: 'negtest'}
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth
    And request 'abdef'
    When method POST
    Then status 401

  @auth
  Scenario: Validate if user cannot post a valid HL7 message when no authentication is selected in the API.
    And request 'abdef'
    When method POST
    Then status 401







