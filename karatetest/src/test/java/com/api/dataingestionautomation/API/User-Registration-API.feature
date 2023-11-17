@parallel=true
Feature: STLT administrators wield the power to register new lab and provider admin accounts. This crucial registration API call necessitates
  the input of the client username and client secret alongside the admin's username and password, ensuring a secure and straightforward onboarding process for authorized personnel.



  Background:
    * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth
    * header Content-Type = 'application/json'
    * def FakerHelper = Java.type('com.api.dataingestionautomation.API.FakerHelper')
    * def randomFirstName = 'test10' + FakerHelper.getRandomFirstName()
    * url registrationapiurl



  @registration
  Scenario: Allow users to create/register a new client username
    Given request { username: '#(randomFirstName)', password: '%$%^GFDSAGD' }
    When method POST
    Then status 200
    Then match response == "User Created Successfully."
    * def newuser = randomFirstName

  @registration
  Scenario: Do not let users create a new client user when the same username already exists
    Given request { username: '#(newuser)', password: '%$%^GFDSAGD' }
    When method POST
    Then status 200
    Then match response == "User already exists.Please choose another."


  @registration
  Scenario: Do not allow users to create a new user with username less than 6 characters
    Given request { username: 'test', password: 'testpwd1' }
    When method POST
    Then status 200
    Then match response == "The username must be atleast six characters in length."

  @registration
  Scenario: Do not allow users to create a new user with password less than 8 characters
    Given request { username: '#(randomFirstName)', password: 'test1' }
    When method POST
    Then status 200
    Then match response == "The password must be atleast eight characters in length."



  @registration
  Scenario: Do not allow users to create a new user with missing username
    Given request { username: '', password: 'testpassword' }
    When method POST
    Then status 200
    Then match response == "Username and/or password are required."

  @registration
  Scenario: Do not allow users to create a new user with missing password
    Given request { username: '#(randomFirstName)', password: '' }
    When method POST
    Then status 200
    Then match response == "Username and/or password are required."

  @registration
  Scenario: Do not allow users to create a new user with missing password and username
    Given request { username: '', password: '' }
    When method POST
    Then status 200
    Then match response == "Username and/or password are required."

  @registration
  Scenario: Do not allow users to create a new user with password that has just 8 spaces
    Given request { username: '#(randomFirstName)', password: '        ' }
    When method POST
    Then status 200
    Then match response == "The password must be atleast eight characters in length."

  @registration
  Scenario: Do not allow users to create a new user with empty request
    Given request ''
    When method POST
    Then status 400
    Then match response.detail == "Failed to read request"
    Then match response.title == "Bad Request"
    Then match response.type == "about:blank"


  @registration
  Scenario: Do not allow users to create a new user with empty authorization
    * header Authorization = null
    Given request ''
    When method POST
    Then status 401
    Then match response.details == "Full authentication is required to access this resource"
    Then match response.message == "Unauthorized"
    Then match response.statusCode == 401