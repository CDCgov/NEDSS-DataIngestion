@parallel=false
Feature: Scenarios to test end to end flow along with Kafka validations

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

  @smoke
  Scenario Outline: Read Hl7 messages from JSON file and post it via REST API and perform Database and Kafka validations
    * def hl7Message = data
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * url apiurl
    And request modifiedData
    When method POST
    Then status 200
    * def elr_raw_id = db.readRows('select id, payload from elr_raw where id = \'' + response + '\'')
    And eval Thread.sleep(400)
     And match elr_raw_id[0].id == response
     And match elr_raw_id[0].payload == modifiedData
    # * def elr_raw_validated_id = db.readRows('select raw_message_id, id, validated_message from elr_validated where raw_message_id = \'' + response + '\'')
    # And eval Thread.sleep(400)
    # And match elr_raw_validated_id[0].raw_message_id == response
    # * def kafka_elr_validated_id =  elr_raw_validated_id[0].id
    And eval Thread.sleep(600)
    #* def elr_fhir_id = db.readRows('select raw_message_id, id from elr_fhir where raw_message_id = \'' + response + '\'')
    #* def kafka_elr_fhir_id = elr_fhir_id[0].id
    #And match elr_fhir_id[0].raw_message_id == response
     #* def topics = ['elr_raw', 'elr_validated', 'elr_duplicate', 'elr_raw_dlt', 'elr_validated_dlt']
     #* def latestRecords = kafkaConsumer.readLatestFromTopics(...topics)
     #* assert response == latestRecords['elr_raw']
    # * assert kafka_elr_validated_id == latestRecords['elr_validated']
    #* assert kafka_elr_validated_id != latestRecords['elr_validated_dlt']
    # * assert kafka_elr_validated_id != latestRecords['elr_raw_dlt']
    #* assert kafka_elr_fhir_id == latestRecords['fhir_converted']



    Examples:
      | read('data.json') |



  @regressionu
  Scenario: Transmit a bad Hl7 message and validate that data is only in ELR_RAw tables but not other tables
    Given url apiurl
    And request 'MSH|^~\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20120411070545||ORU^R01|59689|P|2.9'
    When method POST
    Then status 200
    * def reqpayload = 'MSH|^~\&|SendingApp|SendingFac|ReceivingApp|ReceivingFac|20120411070545||ORU^R01|59689|P|2.9'
    * def elr_raw_id_neg = db.readRows('select id, payload from elr_raw where id = \'' + response + '\'')
    And eval Thread.sleep(100)
    And match elr_raw_id_neg[0].id == response
    And match elr_raw_id_neg[0].payload == reqpayload
    * def elr_raw_validated_id_neg = db.readRows('select id, raw_message_id, validated_message from elr_validated where raw_message_id = \'' + response + '\'')
    And eval Thread.sleep(200)
    And match karate.sizeOf(elr_raw_validated_id_neg) == 0




  @regression
  Scenario Outline: post the same Hl7 message twice and validate the record persists in elr_duplicate topic in kafka
    * def hl7Message = data
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * url apiurl
    And request modifiedData
    * def modreq = modifiedData
    When method POST
    Then status 200
    And request modreq
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
    * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
    * header Authorization = basicAuth
    When method POST
    Then status 200
    * def finalresponse = response
    * def elr_raw_id = db.readRows('select id, payload from elr_raw where id = \'' + finalresponse + '\'')
    And eval Thread.sleep(400)
    And match elr_raw_id[0].id == finalresponse
    And match elr_raw_id[0].payload == modreq
    And eval Thread.sleep(100)
    * def elr_raw_validated_id_neg = db.readRows('select raw_message_id, validated_message from elr_validated where raw_message_id = \'' + finalresponse + '\'')
    And eval Thread.sleep(100)
    Then match karate.sizeOf(elr_raw_validated_id_neg) == 0
    #* def elr_fhir_id_neg = db.readRows('select raw_message_id from elr_fhir where raw_message_id = \'' + finalresponse + '\'')
    #And eval Thread.sleep(100)
    #And match karate.sizeOf(elr_fhir_id_neg) == 0
    * def topics = ['elr_raw', 'elr_validated', 'fhir_converted', 'elr_duplicate']
    * def latestRecords = kafkaConsumer.readLatestFromTopics(...topics)
    * assert response == latestRecords['elr_duplicate']
    * assert response == latestRecords['elr_raw']
    * assert response != latestRecords['elr_validated']
    #* assert response != latestRecords['fhir_converted']


    Examples:
      | read('dupdata.json') |

  @regression
  Scenario: post a Hl7 message and validate the record in retry topics
    * def oldlastname = 'LinkLogic'
    * def hl7Message = 'MSH|^~\\&|LinkLogic|TML|John|Doe|200905011130||ORU^R01|20161111-v25|T|2.9'
    * def modifiedData = hl7Message.replace(oldlastname, randomLastName)
    * def modreq = modifiedData
    Given url apiurl
    And request modifiedData
    When method POST
    Then status 200
    * def elr_raw_id = db.readRows('select id, payload from elr_raw where id = \'' + response + '\'')
    And eval Thread.sleep(400)
    And match elr_raw_id[0].id == response
    And match elr_raw_id[0].payload == modreq
    And eval Thread.sleep(100)
    * def elr_raw_validated_id_neg = db.readRows('select raw_message_id, validated_message from elr_validated where raw_message_id = \'' + response + '\'')
    And eval Thread.sleep(100)
    Then match karate.sizeOf(elr_raw_validated_id_neg) == 0
    * def elr_fhir_id_neg = db.readRows('select raw_message_id from elr_fhir where raw_message_id = \'' + response + '\'')
    And eval Thread.sleep(100)
    And match karate.sizeOf(elr_fhir_id_neg) == 0
    * def topics = ['elr_raw', 'elr_validated', 'fhir_converted', 'elr_duplicate', 'elr_raw_dlt', 'elr_raw_retry-0']
    * def latestRecords = kafkaConsumer.readLatestFromTopics(...topics)
    * assert response != latestRecords['elr_duplicate']
    * assert response == latestRecords['elr_raw']
    * assert response != latestRecords['elr_validated']
    * assert response != latestRecords['fhir_converted']
    * assert response == latestRecords['elr_raw_dlt']
    * assert response == latestRecords['elr_raw_retry-0']

  @nbs
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
   # * driver.screenshot()
    And click('img#id_Submit_bottom_ToolbarButtonGraphic')
    #* driver.screenshot()
    And input('#DEM104', randomFirstName )
    And input('#DEM102', randomLastName )
   # * driver.screenshot()
    #* delay(90000)
   # And click('tr:nth-child(8) input:nth-child(1)')
   # * delay(3000)
    #* driver.screenshot()
   # * def name = karate.extract(text, '<b>Legal</b><br>\\s+(.+?)<br>', 1)
   # * def expectedName = lastname + ', ' + firstname
   # * match name == expectedName
   #no changes


    Examples:
      | read('dupdata.json') |




  #@fhirvalidation
  #Scenario: post an Hl7 message that passed on validation but fails to generate a fhir conversion and validate if its present in the correct kafka topics
   # * url apiurl
    #* def message = 'MSH|^~\\&|LinkLogic|TML|John|Doe|200905011130||ORU^R01|20161111-v25|T|2.9'
    #* def modifiedData = message.replace(oldValue, randomString)
   # And request modifiedData
   # * def modreq = modifiedData
   # When method POST
   # Then status 200
    #* def elr_raw_id = db.readRows('select id, payload from elr_raw where id = \'' + response + '\'')
    #And eval Thread.sleep(300)
   # And match elr_raw_id[0].id == response
    #And match elr_raw_id[0].payload == modreq
   # And eval Thread.sleep(100)
   # * def elr_raw_validated_id = db.readRows('select raw_message_id, id, validated_message from elr_validated where raw_message_id = \'' + response + '\'')
   # And eval Thread.sleep(400)
   # * def kafka_elr_validated_id =  elr_raw_validated_id[0].id
   # And match elr_raw_validated_id[0].raw_message_id == response
   # And match elr_raw_validated_id[0].validated_message == modreq
    #And eval Thread.sleep(600)
    #* def elr_fhir_id_neg = db.readRows('select raw_message_id from elr_fhir where raw_message_id = \'' + response + '\'')
    #And eval Thread.sleep(100)
   # And match karate.sizeOf(elr_fhir_id_neg) == 0
    #* def topics = ['elr_raw', 'elr_validated', 'fhir_converted', 'elr_duplicate', 'elr_raw_dlt', 'elr_raw_retry-0','elr_validated_dlt','elr_validated_retry-0']
   # * def latestRecords = kafkaConsumer.readLatestFromTopics(...topics)
   # * assert response != latestRecords['elr_duplicate']
    #* assert response == latestRecords['elr_raw']
    #* assert response == latestRecords['elr_validated']
    #* assert response != latestRecords['fhir_converted']
   # * assert response != latestRecords['elr_raw_dlt']
    #* assert response != latestRecords['elr_raw_retry-0']
   # * assert response == latestRecords['elr_validated_dlt']
   # * assert response == latestRecords['elr_validated_retry-0']
   # add dummy comment3







