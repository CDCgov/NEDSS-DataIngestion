package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaOrganizationMapper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaOrgMappingHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.nedss.phdc.cda.POCDMT000040Participant2;
import gov.cdc.nedss.phdc.cda.POCDMT000040Section;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class CdaOrgMappingHelper implements ICdaOrgMappingHelper {
    ICdaMapHelper cdaMapHelper;
    public CdaOrgMappingHelper(ICdaMapHelper cdaMapHelper) {
        this.cdaMapHelper = cdaMapHelper;
    }


    public CdaOrganizationMapper mapToOrganizationTop(EcrSelectedRecord input, POCDMT000040Section clinicalDocument,
                                                       int performerComponentCounter, int componentCounter,
                                                       int performerSectionCounter)
            throws EcrCdaXmlException {
            CdaOrganizationMapper mapper = new CdaOrganizationMapper();
            if(input.getMsgOrganizations()!= null && !input.getMsgOrganizations().isEmpty()) {
                for(int i = 0; i < input.getMsgOrganizations().size(); i++) {
                    mapToOrganizationTopDocumentCheck(clinicalDocument);

                    mapToOrganizationTopFieldMap(
                             clinicalDocument,
                             performerComponentCounter,
                             input,
                             i);
                }
            }
            mapper.setClinicalSection(clinicalDocument);
            mapper.setPerformerComponentCounter(performerComponentCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setPerformerSectionCounter(performerSectionCounter);
            return mapper;


    }
    private void mapToOrganizationTopFieldMap(
            POCDMT000040Section clinicalDocument,
            int performerComponentCounter,
            EcrSelectedRecord input,
            int i) throws EcrCdaXmlException {

        var model = this.cdaMapHelper.mapOrgPlaceDocCommonField( clinicalDocument, performerComponentCounter);
        clinicalDocument = model.getClinicalDocument();
        int performerSectionCounter = model.getPerformerSectionCounter(); // NOSONAR
        POCDMT000040Participant2 out = model.getOut();


        POCDMT000040Participant2 output = this.cdaMapHelper.mapToORG(input.getMsgOrganizations().get(i), out);

        clinicalDocument = this.cdaMapHelper.mapOrgPlaceProviderActCommonField( clinicalDocument,
                performerSectionCounter,
                output);

        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCode("ORG");
        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
        clinicalDocument.getEntryArray(performerSectionCounter).getAct().getCode().setDisplayName(ACT_CODE_DISPLAY_NAME);

    }

    private void mapToOrganizationTopDocumentCheck(POCDMT000040Section clinicalDocument) {
        if (clinicalDocument.getCode() == null) {
            clinicalDocument.addNewCode();
        }

        if (clinicalDocument.getTitle() == null) {
            clinicalDocument.addNewTitle();
        }

    }

}
