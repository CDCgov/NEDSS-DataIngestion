@parallel=true
Feature: Verify if DI system can handle certain validation issues

  Background:
    * def oldfirstname = 'LinkLogic'
    * def oldlastname = 'datateam'
    * def FakerHelper = Java.type('com.api.dataingestionautomation.API.FakerHelper')
    * def randomFirstName = FakerHelper.getRandomFirstName()
    * def randomLastName = FakerHelper.getRandomLastName()
    * configure retry = { count: 3, interval: 2000 }
    * call read('Token-Generation-API.feature')
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * header validationActive = 'true'
    * header Authorization = 'Bearer ' + token
    * url apiurl


  @error
  Scenario: Post an HL7 message with invalid version and verify if validation fails in DI system.Capture the error stack trace as well.
    * def hl7Message = "MSH|^~\\&|LABCORP-CORP^OID^ISO|LABCORP^34D0655059^CLIA|SCDOH^OID^ISO|SC^OID^ISO|201204200100||ORU^R01^ORU_R01|20120605034370001A|D|2.2|||||||||PHLabReport-NoAck^ELR_Receiver^2.16.840.1.113883.9.11^ISO\nSFT|Mirth Corp.|2.0|Mirth Connect|789654||20110101\nPID|1||08660205112^^^^PI^NE_CLINIC&24D1040593||datateam^LinkLogic|||||||||||||||||||||||||\nORC|RE||||||||||||||||||||HUFF MEDICAL CENTER|1212 DOGGIE TRAIL.^SUITE 500^ATLANTA^GA^30004|^^^^^770^1234567\nOBR|1||06050205112A^namespace^OID^ISO|699-9^ORGANISM COUNT^LN|||200603241455|||||||||||||||201205091533|||F\nOBX|1|ST|11475-1^MICROORGANISM IDENTIFIED^LN||||||||F||||||||201205301200\nSPM|1|^08660205112&namespace&OID&ISO||UNK^Unknown^NullFlavor|"
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * request modifiedData
    When method POST
    Then status 200
    * def postid = response
    Given url '' + postid
    * header Authorization = 'Bearer ' + token
    And retry until response.errorMessageId == postid
    When method GET
    Then status 200
    Then match response.errorStackTraceShort == "DiHL7Exception: Unsupported HL7 Version, please only specify either 2.3.1 or 2.5.1. Provided version is: 		2.2"
    Then match response.dltOccurrence == 1
    Then match response.createdBy == 'elr_raw_dlt'
    Then match response.updatedBy == 'elr_raw_dlt'
    Then match response.updatedOn == null
    Then match response.dltStatus == 'ERROR'
    Then match response.errorMessageSource == 'elr_raw'
    Then match response.message == modifiedData

  @error
  Scenario: Post an exact same HL7 message more than once and verify if validation fails in DI system. Capture the error stack trace
    * def hl7Message = "MSH|^~\\&|LABCORP-CORP^OID^ISO|LABCORP^34D0655059^CLIA|SCDOH^OID^ISO|SC^OID^ISO|201204200100||ORU^R01^ORU_R01|20120605034370001A|D|2.3.1|||||||||PHLabReport-NoAck^ELR_Receiver^2.16.840.1.113883.9.11^ISO\nPID|1||08660205112^^^^PI^NE_CLINIC&24D1040593||datateam^LinkLogic|||||||||||||||||||||||||\nORC|RE||||||||||||||||||||HUFF MEDICAL CENTER|1212 DOGGIE TRAIL.^SUITE 500^ATLANTA^GA^30004|^^^^^770^1234567\nOBR|1||06050205112A^namespace^OID^ISO|699-9^ORGANISM COUNT^LN|||200603241455|||||||||||||||201205091533|||F\nOBX|1|ST|11475-1^MICROORGANISM IDENTIFIED^LN||||||||F||||||||201205301200\nSPM|1|^08660205112&namespace&OID&ISO||UNK^Unknown^NullFlavor|"
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * request modifiedData
    When method POST
    Then status 200
    And request modifiedData
    * header Content-Type = 'text/plain'
    * header msgType = 'HL7'
    * header validationActive = 'true'
    * header Authorization = 'Bearer ' + token
    When method POST
    Then status 200
    * def dupid = response
    Given url '' + dupid
    * header Authorization = 'Bearer ' + token
    And retry until response.errorMessageId == dupid
    When method GET
    Then status 200
    Then match response.errorStackTraceShort == "DuplicateHL7FileFoundException: HL7 document     already exists in the database. Please check elr_raw table for the failed document. Record Id: dupid"
    Then match response.dltOccurrence == 1
    Then match response.createdBy == 'elr_raw_dlt'
    Then match response.updatedBy == 'elr_raw_dlt'
    Then match response.updatedOn == null
    Then match response.dltStatus == 'ERROR'
    Then match response.errorMessageSource == 'elr_raw'
    Then match response.message == modifiedData

  @error
  Scenario: Post an incorrect formatted HL7 message and verify if validation fails in DI system.
    * request "abcdef"
    When method POST
    Then status 200
    * def postiddd = response
    Given url ' + postiddd
    * header Authorization = 'Bearer ' + token
    And retry until response.errorMessageId == postiddd
    When method GET
    Then status 200
    Then match response.errorStackTraceShort == "DiHL7Exception: Invalid Message Determine encoding for message. The following is the first 50 chars of the message for reference, although this may not be where the issue is: abcdef"
    Then match response.dltOccurrence == 1
    Then match response.createdBy == 'elr_raw_dlt'
    Then match response.updatedBy == 'elr_raw_dlt'
    Then match response.updatedOn == null
    Then match response.dltStatus == 'ERROR'
    Then match response.errorMessageSource == 'elr_raw'
    Then match response.message == "abcdef"

  @error
  Scenario: Post an an  HL7 message with invalid datetime and verify if validation fails in DI system.
    * def hl7Message = "FHS|^~\\&|ELR|Centennial Hills Hospital Medical Center|NV|NVDOH|20150225||FHSeg|Generated by ELR Route|FHSAppGen20150225|Application Generate\nBHS|^~\\&|UHS|Centennial Hills Hospital Medical Center|NV|NVDOH|20150225||BHSeg|Application Generated|BHSAppGen20150225|P\nMSH|^~\\&|UHS|Centennial Hills Hospital Medical Center^TBD^CLIA|NV|NVDOH|20150224114200-0400000||ORU^R01|2015022411420626264_1001_6|P|2.3.1|||||USA\nPID|1||0000000^^^Summerlin emergency Medical Center&TBD&CLIA^MR^Summerlin Hospital Medical Center&TBD&CLIA~19116381^^^Summerlin Hospital Medical Center&TBD&CLIA^PI^Summerlin Hospital Medical Center&TBD&CLIA~XxxXx3681^^^SSN&TBD&ISO^SS||XXXXLast^XXXXFirst||19000000000000|M||W|XXXXXXXXX^^YYYY^NV^89000^USA^C||^PRN^PH^^^555^5555555|||||||||N\nORC|RE|40823320473600001320150540003344228588^EHR^TBD^ISO|2015054000334|||||||||^Adrian^Adrian^^^^^^^L|SHM CVCU|^WPN^PH^^^702^5089199|||||||SHM- Summerlin Hospital Medical Center^L^^^^SHM- Summerlin Hospital Medical Center&TBD&CLIA|657 Town Center Drive^^Las Vegas^NV^89144-6367^^B^^32003|^^PH^^^702^2337000|5940 S RAINBOW BLVD^^LAS VEGAS^NV^89118-0000^USA^B\nOBR|1|40823320473600001320150540003344228588^EHR^TBD^ISO|2015054000334|24325-3^Acute Hepatitis Panel^LN|||2015022306340000000000||||||None|2015022313090000000000|119297000&Blood&SNM|^Adrian^Adrian^^^^^^^L|^WPN^PH^^^702^5089199|||||20150223141500||LAB|F\nOBX|1|CE|22327-1^Hepatitis C Antibody (Anti HCV)^LN^408^Hepatitis C Antibody (Anti HCV)^L|1|11214006^Reactive^SCT||||||F|||2015022306340000000000|29D1070766^CHH Medical Center^CLIA\nNTE|1||Performing Lab Address: 6900 North Durango Drive, Las Vegas, NV  89149\nBTS|1|Generated by ELR Route\nFTS|1|Generated by NBS ELR Route"
    * def modifiedmsg = hl7Message.replace(oldfirstname, randomFirstName)
    * def modifiedData = modifiedmsg.replace(oldlastname, randomLastName)
    * request modifiedData
    When method POST
    Then status 200
    * def postidddd = response
    Given url '' + postidddd
    * header Authorization = 'Bearer ' + token
    And retry until response.errorMessageId == postidddd
    When method GET
    Then status 200
    Then match response.errorStackTraceShort == "DiHL7Exception: Invalid Message ca.uhn.hl7v2.validation.ValidationException: Validation failed: Primitive value '20150224114200-0400000' requires to be empty or a HL7 datetime string at MSH-6(0)"
    Then match response.dltOccurrence == 1
    Then match response.createdBy == 'elr_raw_dlt'
    Then match response.updatedBy == 'elr_raw_dlt'
    Then match response.updatedOn == null
    Then match response.dltStatus == 'ERROR'
    Then match response.errorMessageSource == 'elr_raw'
    Then match response.message == modifiedData