@parallel=true
Feature:Before labs and providers can leverage the Data Ingestion APIs, they must first acquire an authentication token. Registered labs and
  providers can obtain this token by providing their client username and secret.

  Background:
    * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth


  @token
  Scenario: Allow users to create a new token
    Given url tokenurl
    When method POST
    Then status 200
    * def token = response

  @token
  Scenario: Do not allow users to create a new token when authorization is missing.
    * header Authorization = null
    Given url tokenurl
    When method POST
    Then status 500
    Then match response.details == "Cannot invoke \"org.springframework.security.core.Authentication.getName()\" because \"authentication\" is null"
    Then match response.message == "An internal server error occurred."
    Then match response.statusCode == 500

  @token
  Scenario: Do not allow users to create a new token with incorrect authorization credentials
    * def badAuth = karate.call('classpath:basic-auth.js', { username: 'wronguser', password: 'wrongpass' })
    * header Authorization = badAuth
    Given url tokenurl
    When method POST
    Then status 401
    Then match response.details == "Full authentication is required to access this resource"
    Then match response.message == "Unauthorized"
    Then match response.statusCode == 401