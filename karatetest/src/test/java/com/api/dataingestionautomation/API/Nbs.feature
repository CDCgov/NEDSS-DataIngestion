@parallel=false

Feature: test end to end flow from posting Hl7 message to validating in NBS UI


  Background:
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth
    * def Thread = Java.type('java.lang.Thread')
    * def oldfirstname = 'LinkLogic'
    * def oldlastname = 'datateam'
    * def DbUtils = Java.type('com.api.dataingestionautomation.API.DbUtils')
    * def config = karate.call('classpath:karate-config.js')
    * def db = new DbUtils(config)
    * def KarateKafkaConsumer = Java.type('com.api.dataingestionautomation.API.KarateKafkaConsumer')
    * def bootstrapServers = karate.properties['test.bootstrapServers']
    * def groupId = karate.properties['test.groupId']
    * def kafkaConsumer = new KarateKafkaConsumer(bootstrapServers, groupId)
    * configure driver = { type: 'chrome' }
    * def FakerHelper = Java.type('com.api.dataingestionautomation.API.FakerHelper')
    * def randomFirstName = FakerHelper.getRandomFirstName()
    * def randomLastName = FakerHelper.getRandomLastName()

  @nbsss
  Scenario Outline: NBS flow
    * def hl7Message = data
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * url apiurl
    And request modifiedData
    When method POST
    Then status 200
    * def elr_raw_id = db.readRows('select id, payload from elr_raw where id = \'' + response + '\'')
    And eval Thread.sleep(100)
    And match elr_raw_id[0].id == response
    And eval Thread.sleep(100)
    * def elr_raw_validated_id = db.readRows('select raw_message_id, id, validated_message from elr_validated where raw_message_id = \'' + response + '\'')
    And eval Thread.sleep(200)
    * def kafka_elr_validated_id =  elr_raw_validated_id[0].id
    And match elr_raw_validated_id[0].raw_message_id == response
    And eval Thread.sleep(100)
   # * def elr_fhir_id = db.readRows('select raw_message_id, id from elr_fhir where raw_message_id = \'' + response + '\'')
   # * def kafka_elr_fhir_id = elr_fhir_id[0].id
   # And match elr_fhir_id[0].raw_message_id == response
    * def topics = ['elr_raw', 'elr_validated', 'fhir_converted', 'elr_duplicate', 'elr_raw_dlt', 'elr_validated_dlt']
    * def latestRecords = kafkaConsumer.readLatestFromTopics(...topics)
    * assert response == latestRecords['elr_raw']
    * assert kafka_elr_validated_id == latestRecords['elr_validated']
    #* assert kafka_elr_fhir_id == latestRecords['fhir_converted']
    #* assert kafka_elr_fhir_id != latestRecords['elr_duplicate']
    * assert kafka_elr_validated_id != latestRecords['elr_validated_dlt']
    * assert kafka_elr_validated_id != latestRecords['elr_raw_dlt']
    Given driver nbsurl
   # * driver.screenshot()
    And input('#id_UserName', 'state')
    #* driver.screenshot()
    And click('img#id_Submit_bottom_ToolbarButtonGraphic')
    #* driver.screenshot()
    And input('#DEM104', randomFirstName )
    And input('#DEM102', randomLastName )
    #* driver.screenshot()
    #* delay(90000)
   # And click('tr:nth-child(8) input:nth-child(1)')
   # * delay(3000)
    #* driver.screenshot()
   # * def name = karate.extract(text, '<b>Legal</b><br>\\s+(.+?)<br>', 1)
   # * def expectedName = lastname + ', ' + firstname
   # * match name == expectedName



    Examples:
      | read('dupdata.json') |


