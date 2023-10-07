package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.nbs.ecr.model.cases.CdaCaseComponent;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;

public class CdaCaseMappingHelper {

    public static POCDMT000040ClinicalDocument1 checkCaseStructComponent(
            POCDMT000040ClinicalDocument1 clinicalDocument
    ) {
        if (clinicalDocument.getComponent() == null) {
            clinicalDocument.addNewComponent().addNewStructuredBody().addNewComponent();

        }
        else {
            if (!clinicalDocument.getComponent().isSetStructuredBody()) {
                clinicalDocument.getComponent().addNewStructuredBody();
            }
            else {
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                }
            }
        }
        return clinicalDocument;
    }

    public static CdaCaseComponent checkCaseStructComponentWithSectionAndIndex(
            POCDMT000040ClinicalDocument1 clinicalDocument,
            int c
    ) {
        CdaCaseComponent caseComponent = new CdaCaseComponent();
        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
        } else {
            c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
        }


        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection().addNewId();
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
        }
        else {
            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getId() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewId();
            }
            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
            }
            if( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
            }
        }

        caseComponent.setComponentIndex(c);
        caseComponent.setClinicalDocument(clinicalDocument);
        return caseComponent;
    }
}
