@parallel=false
Feature: Post hl7 messages from json file

  Background:
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth
    * def Thread = Java.type('java.lang.Thread')
    * def oldfirstname = 'LinkLogic'
    * def oldlastname = 'datateam'
    * def config = karate.call('classpath:karate-config.js')
    * def FakerHelper = Java.type('com.api.dataingestionautomation.API.FakerHelper')
    * def randomFirstName = FakerHelper.getRandomFirstName()
    * def randomLastName = FakerHelper.getRandomLastName()


  @smoke
  Scenario Outline: Read Hl7 messages from JSON file and post it via REST API url
    * def hl7Message = data
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * url apiurl
    And request modifiedData
    When method POST
    Then status 200

    Examples:
      | read('data.json') |