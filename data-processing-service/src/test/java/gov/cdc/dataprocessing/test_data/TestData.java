package gov.cdc.dataprocessing.test_data;

import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.MaterialContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class TestData {
    public static LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer() {
    };
    public static ObservationContainer observationContainer = new ObservationContainer();
    public static EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();

    public static void createLabResultContainer() {
        // OBS Conn
        var obsConCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDto = new ObservationDto();
        obsConn.setTheObservationDto(obsDto);
        obsConCol.add(obsConn);
        labResultProxyContainer.setTheObservationContainerCollection(obsConCol);

        // Material Conn
        var materialContainerCollection = new ArrayList<MaterialContainer>();
        MaterialContainer materialContainer = new MaterialContainer();
        materialContainerCollection.add(materialContainer);
        labResultProxyContainer.setTheMaterialContainerCollection(materialContainerCollection);

        // Role Dto
        var roleDtoCollection = new ArrayList<RoleDto>();
        RoleDto roleDto = new RoleDto();
        roleDtoCollection.add(roleDto);
        labResultProxyContainer.setTheRoleDtoCollection(roleDtoCollection);

        // Act Id Dto
        var actIdDTCollection = new ArrayList<Object>();
        var actIdDT = new ActIdDto();
        actIdDTCollection.add(actIdDT);
        labResultProxyContainer.setTheActIdDTCollection(actIdDTCollection);

        // Intervention Conn
//        var interventionVOCollection = new ArrayList<>();
//        Object interventionVO = new Object();
//        interventionVOCollection.add(interventionVO);
//        labResultProxyContainer.setTheInterventionVOCollection(interventionVOCollection);

        // Edx Doc
        var edxDocumentCollection = new ArrayList<EDXDocumentDto>();
        EDXDocumentDto edxDocumentDto = new EDXDocumentDto();
        edxDocumentCollection.add(edxDocumentDto);
        labResultProxyContainer.setEDXDocumentCollection(edxDocumentCollection);

        // Condition List
        var conditionsList = new ArrayList<>(Arrays.asList("Condition1", "Condition2"));
        labResultProxyContainer.setTheConditionsList(conditionsList);

        // Message Log Dto
        var messageLogCollection = new ArrayList<MessageLogDto>();
        MessageLogDto messageLogDto = new MessageLogDto();
        messageLogCollection.add(messageLogDto);
        labResultProxyContainer.setMessageLogDCollection(messageLogCollection);

        // Participation
        var patCol = new ArrayList<ParticipationDto>();
        var patDto = new ParticipationDto();
        patCol.add(patDto);
        labResultProxyContainer.setTheParticipationDtoCollection(patCol);

        // Org
        var orgCol = new ArrayList<OrganizationContainer>();
        var orgCon = new OrganizationContainer();
        orgCol.add(orgCon);
        labResultProxyContainer.setTheOrganizationContainerCollection(orgCol);


        labResultProxyContainer.setSendingFacilityUid(123L);
        labResultProxyContainer.setAssociatedNotificationInd(false);
        labResultProxyContainer.setAssociatedInvInd(false);
        labResultProxyContainer.setManualLab(false);
        labResultProxyContainer.setLabClia("ExampleCLIA");

    }

    public static void createObservationContainer() {
        var obsDto = new ObservationDto();
        obsDto.setJurisdictionCd("JUS");
        obsDto.setRptToStateTime(TimeStampUtil.getCurrentTimeStamp());
        observationContainer.setTheObservationDto(obsDto);
    }

    public static void createEdxLabInformationDto(String investigationType) {
        edxLabInformationDto.setNextUid(1);
        edxLabInformationDto.setConditionCode("COND");
        edxLabInformationDto.setInvestigationType(investigationType);

        createLabResultContainer();
        edxLabInformationDto.setLabResultProxyContainer(labResultProxyContainer);

        var obsConCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        // OBS NUMERIC
        var numericCol = new ArrayList<ObsValueNumericDto>();
        var numeric = new ObsValueNumericDto();
        numeric.setNumericValue1(BigDecimal.valueOf(1));
        numeric.setNumericUnitCd("ML");
        numericCol.add(numeric);
        numericCol = new ArrayList<ObsValueNumericDto>();
        numeric = new ObsValueNumericDto();
        numeric.setNumericValue1(BigDecimal.valueOf(2));
        numeric.setNumericUnitCd(null);
        numericCol.add(numeric);
        obsConn.setTheObsValueNumericDtoCollection(numericCol);
        // OBS DATE
        var dateCol = new ArrayList<ObsValueDateDto>();
        var date = new ObsValueDateDto();
        date.setFromTime(TimeStampUtil.getCurrentTimeStamp());
        dateCol.add(date);
        obsConn.setTheObsValueDateDtoCollection(dateCol);
        // OBS VALUE CODED
        var codedCol = new ArrayList<ObsValueCodedDto>();
        var coded = new ObsValueCodedDto();
        coded.setCode("CODE");
        codedCol.add(coded);
        obsConn.setTheObsValueCodedDtoCollection(codedCol);
        // OBS TXT
        var textCol = new ArrayList<ObsValueTxtDto>();
        var text = new ObsValueTxtDto();
        text.setTxtTypeCd("TXT");
        text.setValueTxt("TXT");
        textCol.add(text);
        textCol = new ArrayList<ObsValueTxtDto>();
        text = new ObsValueTxtDto();
        text.setTxtTypeCd(null);
        text.setValueTxt("TXT");
        textCol.add(text);
        obsConn.setTheObsValueTxtDtoCollection(textCol);

        // OBS DTO
        var obsDto = new ObservationDto();
        obsDto.setCd("CODE");
        obsConn.setTheObservationDto(obsDto);
        obsConCol.add(obsConn);
        edxLabInformationDto.getLabResultProxyContainer().setTheObservationContainerCollection(obsConCol);
    }
}
