package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaOrganizationMapper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaOrgMappingHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.*;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.ACT_CODE_DISPLAY_NAME;

public class CdaOrgMappingHelper implements ICdaOrgMappingHelper {
    ICdaMapHelper cdaMapHelper;
    public CdaOrgMappingHelper(ICdaMapHelper cdaMapHelper) {
        this.cdaMapHelper = cdaMapHelper;
    }


    public CdaOrganizationMapper mapToOrganizationTop(EcrSelectedRecord input, POCDMT000040Section clinicalDocument,
                                                       int performerComponentCounter, int componentCounter,
                                                       int performerSectionCounter)
            throws EcrCdaXmlException {

        try {
            CdaOrganizationMapper mapper = new CdaOrganizationMapper();
            if(input.getMsgOrganizations()!= null && !input.getMsgOrganizations().isEmpty()) {
                for(int i = 0; i < input.getMsgOrganizations().size(); i++) {
                    if (clinicalDocument.getCode() == null) {
                        clinicalDocument.addNewCode();
                    }

                    if (clinicalDocument.getTitle() == null) {
                        clinicalDocument.addNewTitle();
                    }

                    if (performerComponentCounter < 1) {
                        componentCounter++;
                        performerComponentCounter = componentCounter;
                        clinicalDocument.getCode().setCode(CODE);
                        clinicalDocument.getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                        clinicalDocument.getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                        clinicalDocument.getCode().setDisplayName(CODE_DISPLAY_NAME);
                        clinicalDocument.getTitle().set(cdaMapHelper.mapToStringData(CLINICAL_TITLE));


                    }
                    performerSectionCounter = clinicalDocument.getEntryArray().length;
                    if ( clinicalDocument.getEntryArray().length == 0) {
                        clinicalDocument.addNewEntry();
                        performerSectionCounter = 0;
                    }
                    else {
                        performerSectionCounter = clinicalDocument.getEntryArray().length;
                        clinicalDocument.addNewEntry();
                    }


                    if (clinicalDocument.getEntryArray(performerSectionCounter).getAct() == null) {
                        clinicalDocument.getEntryArray(performerSectionCounter).addNewAct();
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                    } else {
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewParticipant();
                    }

                    POCDMT000040Participant2 out = clinicalDocument.getEntryArray(performerSectionCounter).getAct().getParticipantArray(0);
                    POCDMT000040Participant2 output = this.cdaMapHelper.mapToORG(input.getMsgOrganizations().get(i), out);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().setParticipantArray(0, output);

                    clinicalDocument.getEntryArray(performerSectionCounter).setTypeCode(XActRelationshipEntry.COMP);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().setClassCode(XActClassDocumentEntryAct.ACT);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().setMoodCode(XDocumentActMood.EVN);

                    if (clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode() == null){
                        clinicalDocument.getEntryArray(performerSectionCounter).getAct().addNewCode();
                    }

                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCode("ORG");
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                    clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName(ACT_CODE_DISPLAY_NAME);

                }
            }
            mapper.setClinicalSection(clinicalDocument);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setPerformerSectionCounter(performerSectionCounter);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }

}
