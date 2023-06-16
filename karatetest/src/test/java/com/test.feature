Feature: integrate end to end test with CI/CD pipeline


Background:
  * header Content-Type = 'text/plain'
  * header msgType = 'HL7'
  * def configauth = { username: '#(apiusername)', password: '#(apipassword)' }
  * def basicAuth = karate.call('classpath:basic-auth.js', configauth)
  * header Authorization = basicAuth
  * def Thread = Java.type('java.lang.Thread')
  * def oldfirstname = 'LinkLogic'
  * def oldlastname = 'datateam'
  * def data = read('data.csv')
  * def DbUtils = Java.type('com.api.dataingestionautomation.API.DbUtils')
  * def config = karate.call('classpath:karate-config.js')
  * def db = new DbUtils(config)
  * def KarateKafkaConsumer = Java.type('com.api.dataingestionautomation.API.KarateKafkaConsumer')
  * def bootstrapServers = karate.properties['test.bootstrapServers']
  * def groupId = karate.properties['test.groupId']
  * def kafkaConsumer = new KarateKafkaConsumer(bootstrapServers, groupId)
  * configure driver = { type: 'chrome' }
  * def generateRandomAlphabets =
"""
function(len) {
  var result = '';
  var characters = 'abcdefghijklmnopqrstuvwxyz';
  var charactersLength = characters.length;
  for ( var i = 0; i < len; i++ ) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}
"""
  * def firstname = call generateRandomAlphabets 8

  * def generateRandomAlphabetsforlastname =
"""
function(len) {
  var result = '';
  var characters = 'abcdefghijklmnopqrstuvwxyz';
  var charactersLength = characters.length;
  for ( var i = 0; i < len; i++ ) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}
"""
  * def lastname = call generateRandomAlphabetsforlastname 8



@cicd
Scenario Outline: NBS flow
  * url apiurl
  * def modifiedData = data[rowIndex].data
  * def segments = modifiedData.split('\n')  // Split by newline character
  * def mshSegment = segments[0]
  * def pidSegment = segments[1]
  * def orcSegment = segments[2]
  * def obrSegment = segments[3]
  * def obxSegment = segments[4]
  * def combinedMessage = mshSegment + '\n' + pidSegment + '\n' + orcSegment + '\n' + obrSegment + '\n' + obxSegment
  * def modifiedmsg = data[rowIndex].data.replace(oldfirstname, firstname)
  * def modifiedData = modifiedmsg.replace(oldlastname, lastname)
  And request modifiedData
  When method POST
  Then status 200
  And print response
  * def elr_raw_id = db.readRows('select id, payload from elr_raw where id = \'' + response + '\'')
  And eval Thread.sleep(400)
  And match elr_raw_id[0].id == response
  And eval Thread.sleep(100)
  * def elr_raw_validated_id = db.readRows('select raw_message_id, id, validated_message from elr_validated where raw_message_id = \'' + response + '\'')
  And eval Thread.sleep(400)
  * print elr_raw_validated_id
  * def kafka_elr_validated_id =  elr_raw_validated_id[0].id
  * print kafka_elr_validated_id
  And match elr_raw_validated_id[0].raw_message_id == response
  And eval Thread.sleep(100)
 # * def elr_fhir_id = db.readRows('select raw_message_id, id from elr_fhir where raw_message_id = \'' + response + '\'')
 # * print elr_fhir_id
 # * def kafka_elr_fhir_id = elr_fhir_id[0].id
 # And print kafka_elr_fhir_id
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
  * driver.screenshot()
  And input('#id_UserName', 'state')
  * driver.screenshot()
  And click('img#id_Submit_bottom_ToolbarButtonGraphic')
  * driver.screenshot()
  And input('#DEM104', firstname )
  * driver.screenshot()
  And input('#DEM102', lastname )
  * driver.screenshot()



  Examples:
    | rowIndex |
    | 2        |

