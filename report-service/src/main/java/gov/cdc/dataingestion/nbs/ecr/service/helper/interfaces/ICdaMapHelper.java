package gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.dao.LookUp.PhdcAnswerDao;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgOrganizationDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgProviderDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlObject;

import java.text.ParseException;

public interface ICdaMapHelper {
    XmlObject mapToCData(String data) throws EcrCdaXmlException;
    XmlObject mapToStringData(String data) throws EcrCdaXmlException;
    XmlObject mapToUsableTSElement(String data, XmlObject output, String name) throws EcrCdaXmlException;
    TS mapToTsType(String data) throws EcrCdaXmlException;
    String mapToQuestionId(String data);
    CE mapToCEAnswerType(String data, String questionCode);
    String mapToAddressType(String data, String questionCode);
    PhdcAnswerDao mapToCodedAnswer(String data, String questionCode);
    POCDMT000040CustodianOrganization mapToElementValue(String data, POCDMT000040CustodianOrganization output, String name);
    String getCurrentUtcDateTimeInCdaFormat();

    POCDMT000040Observation mapToObservation(String questionCode, String data,
                                             POCDMT000040Observation observation) throws EcrCdaXmlException;


    PhdcQuestionLookUpDto mapToCodedQuestionType(String questionIdentifier);
    CE mapToCEQuestionType(String questionCode, CE output);
    XmlObject mapToSTValue(String input, XmlObject output);
    XmlObject mapToObservationPlace(String in, XmlObject out);

    POCDMT000040Participant2 mapToPSN(EcrMsgProviderDto in, POCDMT000040Participant2 out)
            throws EcrCdaXmlException;

    POCDMT000040Participant2 mapToORG(EcrMsgOrganizationDto in,
                                      POCDMT000040Participant2 out)
            throws EcrCdaXmlException;
}
