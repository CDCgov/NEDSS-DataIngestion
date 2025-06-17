package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxLabIdentiferDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.CommonLabUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

@Component

public class ObservationResultRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObservationResultRequestHandler.class);

    private final ICatchingValueDpService checkingValueService;
    private final NBSObjectConverter nbsObjectConverter;
    private final CommonLabUtil commonLabUtil;
    private final ICacheApiService cacheApiService;
    @Value("${service.timezone}")
    private String tz = "UTC";

    public ObservationResultRequestHandler(
            ICatchingValueDpService checkingValueService,
            NBSObjectConverter nbsObjectConverter, CommonLabUtil commonLabUtil, @Lazy ICacheApiService cacheApiService) {
        this.checkingValueService = checkingValueService;
        this.nbsObjectConverter = nbsObjectConverter;
        this.commonLabUtil = commonLabUtil;
        this.cacheApiService = cacheApiService;
    }

    public LabResultProxyContainer getObservationResultRequest(List<HL7OBSERVATIONType> observationRequestArray,
                                                               LabResultProxyContainer labResultProxyContainer,
                                                               EdxLabInformationDto edxLabInformationDto) throws DataProcessingException{
        try {
            for (HL7OBSERVATIONType hl7OBSERVATIONType : observationRequestArray) {
                ObservationContainer observationContainer = getObservationResult(hl7OBSERVATIONType.getObservationResult(), labResultProxyContainer, edxLabInformationDto);
                getObsReqNotes(hl7OBSERVATIONType.getNotesAndComments(), observationContainer);
                labResultProxyContainer.getTheObservationContainerCollection().add(observationContainer);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return labResultProxyContainer;
    }

    @SuppressWarnings({"java:S6541","java:S3776"})
    protected ObservationContainer getObservationResult(HL7OBXType hl7OBXType,
                                                      LabResultProxyContainer labResultProxyContainer,
                                                      EdxLabInformationDto edxLabInformationDto)
            throws DataProcessingException {
        try {
            ObservationContainer observationContainer = initializeObservationContainer(edxLabInformationDto);
            ObservationDto observationDto = observationContainer.getTheObservationDto();

            validateResultedTestName(hl7OBXType, edxLabInformationDto);

            createAndLinkLabIdentifier(edxLabInformationDto, hl7OBXType, observationDto);

            Collection<ActIdDto> actIdDtoList = createActIds(edxLabInformationDto, observationDto);
            actIdDtoList = setEquipments(hl7OBXType.getEquipmentInstanceIdentifier(), observationDto, actIdDtoList);
            observationContainer.setTheActIdDtoCollection(actIdDtoList);

            addActRelationship(observationContainer, labResultProxyContainer, edxLabInformationDto);

            processObservationIdentifier(hl7OBXType.getObservationIdentifier(), observationDto, edxLabInformationDto, observationContainer);

            processObservationValues(hl7OBXType, observationContainer, edxLabInformationDto);

            observationContainer = processingReferringRange(hl7OBXType, observationContainer);
            observationContainer = processingAbnormalFlag(hl7OBXType.getAbnormalFlags(), observationDto, observationContainer);

            processObservationResultStatus(hl7OBXType, observationDto);

            processDateTimeOfAnalysis(hl7OBXType, observationDto);

            processPerformingOrganization(hl7OBXType, observationDto.getObservationUid(), labResultProxyContainer, edxLabInformationDto);

            return processingObservationMethod(hl7OBXType.getObservationMethod(), edxLabInformationDto, observationContainer);

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    protected ObservationContainer initializeObservationContainer(EdxLabInformationDto info) {
        ObservationDto dto = new ObservationDto();
        dto.setCtrlCdDisplayForm(EdxELRConstant.CTRL_CD_DISPLAY_FORM);
        dto.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
        dto.setObservationUid((long) info.getNextUid());
        dto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        dto.setObsDomainCdSt1(info.isParentObsInd() ? EdxELRConstant.ELR_REF_RESULT_CD : EdxELRConstant.ELR_RESULT_CD);
        dto.setItNew(true);
        dto.setItDirty(false);

        ObservationContainer container = new ObservationContainer();
        container.setItNew(true);
        container.setItDirty(false);
        container.setTheObservationDto(dto);
        return container;
    }

    protected void validateResultedTestName(HL7OBXType obx, EdxLabInformationDto info) throws DataProcessingException {
        var id = obx.getObservationIdentifier();
        if (!info.isParentObsInd() && (id == null || (id.getHL7Identifier() == null && id.getHL7AlternateIdentifier() == null))) {
            info.setResultedTestNameMissing(true);
            info.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
            throw new DataProcessingException(EdxELRConstant.NO_RESULT_NAME + " XMLElementName: " +
                    commonLabUtil.getXMLElementNameForOBX(obx) + ".ObservationIdentifier");
        }
    }

    protected void createAndLinkLabIdentifier(EdxLabInformationDto info, HL7OBXType obx, ObservationDto obsDto) {
        EdxLabIdentiferDto labId = new EdxLabIdentiferDto();
        var id = obx.getObservationIdentifier();

        if (id.getHL7Identifier() != null)
            labId.setIdentifer(id.getHL7Identifier());
        else if (id.getHL7AlternateIdentifier() != null)
            labId.setIdentifer(id.getHL7AlternateIdentifier());

        labId.setSubMapID(obx.getObservationSubID());
        labId.setObservationValues(obx.getObservationValue());
        labId.setObservationUid(obsDto.getObservationUid());

        info.getEdxSusLabDTMap().put(labId.getObservationUid(), labId);
        if (info.getEdxLabIdentiferDTColl() == null) {
            info.setEdxLabIdentiferDTColl(new ArrayList<>());
        }
        info.getEdxLabIdentiferDTColl().add(labId);
    }


    protected List<ActIdDto> createActIds(EdxLabInformationDto info, ObservationDto obsDto) {
        List<ActIdDto> list = new ArrayList<>();

        ActIdDto ctrlId = new ActIdDto();
        ctrlId.setActUid(obsDto.getObservationUid());
        ctrlId.setActIdSeq(1);
        ctrlId.setRootExtensionTxt(info.getMessageControlID());
        ctrlId.setAssigningAuthorityCd(info.getSendingFacilityClia());
        ctrlId.setAssigningAuthorityDescTxt(info.getSendingFacilityName());
        ctrlId.setTypeCd(EdxELRConstant.ELR_MESSAGE_CTRL_CD);
        ctrlId.setTypeDescTxt(EdxELRConstant.ELR_MESSAGE_CTRL_DESC);
        ctrlId.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);

        ActIdDto fillerId = new ActIdDto();
        fillerId.setActUid(obsDto.getObservationUid());
        fillerId.setActIdSeq(2);
        fillerId.setRootExtensionTxt(info.getFillerNumber());
        fillerId.setAssigningAuthorityCd(info.getSendingFacilityClia());
        fillerId.setAssigningAuthorityDescTxt(info.getSendingFacilityName());
        fillerId.setTypeCd(EdxELRConstant.ELR_FILLER_NUMBER_CD);
        fillerId.setTypeDescTxt(EdxELRConstant.ELR_FILLER_NUMBER_DESC);
        fillerId.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);

        list.add(ctrlId);
        list.add(fillerId);
        return list;
    }


    protected void addActRelationship(ObservationContainer obsContainer,
                                    LabResultProxyContainer proxy,
                                    EdxLabInformationDto info) {

        ActRelationshipDto rel = new ActRelationshipDto();
        rel.setItNew(true);
        rel.setItDirty(false);
        rel.setAddTime(info.getAddTime());
        rel.setLastChgTime(info.getAddTime());
        rel.setRecordStatusTime(info.getAddTime());
        rel.setTypeCd(EdxELRConstant.ELR_COMP_CD);
        rel.setTypeDescTxt(EdxELRConstant.ELR_COMP_DESC);
        rel.setSourceActUid(obsContainer.getTheObservationDto().getObservationUid());
        rel.setTargetActUid(info.isParentObsInd() ? info.getParentObservationUid() : info.getRootObserbationUid());
        rel.setTargetClassCd(EdxELRConstant.ELR_OBS);
        rel.setSourceClassCd(EdxELRConstant.ELR_OBS);
        rel.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        rel.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        rel.setSequenceNbr(1);

        if (proxy.getTheActRelationshipDtoCollection() == null) {
            proxy.setTheActRelationshipDtoCollection(new ArrayList<>());
        }
        proxy.getTheActRelationshipDtoCollection().add(rel);
    }

    protected void processObservationResultStatus(HL7OBXType obx, ObservationDto dto) throws DataProcessingException {
        String rawStatus = obx.getObservationResultStatus();
        if (rawStatus != null) {
            String toCode = checkingValueService.findToCode("ELR_LCA_STATUS", rawStatus, "ACT_OBJ_ST");
            if (toCode != null && !toCode.trim().isEmpty()) {
                dto.setStatusCd(toCode.trim());
            } else {
                dto.setStatusCd(rawStatus);
            }
        }
    }

    protected void processDateTimeOfAnalysis(HL7OBXType obx, ObservationDto dto) throws DataProcessingException {
        if (obx.getDateTimeOftheAnalysis() != null) {
            dto.setActivityToTime(nbsObjectConverter.processHL7TSType(obx.getDateTimeOftheAnalysis(), EdxELRConstant.DATE_VALIDATION_OBX_LAB_PERFORMED_DATE_MSG));
        }
        dto.setRptToStateTime(dto.getRptToStateTime());
    }


    protected void processPerformingOrganization(HL7OBXType obx,
                                               Long obsUid,
                                               LabResultProxyContainer proxy,
                                               EdxLabInformationDto info) throws DataProcessingException {
        if (obx.getPerformingOrganizationName() != null) {
            OrganizationContainer producerOrg = getPerformingFacility(obx, obsUid, proxy, info);
            proxy.getTheOrganizationContainerCollection().add(producerOrg);
        }
    }

    protected void processObservationValues(HL7OBXType obx,
                                          ObservationContainer container,
                                          EdxLabInformationDto info) throws DataProcessingException {
        List<String> values = obx.getObservationValue();
        String elementName = "ObservationValue";

        for (String val : values) {
            formatValue(val, obx, container, info, elementName);
            if (!List.of(EdxELRConstant.ELR_STRING_CD, EdxELRConstant.ELR_TEXT_CD,
                            EdxELRConstant.ELR_TEXT_DT, EdxELRConstant.ELR_TEXT_TS)
                    .contains(obx.getValueType())) {
                break;
            }
        }
    }


    protected void processObservationIdentifier(HL7CWEType obsIdentifier,
                                              ObservationDto observationDto,
                                              EdxLabInformationDto edxLabInformationDto,
                                              ObservationContainer observationContainer)
            throws DataProcessingException {

        if (obsIdentifier == null) {
            logger.error("ObservationResultRequest.getObservationResult The Resulted Test ObservationCd can't be set to null. Please check. {}", observationDto.getCd());
            throw new DataProcessingException("ObservationResultRequest.getObservationResult The Resulted Test ObservationCd can't be set to null. Please check." + observationDto.getCd());
        }

        // 1. Set Code and Text
        if (obsIdentifier.getHL7Identifier() != null)
            observationDto.setCd(obsIdentifier.getHL7Identifier());

        if (obsIdentifier.getHL7Text() != null)
            observationDto.setCdDescTxt(obsIdentifier.getHL7Text());

        if (observationDto.getCd() == null && obsIdentifier.getHL7AlternateIdentifier() != null)
            observationDto.setCd(obsIdentifier.getHL7AlternateIdentifier());
        else if (observationDto.getCd() != null && obsIdentifier.getHL7AlternateIdentifier() != null)
            observationDto.setAltCd(obsIdentifier.getHL7AlternateIdentifier());

        if (obsIdentifier.getHL7AlternateText() != null && observationDto.getCdDescTxt() == null)
            observationDto.setCdDescTxt(obsIdentifier.getHL7AlternateText());
        else if (obsIdentifier.getHL7AlternateText() != null)
            observationDto.setAltCdDescTxt(obsIdentifier.getHL7AlternateText());

        // 2. Set Code Systems
        if (observationDto.getCd() != null || observationDto.getCdDescTxt() != null) {
            observationDto.setCdSystemCd(obsIdentifier.getHL7NameofCodingSystem());
            observationDto.setCdSystemDescTxt(obsIdentifier.getHL7NameofCodingSystem());
        }

        if (observationDto.getAltCd() != null || observationDto.getAltCdDescTxt() != null) {
            observationDto.setAltCdSystemCd(obsIdentifier.getHL7NameofAlternateCodingSystem());
            observationDto.setAltCdSystemDescTxt(obsIdentifier.getHL7NameofAlternateCodingSystem());
        } else if (observationDto.getCdSystemCd() == null) {
            observationDto.setCdSystemCd(obsIdentifier.getHL7NameofAlternateCodingSystem());
            observationDto.setCdSystemDescTxt(obsIdentifier.getHL7NameofAlternateCodingSystem());
        }

        // 3. Normalize System Descriptions
        if (EdxELRConstant.ELR_LOINC_CD.equals(observationDto.getCdSystemCd())) {
            observationDto.setCdSystemDescTxt(EdxELRConstant.ELR_LOINC_DESC);
            if (cacheApiService.getSrteCacheBool(ObjectName.LOINC_CODES.name(), observationDto.getCd())) {
                observationDto.setMethodCd(NEDSSConstant.AOE_OBS);
            }
        } else if (EdxELRConstant.ELR_SNOMED_CD.equals(observationDto.getCdSystemCd())) {
            observationDto.setCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
        } else if (EdxELRConstant.ELR_LOCAL_CD.equals(observationDto.getCdSystemCd())) {
            observationDto.setCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
        }

        if (EdxELRConstant.ELR_SNOMED_CD.equals(observationDto.getAltCdSystemCd())) {
            observationDto.setAltCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
        } else if (observationDto.getAltCd() != null) {
            observationDto.setAltCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
            observationDto.setAltCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
        }

        // 4. Validation: Drug name required for parent obs
        if (edxLabInformationDto.isParentObsInd()
                && (observationDto.getCd() == null || observationDto.getCd().trim().isEmpty())) {
            edxLabInformationDto.setDrugNameMissing(true);
            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
            throw new DataProcessingException(EdxELRConstant.NO_DRUG_NAME);
        }
    }


    protected OrganizationContainer getPerformingFacility(HL7OBXType hl7OBXType, long observationUid,
                                                        LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {

        HL7XONType hl7XONTypeName = hl7OBXType.getPerformingOrganizationName();

        OrganizationContainer organizationContainer;
        try {
            organizationContainer = new OrganizationContainer();
            OrganizationDto organizationDto = new OrganizationDto();
            organizationContainer.setItNew(true);
            organizationContainer.setItDirty(false);


            organizationDto.setCd(EdxELRConstant.ELR_SENDING_LAB_CD);
            organizationDto.setCdDescTxt(EdxELRConstant.ELR_LABORATORY_DESC);
            organizationDto.setStandardIndustryClassCd(EdxELRConstant.ELR_STANDARD_INDUSTRY_CLASS_CD);
            organizationDto.setStandardIndustryDescTxt(EdxELRConstant.ELR_STANDARD_INDUSTRY_DESC_TXT);
            organizationDto.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
            organizationDto.setOrganizationUid((long)(edxLabInformationDto.getNextUid()));
            organizationDto.setAddUserId(edxLabInformationDto.getUserId());
            organizationDto.setItNew(true);
            organizationDto.setItDirty(false);
            organizationContainer.setTheOrganizationDto(organizationDto);
            
            EntityIdDto entityIdDto = new EntityIdDto();
            entityIdDto.setEntityUid(organizationDto.getOrganizationUid());
            entityIdDto.setRootExtensionTxt(hl7XONTypeName.getHL7OrganizationIdentifier());
            entityIdDto.setTypeCd(EdxELRConstant.ELR_FACILITY_CD);
            entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_FACILITY_DESC);
            entityIdDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            entityIdDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDto.setAsOfDate(edxLabInformationDto.getAddTime());

            entityIdDto.setEntityIdSeq(1);
            if(hl7XONTypeName.getHL7AssigningAuthority()!=null){
                entityIdDto.setAssigningAuthorityCd(hl7XONTypeName.getHL7AssigningAuthority().getHL7UniversalID());
                entityIdDto.setAssigningAuthorityIdType(hl7XONTypeName.getHL7AssigningAuthority().getHL7UniversalIDType());
            }
            if(hl7XONTypeName.getHL7AssigningAuthority()!=null && hl7XONTypeName.getHL7AssigningAuthority().getHL7NamespaceID()!=null 
                    && hl7XONTypeName.getHL7AssigningAuthority().getHL7NamespaceID().equals(EdxELRConstant.ELR_CLIA_CD)) {
                entityIdDto.setAssigningAuthorityDescTxt(EdxELRConstant.ELR_CLIA_DESC);
            }
            
            organizationContainer.getTheEntityIdDtoCollection().add(entityIdDto);

            ParticipationDto participationDto = new ParticipationDto();
            participationDto.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDto.setCd(EdxELRConstant.ELR_REPORTING_ENTITY_CD);
            participationDto.setTypeCd(EdxELRConstant.ELR_LAB_PERFORMER_CD);
            participationDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());

            participationDto.setItNew(true);
            participationDto.setItDirty(false);
            participationDto.setTypeDescTxt(EdxELRConstant.ELR_LAB_PERFORMER_DESC);
            participationDto.setSubjectClassCd(EdxELRConstant.ELR_ORG);
            participationDto.setSubjectEntityUid(organizationDto.getOrganizationUid());
            participationDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            participationDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            participationDto.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
            participationDto.setActUid(observationUid);

            labResultProxyContainer.getTheParticipationDtoCollection().add(participationDto);

            RoleDto roleDto = new RoleDto();
            roleDto.setCd(EdxELRConstant.ELR_REPORTING_ENTITY_CD);
            roleDto.setCdDescTxt(EdxELRConstant.ELR_REPORTING_ENTITY_DESC);
            roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDto.setRoleSeq((long)(1));
            roleDto.setItNew(true);
            roleDto.setItDirty(false);
            roleDto.setAddReasonCd("");
            roleDto.setAddTime(organizationContainer.getTheOrganizationDto().getAddTime());
            roleDto.setAddUserId(organizationContainer.getTheOrganizationDto().getAddUserId());
            roleDto.setItNew(true);
            roleDto.setItDirty(false);
            roleDto.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
            roleDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            roleDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDto.setLastChgTime(organizationContainer.getTheOrganizationDto().getAddTime());
            roleDto.setSubjectClassCd(EdxELRConstant.ELR_SENDING_HCFAC);
            roleDto.setSubjectEntityUid(organizationContainer.getTheOrganizationDto().getOrganizationUid());
            roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
            labResultProxyContainer.getTheRoleDtoCollection().add(roleDto);

            Collection<OrganizationNameDto> orgNameColl = new ArrayList<>();
            OrganizationNameDto organizationNameDto = new OrganizationNameDto(tz);
            organizationNameDto.setNmTxt(hl7XONTypeName.getHL7OrganizationName());
            organizationNameDto.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
            organizationNameDto.setOrganizationNameSeq(1);
            organizationDto.setDisplayNm(organizationNameDto.getNmTxt());
            orgNameColl.add(organizationNameDto);

            organizationContainer.setTheOrganizationNameDtoCollection(orgNameColl);
            HL7XADType addressType = hl7OBXType.getPerformingOrganizationAddress();
            Collection<EntityLocatorParticipationDto> addressCollection = new ArrayList<>();

            if (addressType != null) {
                EntityLocatorParticipationDto elpDT = nbsObjectConverter.organizationAddressType(addressType, EdxELRConstant.ELR_OP_CD, organizationContainer);
                addressCollection.add(elpDT);
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);

        }
        return organizationContainer;
    }

    protected void formatValue(String text,
                               HL7OBXType hl7OBXType,
                               ObservationContainer observationContainer,
                               EdxLabInformationDto edxLabInformationDto,
                               String elementName) throws DataProcessingException {
        try {
            String type = hl7OBXType.getValueType();
            HL7CEType unit = hl7OBXType.getUnits();

            if (type == null) return;

            switch (type) {
                case EdxELRConstant.ELR_CODED_WITH_EXC_CD, EdxELRConstant.ELR_CODED_EXEC_CD ->
                        handleCodedValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);

                case EdxELRConstant.ELR_STUCTURED_NUMERIC_CD ->
                        handleStructuredNumericValue(text, unit, observationContainer);

                case EdxELRConstant.ELR_NUMERIC_CD ->
                        handleSimpleNumericValue(text, unit, observationContainer);

                case EdxELRConstant.ELR_STRING_CD, EdxELRConstant.ELR_TEXT_CD,
                     EdxELRConstant.ELR_TEXT_DT, EdxELRConstant.ELR_TEXT_TS ->
                        handleTextValue(text, observationContainer);

                default -> {
                    edxLabInformationDto.setUnexpectedResultType(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.UNEXPECTED_RESULT_TYPE);
                }
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    protected void handleCodedValue(String text,
                                  HL7OBXType hl7OBXType,
                                  ObservationContainer container,
                                  EdxLabInformationDto info,
                                  String elementName) throws DataProcessingException {

        if (text == null) return;

        String[] textValue = text.split("\\^");
        ObsValueCodedDto dto = new ObsValueCodedDto();
        dto.setItNew(true);
        dto.setItDirty(false);

        if (!text.isEmpty() && textValue.length > 0) {
            dto.setCode(textValue[0]);
            if (textValue.length > 1) dto.setDisplayName(textValue[1]);
            if (textValue.length == 2) dto.setCodeSystemCd(EdxELRConstant.ELR_SNOMED_CD);
            else if (textValue.length == 3) dto.setCodeSystemCd(textValue[2]);
            if (textValue.length >= 6) {
                dto.setAltCd(textValue[3]);
                dto.setAltCdDescTxt(textValue[4]);
            }
        }

        // Validate required
        if ((isEmpty(dto.getCode()) && isEmpty(dto.getAltCd()))) {
            info.setReflexResultedTestCdMissing(true);
            info.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
            String xmlElementName = commonLabUtil.getXMLElementNameForOBX(hl7OBXType) + "." + elementName;
            throw new DataProcessingException(EdxELRConstant.NO_REFLEX_RESULT_NM + " XMLElementName: " + xmlElementName);
        }

        // Fallback to alt if primary missing
        if (isEmpty(dto.getCode())) {
            dto.setCode(dto.getAltCd());
            dto.setDisplayName(dto.getAltCdDescTxt());
            dto.setCodeSystemCd(dto.getAltCdSystemCd());
            dto.setAltCd(null);
            dto.setAltCdDescTxt(null);
            dto.setAltCdSystemCd(null);
        }

        // Set descriptions
        if ("SNOMED".equalsIgnoreCase(dto.getCodeSystemCd()))
            dto.setCodeSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
        else if ("LOCAL".equalsIgnoreCase(dto.getCodeSystemCd()))
            dto.setCodeSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);

        if ("SNOMED".equalsIgnoreCase(dto.getAltCdSystemCd()))
            dto.setAltCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
        else if ("LOCAL".equalsIgnoreCase(dto.getAltCdSystemCd()))
            dto.setAltCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);

        dto.setObservationUid(container.getTheObservationDto().getObservationUid());

        if (container.getTheObsValueCodedDtoCollection() == null)
            container.setTheObsValueCodedDtoCollection(new ArrayList<>());

        container.getTheObsValueCodedDtoCollection().add(dto);
    }

    protected void handleStructuredNumericValue(String text, HL7CEType unit, ObservationContainer container) {
        ObsValueNumericDto dto = new ObsValueNumericDto();
        dto.setObsValueNumericSeq(1);
        dto.setItNew(true);
        dto.setItDirty(false);

        StringTokenizer tokenizer = new StringTokenizer(text, "^");
        int i = text.indexOf("^") == 0 ? 1 : 0;

        while (tokenizer.hasMoreTokens()) {
            i++;
            String token = tokenizer.nextToken();
            switch (i) {
                case 1 -> dto.setComparatorCd1(switch (token) {
                    case "&lt;" -> "<";
                    case "&gt;" -> ">";
                    default -> token;
                });
                case 2 -> dto.setNumericValue1(new BigDecimal(token));
                case 3 -> dto.setSeparatorCd(token);
                case 4 -> dto.setNumericValue2(new BigDecimal(token));
            }
        }

        if (unit != null)
            dto.setNumericUnitCd(unit.getHL7Identifier());

        dto.setObservationUid(container.getTheObservationDto().getObservationUid());

        if (container.getTheObsValueNumericDtoCollection() == null)
            container.setTheObsValueNumericDtoCollection(new ArrayList<>());

        container.getTheObsValueNumericDtoCollection().add(dto);
    }


    private void handleSimpleNumericValue(String text, HL7CEType unit, ObservationContainer container) {
        ObsValueNumericDto dto = new ObsValueNumericDto();
        dto.setObsValueNumericSeq(1);
        dto.setItNew(true);
        dto.setItDirty(false);
        dto.setNumericValue1(new BigDecimal(text));

        if (unit != null)
            dto.setNumericUnitCd(unit.getHL7Identifier());

        dto.setObservationUid(container.getTheObservationDto().getObservationUid());

        if (container.getTheObsValueNumericDtoCollection() == null)
            container.setTheObsValueNumericDtoCollection(new ArrayList<>());

        container.getTheObsValueNumericDtoCollection().add(dto);
    }

    private void handleTextValue(String text, ObservationContainer container) {
        StringTokenizer tokenizer = new StringTokenizer(text, "^");

        if (container.getTheObsValueTxtDtoCollection() == null)
            container.setTheObsValueTxtDtoCollection(new ArrayList<>());

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            ObsValueTxtDto dto = new ObsValueTxtDto();
            dto.setValueTxt(token);
            dto.setObsValueTxtSeq(container.getTheObsValueTxtDtoCollection().size() + 1);
            dto.setTxtTypeCd(EdxELRConstant.ELR_OFFICE_CD);
            dto.setObservationUid(container.getTheObservationDto().getObservationUid());
            dto.setItNew(true);
            dto.setItDirty(false);

            container.getTheObsValueTxtDtoCollection().add(dto);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }




    protected ObservationContainer getObsReqNotes(List<HL7NTEType> noteArray,
                                                  ObservationContainer observationContainer) throws DataProcessingException {
        try {
            for (HL7NTEType noteEntry : noteArray) {
                List<String> comments = noteEntry.getHL7Comment();
                if (comments != null && !comments.isEmpty()) {
                    addCommentsToObservation(comments, observationContainer);
                } else {
                    addEmptyNoteToObservation(observationContainer);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return observationContainer;
    }

    protected void addCommentsToObservation(List<String> comments, ObservationContainer container) {
        for (String comment : comments) {
            ObsValueTxtDto dto = buildObsValueTxtDto(container, comment);
            container.getTheObsValueTxtDtoCollection().add(dto);
        }
    }

    protected void addEmptyNoteToObservation(ObservationContainer container) {
        ObsValueTxtDto dto = buildObsValueTxtDto(container, "\r");
        container.getTheObsValueTxtDtoCollection().add(dto);
    }

    protected ObsValueTxtDto buildObsValueTxtDto(ObservationContainer container, String text) {
        if (container.getTheObsValueTxtDtoCollection() == null) {
            container.setTheObsValueTxtDtoCollection(new ArrayList<>());
        }

        ObsValueTxtDto dto = new ObsValueTxtDto();
        dto.setItNew(true);
        dto.setItDirty(false);
        dto.setObservationUid(container.getTheObservationDto().getObservationUid());
        dto.setTxtTypeCd(EdxELRConstant.ELR_OBX_COMMENT_TYPE);
        dto.setValueTxt(text);
        dto.setObsValueTxtSeq(container.getTheObsValueTxtDtoCollection().size() + 1);
        return dto;
    }



    protected Collection<ActIdDto> setEquipments(List<HL7EIType> equipmentIdType, ObservationDto observationDto, Collection<ActIdDto> actIdDtoColl) {
        int seq = 3;
        for (HL7EIType equipmentId : equipmentIdType) {
            ActIdDto act3IdDT = new ActIdDto();
            act3IdDT.setActUid(observationDto.getObservationUid());
            act3IdDT.setActIdSeq(seq);
            act3IdDT.setRootExtensionTxt(equipmentId.getHL7EntityIdentifier());
            act3IdDT.setAssigningAuthorityCd(equipmentId.getHL7UniversalID());
            act3IdDT.setAssigningAuthorityDescTxt(equipmentId.getHL7UniversalIDType());
            act3IdDT.setTypeCd(EdxELRConstant.ELR_EQUIPMENT_INSTANCE_CD);
            act3IdDT.setTypeDescTxt(EdxELRConstant.ELR_EQUIPMENT_INSTANCE_DESC);
            act3IdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDtoColl.add(act3IdDT);
            seq = seq + 1;
        }

        return actIdDtoColl;

    }

    protected ObservationContainer processingAbnormalFlag(List<HL7CWEType> abnormalFlag, ObservationDto observationDto,
                                          ObservationContainer observationContainer) throws DataProcessingException {
        if(abnormalFlag !=null && !abnormalFlag.isEmpty())
        {
            ObservationInterpDto observationIntrepDT = new ObservationInterpDto();
            observationIntrepDT.setObservationUid(observationDto.getObservationUid());
            observationIntrepDT.setInterpretationCd(abnormalFlag.getFirst().getHL7Identifier());

            String str= checkingValueService.getCodeDescTxtForCd("OBS_INTRP",observationIntrepDT.getInterpretationCd());
            if(str==null || str.trim().isEmpty()) {
                observationIntrepDT.setInterpretationDescTxt(abnormalFlag.getFirst().getHL7Text());
            }
            else {
                observationIntrepDT.setInterpretationDescTxt(str);
            }
            observationIntrepDT.setObservationUid(observationContainer.getTheObservationDto().getObservationUid());
            observationContainer.setTheObservationInterpDtoCollection(new ArrayList<>());
            observationContainer.getTheObservationInterpDtoCollection().add(observationIntrepDT);
        }
        return observationContainer;
    }

    protected ObservationContainer processingReferringRange(HL7OBXType hl7OBXType, ObservationContainer observationContainer) {
        String range = hl7OBXType.getReferencesRange();
        if (range == null) {
            return observationContainer;
        }

        ObsValueNumericDto obsValueNumericDto = prepareObsValueNumeric(observationContainer);

        if (range.contains("^")) {
            parseRangeWithDelimiter(range, obsValueNumericDto);
        } else {
            obsValueNumericDto.setLowRange(range);
            ensureNumericCollectionExists(observationContainer);
            observationContainer.getTheObsValueNumericDtoCollection().add(obsValueNumericDto);
        }

        return observationContainer;
    }

    private void parseRangeWithDelimiter(String range, ObsValueNumericDto dto) {
        int i = range.indexOf("^") == 0 ? 1 : 0;
        StringTokenizer tokenizer = new StringTokenizer(range, "^");

        while (tokenizer.hasMoreTokens()) {
            i++;
            String token = tokenizer.nextToken();
            if (i == 1) {
                dto.setLowRange(token);
            } else if (i == 2) {
                dto.setHighRange(token);
            }
        }
    }

    private void ensureNumericCollectionExists(ObservationContainer container) {
        if (container.getTheObsValueNumericDtoCollection() == null) {
            container.setTheObsValueNumericDtoCollection(new ArrayList<>());
        }
    }


    private ObsValueNumericDto prepareObsValueNumeric(ObservationContainer container) {
        if (container.getTheObsValueNumericDtoCollection() != null &&
                !container.getTheObsValueNumericDtoCollection().isEmpty()) {
            return new ArrayList<>(container.getTheObsValueNumericDtoCollection()).getFirst();
        }

        ObsValueNumericDto dto = new ObsValueNumericDto();
        dto.setItNew(true);
        dto.setItDirty(false);
        dto.setObsValueNumericSeq(1);
        dto.setObservationUid(container.getTheObservationDto().getObservationUid());
        return dto;
    }



    protected ObservationContainer processingObservationMethod(List<HL7CEType> methodArray,
                                                               EdxLabInformationDto edxLabInformationDto,
                                                               ObservationContainer observationContainer)
            throws DataProcessingException {

        final String delimiter = "**";
        StringBuilder methodCd = new StringBuilder();
        StringBuilder methodDescTxt = new StringBuilder();

        for (HL7CEType method : methodArray) {
            processMethodEntry(method, edxLabInformationDto, methodCd, methodDescTxt, delimiter);
        }

        trimTrailingDelimiter(methodCd, delimiter);
        trimTrailingDelimiter(methodDescTxt, delimiter);

        ObservationDto dto = observationContainer.getTheObservationDto();
        dto.setMethodCd(!methodCd.isEmpty() ? methodCd.toString() : "");
        dto.setMethodDescTxt(!methodDescTxt.isEmpty() ? methodDescTxt.toString() : "");

        return observationContainer;
    }

    private void processMethodEntry(HL7CEType method,
                                    EdxLabInformationDto info,
                                    StringBuilder codeBuilder,
                                    StringBuilder descBuilder,
                                    String delimiter) throws DataProcessingException {

        if (method.getHL7Identifier() != null) {
            appendWithDelimiter(codeBuilder, method.getHL7Identifier(), delimiter);
            String translated = checkingValueService.getCodeDescTxtForCd("OBS_METH", method.getHL7Identifier());

            if (translated == null || translated.trim().isEmpty()) {
                logger.warn("ObservationResultRequest.getObservationResult warning: Method code could not be translated. Please check!!!");
                info.setObsMethodTranslated(false);
            }

            if (method.getHL7Text() != null) {
                appendWithDelimiter(descBuilder, method.getHL7Text(), delimiter);
            }
        }
    }

    private void appendWithDelimiter(StringBuilder sb, String value, String delimiter) {
        if (sb.isEmpty()) {
            sb.append(value).append(delimiter);
        } else {
            sb.append(value).append(delimiter);
        }
    }

    private void trimTrailingDelimiter(StringBuilder sb, String delimiter) {
        int lastIndex = sb.lastIndexOf(delimiter);
        if (lastIndex > 0) {
            sb.setLength(lastIndex);
        }
    }




}
