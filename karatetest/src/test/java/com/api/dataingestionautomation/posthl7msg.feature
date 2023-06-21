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
    * def randomString = function() { return java.util.UUID.randomUUID().toString().substring(0, 8) }
    * def DbUtils = Java.type('com.api.dataingestionautomation.API.DbUtils')
    * def config = karate.call('classpath:karate-config.js')
    * def db = new DbUtils(config)
    * def KarateKafkaConsumer = Java.type('com.api.dataingestionautomation.API.KarateKafkaConsumer')
    * def bootstrapServers = karate.properties['test.bootstrapServers']
    * def groupId = karate.properties['test.groupId']
    * def kafkaConsumer = new KarateKafkaConsumer(bootstrapServers, groupId)
    * def FakerHelper = Java.type('com.api.dataingestionautomation.API.FakerHelper')
    * def randomFirstName = FakerHelper.getRandomFirstName()
    * def randomLastName = FakerHelper.getRandomLastName()
    * configure driver = { type: 'chrome' }

  @smokee
  Scenario Outline: Read Hl7 messages from JSON file and post it via REST API.
    * def hl7Message = data
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * url apiurl
    And request modifiedData
    When method POST
    Then status 200



    Examples:
      | read('data.json') |