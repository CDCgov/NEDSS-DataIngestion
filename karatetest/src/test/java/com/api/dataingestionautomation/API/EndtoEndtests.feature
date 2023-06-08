@parallel=false
Feature: Scenarios to test end to end flow along with Kafka validations


  Background:
    * url apiurl
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * def Thread = Java.type('java.lang.Thread')
    * def oldValue = 'LinkLogic'
    * def randomString = java.util.UUID.randomUUID().toString().substring(0, 8)
    * def data = read('data.csv')
    * def DbUtils = Java.type('com.api.dataingestionautomation.API.DbUtils')
    * def config = karate.call('classpath:karate-config.js')
    * def db = new DbUtils(config)
    * def KarateKafkaConsumer = Java.type('com.api.dataingestionautomation.API.KarateKafkaConsumer')
    * def bootstrapServers = karate.properties['test.bootstrapServers']
    * def groupId = karate.properties['test.groupId']
    * def kafkaConsumer = new KarateKafkaConsumer(bootstrapServers, groupId)




  Scenario Outline: Read Hl7 messages from CSV file and post it via REST API and perform Database and Kafka validations
    * url apiurl
    * def modifiedData = data[rowIndex].data.replace(oldValue, randomString)
    And request modifiedData
    * print modifiedData
    When method POST
    Then status 200
    And print response
    * def elr_raw_id = db.readRows('select id, payload from elr_raw where id = \'' + response + '\'')
    And eval Thread.sleep(400)
    And match elr_raw_id[0].id == response
    And match elr_raw_id[0].payload == modifiedData
    And eval Thread.sleep(100)
    * def elr_raw_validated_id = db.readRows('select raw_message_id, id, validated_message from elr_validated where raw_message_id = \'' + response + '\'')
    And eval Thread.sleep(400)
    * print elr_raw_validated_id
    * def kafka_elr_validated_id =  elr_raw_validated_id[0].id
    * print kafka_elr_validated_id
    And match elr_raw_validated_id[0].raw_message_id == response
    And match elr_raw_validated_id[0].validated_message == modifiedData
    And eval Thread.sleep(600)
    * def elr_fhir_id = db.readRows('select raw_message_id, id from elr_fhir where raw_message_id = \'' + response + '\'')
    * print elr_fhir_id
    * def kafka_elr_fhir_id = elr_fhir_id[0].id
    And print kafka_elr_fhir_id
    And match elr_fhir_id[0].raw_message_id == response
    * def topics = ['elr_raw', 'elr_validated', 'fhir_converted']
    * def latestRecords = kafkaConsumer.readLatestFromTopics(...topics)
    * assert response == latestRecords['elr_raw']
    * assert kafka_elr_validated_id == latestRecords['elr_validated']
    * assert kafka_elr_fhir_id == latestRecords['fhir_converted']



    Examples:
      | rowIndex |
      | 0        |
      | 1        |
      | 2        |


  Scenario: Transmit a bad Hl7 message and validate that data is only in ELR_RAw tables but not other tables

    Given url apiurl
    And request oldValue
    When method POST
    Then status 200
    And print response
    * def elr_raw_id_neg = db.readRows('select id, payload from elr_raw where id = \'' + response + '\'')
    And eval Thread.sleep(100)
    * print elr_raw_id_neg
    And match elr_raw_id_neg[0].id == response
    And match elr_raw_id_neg[0].payload == oldValue
    * def elr_raw_validated_id_neg = db.readRows('select raw_message_id, validated_message from elr_validated where raw_message_id = \'' + response + '\'')
    And eval Thread.sleep(100)
    * print elr_raw_validated_id_neg
    Then match karate.sizeOf(elr_raw_validated_id_neg) == 0
    * def elr_fhir_id_neg = db.readRows('select raw_message_id from elr_fhir where raw_message_id = \'' + response + '\'')
    And eval Thread.sleep(100)
    And  print elr_fhir_id_neg
    And match karate.sizeOf(elr_fhir_id_neg) == 0




