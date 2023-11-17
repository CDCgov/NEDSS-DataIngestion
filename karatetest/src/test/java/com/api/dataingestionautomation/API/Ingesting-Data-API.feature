@parallel=true
Feature:Armed with the token, users can effortlessly ingest HL7 documents using the Data Ingestion system

  Background:
    * def oldfirstname = 'LinkLogic'
    * def oldlastname = 'datateam'
    * def config = karate.call('classpath:karate-config.js')
    * def FakerHelper = Java.type('com.api.dataingestionautomation.API.FakerHelper')
    * def randomFirstName = FakerHelper.getRandomFirstName()
    * def randomLastName = FakerHelper.getRandomLastName()
    * configure retry = { count: 3, interval: 90000 }



  @post
  Scenario Outline: Read Hl7 messages from JSON file and post it via REST API
      * header Content-Type = 'text/plain'
      * header msgType = 'HL7'
      * header validationActive = 'true'
      * call read('Token-Generation-API.feature')
      * header Authorization = 'Bearer ' + token
      * url apiurl
      * def hl7Message = data
      * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
      * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
      * request modifiedData
      When method POST
      Then status 200
      * def postid = response
      Given url checkstatusurl + postid
      * header Authorization = 'Bearer ' + token
      And retry until response.id == postid && response.status == 'Success'
      When method GET
      Then status 200

    Examples:
      | read('dupdata.json') |