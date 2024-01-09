@parallel=true
Feature: Scenarios that can be reused by other features
  Background:
    * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth


  @common
  Scenario: Allow users to create a new token
    Given url tokenurl
    When method POST
    Then status 200
    * def token = response