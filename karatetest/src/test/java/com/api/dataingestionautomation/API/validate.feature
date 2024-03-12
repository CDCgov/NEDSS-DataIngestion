@ValidateFeature
@parallel=false
Feature: Read various HL7 messages from JSON file and Post them using a REST API into DI service and NBS system.

  Background:
    * callonce read('common.feature')
    * header Authorization = 'Bearer ' + token
    * configure headers = { clientid: '#(clientid)', clientsecret: '#(clientsecret)' }
    * def responses = {}
    * def Thread = Java.type('java.lang.Thread')
    * def oldfirstname = 'LinkLogic'
    * def oldlastname = 'datateam'
    * def config = karate.call('classpath:karate-config.js')
    * def FakerHelper = Java.type('com.api.dataingestionautomation.API.FakerHelper')
    * def randomFirstName = FakerHelper.getRandomFirstName()
    * def randomLastName = FakerHelper.getRandomLastName()
    * def Thread = Java.type('java.lang.Thread')
    * def finalmessages = []

  Scenario: Read messages from JSON and post each message
    * def messages = read('data1.json')
    * def results = call read('@postMessage') messages
    * def responses = results.map(response => response.response)
    * print 'Initial UUID Responses:', responses
    * def transformedResponses = responses.map(id => { return { 'ID': id } })
    * print transformedResponses
    * def jsonResponse = karate.toJson(transformedResponses)
    * print jsonResponse
    * def ids = transformedResponses
    * print ids
    * eval Thread.sleep(130000)
    * def statusResults = call read('@checkstatus') ids


  @postMessage @ignore
  Scenario: Post HL7 messages into DI service
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * url apiurl
    * def hl7Message = data
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * print modifiedData
    * request modifiedData
    When method post
    Then status 200

  @checkstatus @ignore
  Scenario: validate the status of the posted HL7 messages into the NBS system
    * def id = ID
    * print id
    * header Authorization = 'Bearer ' + token
    * def newurl = checkstatusurl + id
    * url newurl
    * print newurl
    When method get
    Then status 200
    * def NBSresponse = response.status
    * def NBSerrorresponse = response.error_message
    * def handleFailureOrQueued = NBSresponse == 'Failure' || NBSresponse == 'QUEUED'
    * def Success = NBSresponse == 'Success'
    * def isNotSuccess = NBSerrorresponse == 'Provided UUID is not present in the database. Either provided an invalid UUID or the injected message failed validation.'
    * eval Thread.sleep(5000)
    * def errorFeatureResult = {}
    * if (isNotSuccess) errorFeatureResult = karate.call('validateerror.feature', { id: id })
    * def finalMessage = 'ID: ' + id + ', NBS Response: ' + NBSresponse
    * def printAndAppendMessage = function(text){ karate.log(text); return ', ' + text; }
    * if (isNotSuccess) finalMessage += printAndAppendMessage('Test Case failed at DI validation, Error Response: ' + errorFeatureResult.errorresponse)
    * if (handleFailureOrQueued) finalMessage += printAndAppendMessage('Test Case failed at NBS validation')
    * if (Success) finalMessage += printAndAppendMessage('Test Case Passed')
    * print finalMessage