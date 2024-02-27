package gov.cdc.dataprocessing.utilities.component.data_extraction;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dt.EdxLabIdentiferDT;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.service.interfaces.ICheckingValueService;
import gov.cdc.dataprocessing.utilities.data_extraction.CommonLabUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

@Component
public class ObservationResultRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObservationResultRequestHandler.class);

    private final ICheckingValueService checkingValueService;
    private final NBSObjectConverter nbsObjectConverter;


    public ObservationResultRequestHandler(
            ICheckingValueService checkingValueService,
            NBSObjectConverter nbsObjectConverter) {
        this.checkingValueService = checkingValueService;
        this.nbsObjectConverter = nbsObjectConverter;
    }

    public LabResultProxyContainer getObservationResultRequest(List<HL7OBSERVATIONType> observationRequestArray,
                                                               LabResultProxyContainer labResultProxyContainer,
                                                               EdxLabInformationDto edxLabInformationDto) throws DataProcessingException{
        try {
            //TODO: This process taking some time, pershap move the caching from legacy is a good move
            for (HL7OBSERVATIONType hl7OBSERVATIONType : observationRequestArray) {
                try {
                    ObservationVO observationVO = getObservationResult(hl7OBSERVATIONType.getObservationResult(), labResultProxyContainer, edxLabInformationDto);
                    getObsReqNotes(hl7OBSERVATIONType.getNotesAndComments(), observationVO);
                    //edxLabInformationDT.setParentObservationUid(observationVO.getTheObservationDT().getObservationUid());
                    labResultProxyContainer.getTheObservationVOCollection().add(observationVO);
                } catch (Exception e) {
                    logger.error("ObservationResultRequest.getObservationResultRequest Exception thrown while processing observationRequestArray. Please check!!!" + e.getMessage(), e);
                    throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObservationResultRequest while oricessing observationRequestArray:" + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("ObservationResultRequest.getObservationResultRequest Exception thrown while parsing XML document. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObservationResultRequest:"+ e.getMessage());
        }
        return labResultProxyContainer;
    }

    private ObservationVO getObservationResult(HL7OBXType hl7OBXType, LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto)
            throws DataProcessingException {
        ObservationVO observationVO;
        try {
            observationVO = new ObservationVO();

            ObservationDT observationDT= new ObservationDT();
            observationDT.setCtrlCdDisplayForm(EdxELRConstant.CTRL_CD_DISPLAY_FORM);
            observationDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
            observationDT.setObservationUid((long)(edxLabInformationDto.getNextUid()));
            observationDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            if(edxLabInformationDto.isParentObsInd()){
                observationDT.setObsDomainCdSt1(EdxELRConstant.ELR_REF_RESULT_CD);
            }else{
                observationDT.setObsDomainCdSt1(EdxELRConstant.ELR_RESULT_CD);
            }


            observationDT.setItNew(true);
            observationDT.setItDirty(false);
            observationVO.setItNew(true);
            observationVO.setItDirty(false);
            observationVO.setTheObservationDT(observationDT);
            EdxLabIdentiferDT edxLabIdentiferDT = new EdxLabIdentiferDT();

            if(!edxLabInformationDto.isParentObsInd() && (hl7OBXType.getObservationIdentifier()== null
                    || (hl7OBXType.getObservationIdentifier().getHL7Identifier()==null && hl7OBXType.getObservationIdentifier().getHL7AlternateIdentifier()==null))){
                edxLabInformationDto.setResultedTestNameMissing(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                //TODO: Logic to convert to XML
                String xmlElementName = CommonLabUtil.getXMLElementNameForOBX(hl7OBXType)+".ObservationIdentifier";
                throw new DataProcessingException(EdxELRConstant.NO_RESULT_NAME+" XMLElementName: "+xmlElementName);
            }

            if(hl7OBXType.getObservationIdentifier().getHL7Identifier()!=null){
                edxLabIdentiferDT.setIdentifer(hl7OBXType.getObservationIdentifier().getHL7Identifier());
            }
            else if(hl7OBXType.getObservationIdentifier().getHL7AlternateIdentifier()!=null) {
                edxLabIdentiferDT.setIdentifer(hl7OBXType.getObservationIdentifier().getHL7AlternateIdentifier());
            }
            edxLabIdentiferDT.setSubMapID(hl7OBXType.getObservationSubID());
            edxLabIdentiferDT.setObservationValues(hl7OBXType.getObservationValue()) ;
            edxLabIdentiferDT.setObservationUid(observationDT.getObservationUid());
            edxLabInformationDto.getEdxSusLabDTMap().put(edxLabIdentiferDT.getObservationUid(),edxLabIdentiferDT);
            if(edxLabInformationDto.getEdxLabIdentiferDTColl()==null) {
                edxLabInformationDto.setEdxLabIdentiferDTColl(new ArrayList<>());
            }

            edxLabInformationDto.getEdxLabIdentiferDTColl().add(edxLabIdentiferDT);
            Collection<ActIdDT> actIdDTColl =  new ArrayList<>();
            ActIdDT actIdDT= new ActIdDT();
            actIdDT.setActIdSeq(1);
            actIdDT.setActUid(observationDT.getObservationUid());
            actIdDT.setRootExtensionTxt(edxLabInformationDto.getMessageControlID());
            actIdDT.setAssigningAuthorityCd(edxLabInformationDto.getSendingFacilityClia());
            actIdDT.setAssigningAuthorityDescTxt(edxLabInformationDto.getSendingFacilityName());
            actIdDT.setTypeCd(EdxELRConstant.ELR_MESSAGE_CTRL_CD);
            actIdDT.setTypeDescTxt(EdxELRConstant.ELR_MESSAGE_CTRL_DESC);
            actIdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDTColl.add(actIdDT);

            ActIdDT act2IdDT = new ActIdDT();
            act2IdDT.setActUid(observationDT.getObservationUid());
            act2IdDT.setActIdSeq(2);
            act2IdDT.setRootExtensionTxt(edxLabInformationDto.getFillerNumber());
            act2IdDT.setAssigningAuthorityCd(edxLabInformationDto.getSendingFacilityClia());
            act2IdDT.setAssigningAuthorityDescTxt(edxLabInformationDto.getSendingFacilityName());
            act2IdDT.setTypeCd(EdxELRConstant.ELR_FILLER_NUMBER_CD);
            act2IdDT.setTypeDescTxt(EdxELRConstant.ELR_FILLER_NUMBER_DESC);
            act2IdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actIdDTColl.add(act2IdDT);

            /*
             * ND-18349 HHS ELR Updates for COVID: Updates Required to Process OBX 18
             */
            List<HL7EIType> equipmentIdType = hl7OBXType.getEquipmentInstanceIdentifier();
            int seq = 3;
            for (HL7EIType equipmentId : equipmentIdType) {
                ActIdDT act3IdDT = new ActIdDT();
                act3IdDT.setActUid(observationDT.getObservationUid());
                act3IdDT.setActIdSeq(seq);
                act3IdDT.setRootExtensionTxt(equipmentId.getHL7EntityIdentifier());
                act3IdDT.setAssigningAuthorityCd(equipmentId.getHL7UniversalID());
                act3IdDT.setAssigningAuthorityDescTxt(equipmentId.getHL7UniversalIDType());
                act3IdDT.setTypeCd(EdxELRConstant.ELR_EQUIPMENT_INSTANCE_CD);
                act3IdDT.setTypeDescTxt(EdxELRConstant.ELR_EQUIPMENT_INSTANCE_DESC);
                act3IdDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
                actIdDTColl.add(act3IdDT);
                seq = seq + 1;
            }

            observationVO.setTheActIdDTCollection(actIdDTColl);

            ActRelationshipDT actRelationshipDT = new ActRelationshipDT();
            actRelationshipDT.setItNew(true);
            actRelationshipDT.setItDirty(false);
            actRelationshipDT.setAddTime(edxLabInformationDto.getAddTime());
            actRelationshipDT.setLastChgTime(edxLabInformationDto.getAddTime());
            actRelationshipDT.setRecordStatusTime(edxLabInformationDto.getAddTime());
            actRelationshipDT.setTypeCd(EdxELRConstant.ELR_COMP_CD);
            actRelationshipDT.setTypeDescTxt(EdxELRConstant.ELR_COMP_DESC);
            actRelationshipDT.setSourceActUid(observationVO.getTheObservationDT().getObservationUid());
            if(edxLabInformationDto.isParentObsInd()){
                actRelationshipDT.setTargetActUid(edxLabInformationDto.getParentObservationUid());
            }
            else {
                actRelationshipDT.setTargetActUid(edxLabInformationDto.getRootObserbationUid());
            }
            actRelationshipDT.setTargetClassCd(EdxELRConstant.ELR_OBS);
            actRelationshipDT.setSourceClassCd(EdxELRConstant.ELR_OBS);
            actRelationshipDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            actRelationshipDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            actRelationshipDT.setSequenceNbr(1);
            actRelationshipDT.setItNew(true);
            actRelationshipDT.setItDirty(false);
            if(labResultProxyContainer.getTheActRelationshipDTCollection()==null) {
                labResultProxyContainer.setTheActRelationshipDTCollection(new ArrayList<>());
            }
            labResultProxyContainer.getTheActRelationshipDTCollection().add(actRelationshipDT);

            HL7CWEType obsIdentifier= hl7OBXType.getObservationIdentifier();
            if(obsIdentifier!=null){
                if(obsIdentifier.getHL7Identifier()!=null)
                    observationDT.setCd(obsIdentifier.getHL7Identifier());
                if(obsIdentifier.getHL7Text()!=null)
                    observationDT.setCdDescTxt(obsIdentifier.getHL7Text());

                if(observationDT.getCd()==null && obsIdentifier.getHL7AlternateIdentifier()!=null)
                    observationDT.setCd(obsIdentifier.getHL7AlternateIdentifier());
                else if(observationDT.getCd()!=null && obsIdentifier.getHL7AlternateIdentifier()!=null)
                    observationDT.setAltCd(obsIdentifier.getHL7AlternateIdentifier());
                if(obsIdentifier.getHL7AlternateText()!=null && observationDT.getCdDescTxt()==null)
                    observationDT.setCdDescTxt(obsIdentifier.getHL7AlternateText());
                else if(obsIdentifier.getHL7AlternateText()!=null && observationDT.getCdDescTxt()!=null)
                    observationDT.setAltCdDescTxt(obsIdentifier.getHL7AlternateText());


                if(observationDT.getCd()!=null || observationDT.getCdDescTxt()!=null){
                    observationDT.setCdSystemCd(obsIdentifier.getHL7NameofCodingSystem());
                    observationDT.setCdSystemDescTxt(obsIdentifier.getHL7NameofCodingSystem());
                }
                if(observationDT.getAltCd()!=null || observationDT.getAltCdDescTxt()!=null){
                    observationDT.setAltCdSystemCd(obsIdentifier.getHL7NameofAlternateCodingSystem());
                    observationDT.setAltCdSystemDescTxt(obsIdentifier.getHL7NameofAlternateCodingSystem());
                }else if(observationDT.getCdSystemCd()==null){
                    observationDT.setCdSystemCd(obsIdentifier.getHL7NameofAlternateCodingSystem());
                    observationDT.setCdSystemDescTxt(obsIdentifier.getHL7NameofAlternateCodingSystem());
                }
                if (observationDT.getCdSystemCd() != null
                        && observationDT.getCdSystemCd().equals(EdxELRConstant.ELR_LOINC_CD)) {
                    observationDT.setCdSystemCd(EdxELRConstant.ELR_LOINC_CD);
                    observationDT.setCdSystemDescTxt(EdxELRConstant.ELR_LOINC_DESC);

                    var aOELOINCs = SrteCache.loincCodesMap;
                    if (aOELOINCs != null && aOELOINCs.containsKey(observationDT.getCd())) {
                        observationDT.setMethodCd(NEDSSConstant.AOE_OBS);
                    }

                }else if(observationDT.getCdSystemCd()!=null && observationDT.getCdSystemCd().equals(EdxELRConstant.ELR_SNOMED_CD)){
                    observationDT.setCdSystemCd(EdxELRConstant.ELR_SNOMED_CD);
                    observationDT.setCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
                }else if(observationDT.getCdSystemCd()!=null && observationDT.getCdSystemCd().equals(EdxELRConstant.ELR_LOCAL_CD)){
                    observationDT.setCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                    observationDT.setCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                }

                if(observationDT.getAltCd()!=null && observationDT.getAltCdSystemCd()!=null && observationDT.getAltCdSystemCd().equals(EdxELRConstant.ELR_SNOMED_CD)){
                    observationDT.setAltCdSystemCd(EdxELRConstant.ELR_SNOMED_CD);
                    observationDT.setAltCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
                }else if(observationDT.getAltCd()!=null){
                    observationDT.setAltCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                    observationDT.setAltCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                }

                if(edxLabInformationDto.isParentObsInd()){
                    if(observationVO.getTheObservationDT().getCd() == null || observationVO.getTheObservationDT().getCd().trim().equals("")) {
                        edxLabInformationDto.setDrugNameMissing(true);
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                        throw new DataProcessingException(EdxELRConstant.NO_DRUG_NAME);
                    }
                }
            }
            else{
                logger.error("ObservationResultRequest.getObservationResult The Resulted Test ObservationCd  can't be set to null. Please check." + observationDT.getCd());
                throw new DataProcessingException("ObservationResultRequest.getObservationResult The Resulted Test ObservationCd  can't be set to null. Please check." + observationDT.getCd());
            }


            List<String>  obsValueArray =hl7OBXType.getObservationValue();
            String elementName = "ObservationValue";
            for (String text : obsValueArray) {
                formatValue(text, hl7OBXType, observationVO, edxLabInformationDto, elementName);
                if (!(hl7OBXType.getValueType().equals(EdxELRConstant.ELR_STRING_CD)
                        || hl7OBXType.getValueType().equals(EdxELRConstant.ELR_TEXT_CD)
                        || hl7OBXType.getValueType().equals(EdxELRConstant.ELR_TEXT_DT)
                        || hl7OBXType.getValueType().equals(EdxELRConstant.ELR_TEXT_TS))) {
                    break;
                }
            }

            if(hl7OBXType.getReferencesRange()!=null){
                String range =hl7OBXType.getReferencesRange();
                ObsValueNumericDT obsValueNumericDT;
                if(observationVO.getTheObsValueNumericDTCollection()!=null){
                    obsValueNumericDT = observationVO.getTheObsValueNumericDTCollection().get(0);
                }else{
                    obsValueNumericDT = new ObsValueNumericDT();
                    obsValueNumericDT.setItNew(true);
                    obsValueNumericDT.setItDirty(false);
                    obsValueNumericDT.setObsValueNumericSeq(1);
                    obsValueNumericDT.setObservationUid(observationVO.getTheObservationDT().getObservationUid());
                }
                if(range.contains("^")){
                    int i=0;
                    if(range.indexOf("^")==0){
                        i=1;
                    }
                    StringTokenizer st = new StringTokenizer(range, "^");
                    while (st.hasMoreTokens()) {
                        i++;
                        String token = st.nextToken();
                        if(i==1)
                            obsValueNumericDT.setLowRange(token);
                        else if(i==2)
                            obsValueNumericDT.setHighRange(token);
                    }
                }
                else{
                    obsValueNumericDT.setLowRange(range);
                    if(observationVO.getTheObsValueNumericDTCollection()==null){
                        observationVO.setTheObsValueNumericDTCollection(new ArrayList<>());
                        observationVO.getTheObsValueNumericDTCollection().add(obsValueNumericDT);
                    }
                }
            }
            if(hl7OBXType.getAbnormalFlags()!=null && hl7OBXType.getAbnormalFlags().size()>0){
                ObservationInterpDT observationIntrepDT = new ObservationInterpDT();
                observationIntrepDT.setObservationUid(observationDT.getObservationUid());
                observationIntrepDT.setInterpretationCd(hl7OBXType.getAbnormalFlags().get(0).getHL7Identifier());

                String str= checkingValueService.getCodeDescTxtForCd("OBS_INTRP",observationIntrepDT.getInterpretationCd());
                if(str==null || str.trim().length()==0) {
                    observationIntrepDT.setInterpretationDescTxt(hl7OBXType.getAbnormalFlags().get(0).getHL7Text());
                }
                else {
                    observationIntrepDT.setInterpretationDescTxt(str);
                }
                observationIntrepDT.setObservationUid(observationVO.getTheObservationDT().getObservationUid());
                observationVO.setTheObservationInterpDTCollection(new ArrayList<Object>());
                observationVO.getTheObservationInterpDTCollection().add(observationIntrepDT);

            }
            if(hl7OBXType.getObservationResultStatus()!=null){
                String toCode = checkingValueService.findToCode("ELR_LCA_STATUS", hl7OBXType.getObservationResultStatus(), "ACT_OBJ_ST");
                if (toCode != null && !toCode.equals("") && !toCode.equals(" ")){
                    observationDT.setStatusCd(toCode.trim());

                }else{
                    observationDT.setStatusCd(hl7OBXType.getObservationResultStatus());
                }
            }
            // It was decided to use only OBX19 for this field instead of OBX14(as in 2.3.1) - ER 1085 in Rel4.4
            if(hl7OBXType.getDateTimeOftheAnalysis()!=null){
                observationDT.setActivityToTime(NBSObjectConverter.processHL7TSType(hl7OBXType.getDateTimeOftheAnalysis(),EdxELRConstant.DATE_VALIDATION_OBX_LAB_PERFORMED_DATE_MSG));
            }

            observationDT.setRptToStateTime(edxLabInformationDto.getLastChgTime());
            //2.3.1 to 2.5.1 translation copies this filed from OBX-15(CWE data type) to OBX-23(XON data type) which is required, so always reading it from OBX-23.
            HL7XONType hl7XONTypeName = hl7OBXType.getPerformingOrganizationName();
            if(hl7XONTypeName!=null){
                OrganizationVO producerOrg = getPerformingFacility(hl7OBXType,observationVO.getTheObservationDT().getObservationUid(), labResultProxyContainer, edxLabInformationDto);
                labResultProxyContainer.getTheOrganizationVOCollection().add(producerOrg);
            }
            List<HL7CEType> methodArray = hl7OBXType.getObservationMethod();
            String methodCd = null;
            String methodDescTxt = null;
            final String delimiter = "**";
            for (HL7CEType method : methodArray) {
                if (method.getHL7Identifier() != null) {
                    if (methodCd == null)
                        methodCd = method.getHL7Identifier() + delimiter;
                    else
                        methodCd = methodCd + method.getHL7Identifier() + delimiter;

                    String str = checkingValueService.getCodeDescTxtForCd("OBS_METH", method.getHL7Identifier());
                    if (str == null || str.trim().equals("")) {

                        logger.warn(
                                "ObservationResultRequest.getObservationResult warning: Method code could not be teranslated. Please check!!!");
                        edxLabInformationDto.setObsMethodTranslated(false);
                    }
                    if (method.getHL7Text() != null) {
                        if (methodDescTxt == null)
                            methodDescTxt = method.getHL7Text() + delimiter;
                        else
                            methodDescTxt = methodDescTxt + method.getHL7Text() + delimiter;
                    }
                }
            }

            if (methodCd != null && methodCd.lastIndexOf(delimiter) > 0)
                methodCd = methodCd.substring(0, methodCd.lastIndexOf(delimiter));
            if (methodDescTxt != null && methodDescTxt.lastIndexOf(delimiter) > 0)
                methodDescTxt = methodDescTxt.substring(0, methodDescTxt.lastIndexOf(delimiter));
            observationVO.getTheObservationDT().setMethodCd(methodCd);
            observationVO.getTheObservationDT().setMethodDescTxt(methodDescTxt);
        } catch (Exception e) {
            logger.error("ObservationResultRequest.getObservationResult Exception thrown while parsing XML document. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObservationResult:"+ e.getMessage());
        }

        return observationVO;
    }

    private OrganizationVO getPerformingFacility(HL7OBXType hl7OBXType, long observationUid,
                                                 LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {

        HL7XONType hl7XONTypeName = hl7OBXType.getPerformingOrganizationName();

        OrganizationVO organizationVO;
        try {
            organizationVO = new OrganizationVO();
            OrganizationDT organizationDT= new OrganizationDT();
            organizationVO.setItNew(true);
            organizationVO.setItDirty(false);


            organizationDT.setCd(EdxELRConstant.ELR_SENDING_LAB_CD);
            organizationDT.setCdDescTxt(EdxELRConstant.ELR_LABORATORY_DESC);
            organizationDT.setStandardIndustryClassCd(EdxELRConstant.ELR_STANDARD_INDUSTRY_CLASS_CD);
            organizationDT.setStandardIndustryDescTxt(EdxELRConstant.ELR_STANDARD_INDUSTRY_DESC_TXT);
            organizationDT.setElectronicInd(EdxELRConstant.ELR_ELECTRONIC_IND);
            organizationDT.setOrganizationUid((long)(edxLabInformationDto.getNextUid()));
            organizationDT.setAddUserId(edxLabInformationDto.getUserId());
            organizationDT.setItNew(true);
            organizationDT.setItDirty(false);
            organizationVO.setTheOrganizationDT(organizationDT);
            
            EntityIdDto entityIdDto = new EntityIdDto();
            entityIdDto.setEntityUid(organizationDT.getOrganizationUid());
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
            
            organizationVO.getTheEntityIdDtoCollection().add(entityIdDto);

            ParticipationDT participationDT = new ParticipationDT();
            participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
            participationDT.setCd(EdxELRConstant.ELR_REPORTING_ENTITY_CD);
            participationDT.setTypeCd(EdxELRConstant.ELR_LAB_PERFORMER_CD);
            participationDT.setAddUserId(EdxELRConstant.ELR_ADD_USER_ID);

            participationDT.setItNew(true);
            participationDT.setItDirty(false);
            participationDT.setTypeDescTxt(EdxELRConstant.ELR_LAB_PERFORMER_DESC);
            participationDT.setSubjectClassCd(EdxELRConstant.ELR_ORG);
            participationDT.setSubjectEntityUid(organizationDT.getOrganizationUid());
            participationDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            participationDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            participationDT.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
            participationDT.setActUid(observationUid);

            labResultProxyContainer.getTheParticipationDTCollection().add(participationDT);

            RoleDto roleDto = new RoleDto();
            roleDto.setCd(EdxELRConstant.ELR_REPORTING_ENTITY_CD);
            roleDto.setCdDescTxt(EdxELRConstant.ELR_REPORTING_ENTITY_DESC);
            roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
            roleDto.setRoleSeq((long)(1));
            roleDto.setItNew(true);
            roleDto.setItDirty(false);
            roleDto.setAddReasonCd("");
            roleDto.setAddTime(organizationVO.getTheOrganizationDT().getAddTime());
            roleDto.setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
            roleDto.setItNew(true);
            roleDto.setItDirty(false);
            roleDto.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
            roleDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            roleDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDto.setLastChgTime(organizationVO.getTheOrganizationDT().getAddTime());
            roleDto.setSubjectClassCd(EdxELRConstant.ELR_SENDING_HCFAC);
            roleDto.setSubjectEntityUid(organizationVO.getTheOrganizationDT().getOrganizationUid());
            roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
            labResultProxyContainer.getTheRoleDtoCollection().add(roleDto);

            Collection<OrganizationNameDT> orgNameColl = new ArrayList<>();
            OrganizationNameDT organizationNameDT = new OrganizationNameDT();
            organizationNameDT.setNmTxt(hl7XONTypeName.getHL7OrganizationName());
            organizationNameDT.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
            organizationNameDT.setOrganizationNameSeq(1);
            organizationDT.setDisplayNm(organizationNameDT.getNmTxt());
            orgNameColl.add(organizationNameDT);

            organizationVO.setTheOrganizationNameDTCollection(orgNameColl);
            HL7XADType addressType = hl7OBXType.getPerformingOrganizationAddress();
            Collection<EntityLocatorParticipationDto> addressCollection = new ArrayList<>();

            if (addressType != null) {
                EntityLocatorParticipationDto elpDT = nbsObjectConverter.organizationAddressType(addressType, EdxELRConstant.ELR_OP_CD, organizationVO);
                addressCollection.add(elpDT);
            }

        } catch (Exception e) {
            logger.error("ObservationResultRequest.getPerformingFacility Exception thrown while parsing XML document. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getPerformingFacility:"+ e.getMessage());

        }
        return organizationVO;
    }


    private static void formatValue(String text, HL7OBXType hl7OBXType, ObservationVO observationVO, EdxLabInformationDto edxLabInformationDto, String elementName) throws DataProcessingException{
        String type = "";
        try {
            type = hl7OBXType.getValueType();
            HL7CEType cEUnit = hl7OBXType.getUnits();
            if(type!=null){
                if(type.equals(EdxELRConstant.ELR_CODED_WITH_EXC_CD) ||type.equals(EdxELRConstant.ELR_CODED_EXEC_CD)){
                    if(text!=null){
                        ObsValueCodedDT obsvalueDT= new ObsValueCodedDT();
                        obsvalueDT.setItNew(true);
                        obsvalueDT.setItDirty(false);
                        String[] textValue = text.split("\\^");

                        if (textValue != null && textValue.length>0) {
                            obsvalueDT.setCode(textValue[0]);
                            obsvalueDT.setDisplayName(textValue[1]);
                            if(textValue.length==2){
                                obsvalueDT.setCodeSystemCd(EdxELRConstant.ELR_SNOMED_CD);
                            }else if(textValue.length==3 || textValue.length>3){
                                obsvalueDT.setCodeSystemCd(textValue[2]);
                            }
                            if (textValue.length == 6) {
                                obsvalueDT.setAltCd(textValue[3]);
                                obsvalueDT.setAltCdDescTxt(textValue[4]);
                                if(textValue.length==4){
                                    obsvalueDT.setAltCdSystemCd(EdxELRConstant.ELR_LOCAL_CD);
                                }else if(textValue.length==5 || textValue.length>5){
                                    obsvalueDT.setAltCdSystemCd(textValue[5]);
                                }
                            }
                        }
                        if((obsvalueDT.getCode()==null || obsvalueDT.getCode().trim().equals(""))
                                && (obsvalueDT.getAltCd()==null || obsvalueDT.getAltCd().trim().equals(""))){
                            edxLabInformationDto.setReflexResultedTestCdMissing(true);
                            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_19);
                            // TODO: XML
                            String xmlElementName = CommonLabUtil.getXMLElementNameForOBX(hl7OBXType)+"."+elementName;
                            throw new DataProcessingException(EdxELRConstant.NO_REFLEX_RESULT_NM+" XMLElementName: "+xmlElementName);
                        }

                        if(obsvalueDT.getCode() == null || obsvalueDT.getCode().trim().equals("")){
                            obsvalueDT.setCode(obsvalueDT.getAltCd());
                            obsvalueDT.setDisplayName(obsvalueDT.getAltCdDescTxt());
                            obsvalueDT.setCodeSystemCd(obsvalueDT.getAltCdSystemCd());
                            obsvalueDT.setAltCd(null);
                            obsvalueDT.setAltCdDescTxt(null);
                            obsvalueDT.setAltCdSystemCd(null);
                        }

                        if(obsvalueDT.getCodeSystemCd()!=null && obsvalueDT.getCodeSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_SNOMED_CD))
                            obsvalueDT.setCodeSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
                        else if(obsvalueDT.getCodeSystemCd()!=null && obsvalueDT.getCodeSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_LOCAL_CD))
                            obsvalueDT.setCodeSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);
                        if(obsvalueDT.getAltCdSystemCd()!=null && obsvalueDT.getAltCdSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_SNOMED_CD))
                            obsvalueDT.setAltCdSystemDescTxt(EdxELRConstant.ELR_SNOMED_DESC);
                        else if(obsvalueDT.getAltCdSystemCd()!=null && obsvalueDT.getAltCdSystemCd().equalsIgnoreCase(EdxELRConstant.ELR_LOCAL_CD))
                            obsvalueDT.setAltCdSystemDescTxt(EdxELRConstant.ELR_LOCAL_DESC);



                        obsvalueDT.setObservationUid(observationVO.getTheObservationDT().getObservationUid());

                        if(observationVO.getTheObsValueCodedDTCollection()==null) {
                            observationVO.setTheObsValueCodedDTCollection(new ArrayList<>());
                        }
                        observationVO.getTheObsValueCodedDTCollection().add(obsvalueDT);
                    }
                } else if (type.equals(EdxELRConstant.ELR_STUCTURED_NUMERIC_CD)) {
                    ObsValueNumericDT obsValueNumericDT = new ObsValueNumericDT();
                    obsValueNumericDT.setObsValueNumericSeq(1);
                    StringTokenizer st = new StringTokenizer(text, "^");
                    obsValueNumericDT.setItNew(true);
                    obsValueNumericDT.setItDirty(false);
                    int i = 0;
                    if (text.indexOf("^") == 0) {
                        i = 1;
                    }
                    while (st.hasMoreTokens()) {
                        i++;
                        String token = st.nextToken();
                        if (i == 1) {
                            if (token != null && token.equals("&lt;"))
                                obsValueNumericDT.setComparatorCd1("<");
                            else if (token != null && token.equals("&gt;"))
                                obsValueNumericDT.setComparatorCd1(">");
                            else
                                obsValueNumericDT.setComparatorCd1(token);
                        } else if (i == 2) {
                            obsValueNumericDT.setNumericValue1(new BigDecimal(token));
                        }
                        else if (i == 3) {
                            obsValueNumericDT.setSeparatorCd(token);
                        }
                        else if (i == 4) {
                            obsValueNumericDT.setNumericValue2(new BigDecimal(token));
                        }
                    }
                    if (cEUnit != null)
                        obsValueNumericDT.setNumericUnitCd(cEUnit.getHL7Identifier());
                    obsValueNumericDT.setObservationUid(observationVO.getTheObservationDT().getObservationUid());
                    if (observationVO.getTheObsValueNumericDTCollection() == null) {
                        observationVO.setTheObsValueNumericDTCollection(new ArrayList<>());
                    }
                    observationVO.getTheObsValueNumericDTCollection().add(obsValueNumericDT);

                }

                else if (type.equals(EdxELRConstant.ELR_NUMERIC_CD)) {
                    ObsValueNumericDT obsValueNumericDT = new ObsValueNumericDT();
                    obsValueNumericDT.setObsValueNumericSeq(1);
                    obsValueNumericDT.setItNew(true);
                    obsValueNumericDT.setItDirty(false);

                    obsValueNumericDT.setNumericValue1(new BigDecimal(text));


                    if (cEUnit != null) {
                        obsValueNumericDT.setNumericUnitCd(cEUnit.getHL7Identifier());
                    }
                    obsValueNumericDT.setObservationUid(observationVO.getTheObservationDT().getObservationUid());
                    if (observationVO.getTheObsValueNumericDTCollection() == null) {
                        observationVO.setTheObsValueNumericDTCollection(new ArrayList<>());
                    }
                    observationVO.getTheObsValueNumericDTCollection().add(obsValueNumericDT);

                } else if (type.equals(EdxELRConstant.ELR_STRING_CD) || type.equals(EdxELRConstant.ELR_TEXT_CD)
                        || type.equals(EdxELRConstant.ELR_TEXT_DT) || type.equals(EdxELRConstant.ELR_TEXT_TS)) {
                    ObsValueTxtDT obsValueTxtDT = new ObsValueTxtDT();
                    StringTokenizer st = new StringTokenizer(text, "^");
                    int i;
                    if (observationVO.getTheObsValueTxtDTCollection() == null)
                        observationVO.setTheObsValueTxtDTCollection(new ArrayList<Object>());

                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        i = observationVO.getTheObsValueTxtDTCollection().size() + 1;
                        obsValueTxtDT.setValueTxt(token);
                        obsValueTxtDT.setObsValueTxtSeq(i++);
                        obsValueTxtDT.setTxtTypeCd(EdxELRConstant.ELR_OFFICE_CD);
                        obsValueTxtDT.setObservationUid(observationVO.getTheObservationDT().getObservationUid());
                        obsValueTxtDT.setItNew(true);
                        obsValueTxtDT.setItDirty(false);

                    }
                    observationVO.getTheObsValueTxtDTCollection().add(obsValueTxtDT);

                } else {
                    edxLabInformationDto.setUnexpectedResultType(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.UNEXPECTED_RESULT_TYPE);
                }
            }
        } catch (Exception e) {
            logger.error("ObservationResultRequest.formatValue Exception thrown while observation value. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.formatValue for text:\""+text+"\" and for type:\""+ type+"\"."+e.getMessage());
        }

    }


    private static ObservationVO getObsReqNotes(List<HL7NTEType> noteArray, ObservationVO observationVO) throws DataProcessingException {
        try {
            for (HL7NTEType notes : noteArray) {
                if (notes.getHL7Comment() != null && notes.getHL7Comment().size() > 0) {
                    for (int j = 0; j < notes.getHL7Comment().size(); j++) {
                        String note = notes.getHL7Comment().get(j);
                        ObsValueTxtDT obsValueTxtDT = new ObsValueTxtDT();
                        obsValueTxtDT.setItNew(true);
                        obsValueTxtDT.setItDirty(false);
                        obsValueTxtDT.setObservationUid(observationVO.getTheObservationDT().getObservationUid());
                        obsValueTxtDT.setTxtTypeCd(EdxELRConstant.ELR_OBX_COMMENT_TYPE);

                        obsValueTxtDT.setValueTxt(note);
                        if (observationVO.getTheObsValueTxtDTCollection() == null)
                            observationVO.setTheObsValueTxtDTCollection(new ArrayList<Object>());
                        int seq = observationVO.getTheObsValueTxtDTCollection().size();
                        obsValueTxtDT.setObsValueTxtSeq(++seq);
                        observationVO.getTheObsValueTxtDTCollection().add(obsValueTxtDT);
                    }
                } else {
                    ObsValueTxtDT obsValueTxtDT = new ObsValueTxtDT();
                    obsValueTxtDT.setItNew(true);
                    obsValueTxtDT.setItDirty(false);
                    obsValueTxtDT.setValueTxt("\r");
                    obsValueTxtDT.setObservationUid(observationVO.getTheObservationDT().getObservationUid());
                    obsValueTxtDT.setTxtTypeCd(EdxELRConstant.ELR_OBX_COMMENT_TYPE);

                    if (observationVO.getTheObsValueTxtDTCollection() == null)
                        observationVO.setTheObsValueTxtDTCollection(new ArrayList<Object>());
                    int seq = observationVO.getTheObsValueTxtDTCollection().size();
                    obsValueTxtDT.setObsValueTxtSeq(++seq);
                    observationVO.getTheObsValueTxtDTCollection().add(obsValueTxtDT);

                }

            }
        } catch (Exception e) {
            logger.error("ObservationResultRequest.getObsReqNotes Exception thrown while parsing XML document. Please check!!!"+e.getMessage(), e);
            throw new DataProcessingException("Exception thrown at ObservationResultRequest.getObsReqNotes:"+ e.getMessage());

        }
        return observationVO;


    }



}
