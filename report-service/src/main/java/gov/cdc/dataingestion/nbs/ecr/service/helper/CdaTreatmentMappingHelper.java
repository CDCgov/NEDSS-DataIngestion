package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.AttributeMapper;
import gov.cdc.dataingestion.nbs.ecr.model.CdaTreatmentAdministrationMapper;
import gov.cdc.dataingestion.nbs.ecr.model.CdaTreatmentMapper;
import gov.cdc.dataingestion.nbs.ecr.model.treatment.TreatmentDocument;
import gov.cdc.dataingestion.nbs.ecr.model.treatment.TreatmentField;
import gov.cdc.dataingestion.nbs.ecr.model.treatment.TreatmentProviderAndParticipant;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaTreatmentMappingHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedTreatment;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import java.util.Map;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.VALUE_NAME;

public class CdaTreatmentMappingHelper implements ICdaTreatmentMappingHelper {
    ICdaMapHelper cdaMapHelper;
    public CdaTreatmentMappingHelper(ICdaMapHelper cdaMapHelper) {
        this.cdaMapHelper = cdaMapHelper;
    }

    public CdaTreatmentMapper mapToTreatmentTop(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument,
                                                 int treatmentCounter, int componentCounter,
                                                 int treatmentSectionCounter)
            throws EcrCdaXmlException {
        try {
            CdaTreatmentMapper mapper = new CdaTreatmentMapper();
            if(input.getMsgTreatments() != null && !input.getMsgTreatments().isEmpty()) {
                for(int i = 0; i < input.getMsgTreatments().size(); i++) {
                    var treatmentDoc =  mapToTreatmentTopDocCheck( clinicalDocument);
                    clinicalDocument = treatmentDoc.getClinicalDocument();
                    int c = treatmentDoc.getC();



                    if (treatmentCounter < 1) {
                        treatmentCounter++;
                        componentCounter++;

                        mapToTreatmentTopHasNoCounter( clinicalDocument,  c);
                    }

                    int cTreatment = 0;
                    if ( clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length == 0) {
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry().addNewSubstanceAdministration();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(0).getSubstanceAdministration().addNewStatusCode();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(0).getSubstanceAdministration().addNewEntryRelationship();
                    } else {
                        cTreatment = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray().length;
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewEntry().addNewSubstanceAdministration();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().addNewStatusCode();
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().addNewEntryRelationship();
                    }

                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().getStatusCode().setCode("active");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().getEntryRelationshipArray(0).setTypeCode(XActRelationshipEntryRelationship.COMP);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().setClassCode("SBADM");
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().setMoodCode(XDocumentSubstanceMood.EVN);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration().setNegationInd(false);

                    var o1 = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).getSubstanceAdministration();
                    var o2 = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getText();
                    CdaTreatmentAdministrationMapper mappedVal = mapToTreatment(input.getMsgTreatments().get(0),
                            o1,
                            o2);
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getEntryArray(cTreatment).setSubstanceAdministration(mappedVal.getAdministration());
                    clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().setText(mappedVal.getText());
                    treatmentSectionCounter= treatmentSectionCounter+1;
                }
            }

            mapper.setClinicalDocument(clinicalDocument);
            mapper.setTreatmentCounter(treatmentCounter);
            mapper.setComponentCounter(componentCounter);
            mapper.setTreatmentSectionCounter(treatmentSectionCounter);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }

    private void mapToTreatmentTopHasNoCounter(POCDMT000040ClinicalDocument1 clinicalDocument, int c) throws EcrCdaXmlException {
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("55753-8");
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem(CODE_SYSTEM);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName(CODE_SYSTEM_NAME);
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Treatment Information");

            if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
            }
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle().set(cdaMapHelper.mapToStringData("TREATMENT INFORMATION"));

            if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getText() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewText();
            }

    }
    private TreatmentDocument mapToTreatmentTopDocCheck( POCDMT000040ClinicalDocument1 clinicalDocument) {
        int c = 0;
        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
        } else {
            c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
            clinicalDocument.getComponent().getStructuredBody().addNewComponent();
        }
        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).addNewSection();
            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
        } else {
            if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection() == null) {
                clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewCode();
            }
        }

        TreatmentDocument doc = new TreatmentDocument();
        doc.setClinicalDocument(clinicalDocument);
        doc.setC(c);
        return doc;
    }


    private CdaTreatmentAdministrationMapper mapToTreatment(
            EcrSelectedTreatment input, POCDMT000040SubstanceAdministration output,
            StrucDocText list) throws EcrCdaXmlException {
        String treatmentUid="";
        String TRT_TREATMENT_DT="";
        String TRT_FREQUENCY_AMT_CD="";
        String TRT_DOSAGE_UNIT_CD="";
        String TRT_DURATION_AMT="";
        String TRT_DURATION_UNIT_CD="";

        String treatmentName ="";
        String treatmentNameQuestion ="";

        String customTreatment="";


        for (Map.Entry<String, Object> entry : input.getMsgTreatment().getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = this.cdaMapHelper.getValueFromMap(entry);

            TreatmentField param = new TreatmentField();
            param.setTreatmentUid(treatmentUid);
            param.setTrtTreatmentDt(TRT_TREATMENT_DT);
            param.setTrtFrequencyAmtCd(TRT_FREQUENCY_AMT_CD);
            param.setTrtDosageUnitCd(TRT_DOSAGE_UNIT_CD);
            param.setTrtDurationAmt(TRT_DURATION_AMT);
            param.setTrtDurationUnitCd(TRT_DURATION_UNIT_CD);
            param.setTreatmentName(treatmentName);
            param.setTreatmentNameQuestion(treatmentNameQuestion);
            param.setCustomTreatment(customTreatment);
            var treatmentField = mapToTreatmentFieldCheck( input,  output,
                     name,
                     value,
                     param);
            treatmentUid = treatmentField.getTreatmentUid();
            TRT_TREATMENT_DT = treatmentField.getTrtTreatmentDt();
            TRT_FREQUENCY_AMT_CD = treatmentField.getTrtFrequencyAmtCd();
            TRT_DOSAGE_UNIT_CD = treatmentField.getTrtDosageUnitCd();
            TRT_DURATION_AMT = treatmentField.getTrtDurationAmt();
            TRT_DURATION_UNIT_CD = treatmentField.getTrtDurationUnitCd();
            treatmentName = treatmentField.getTreatmentName();
            treatmentNameQuestion = treatmentField.getTreatmentNameQuestion();
            customTreatment = treatmentField.getCustomTreatment();
            output = treatmentField.getOutput();

        }


        list =  mapToTreatmentCustomTreat( customTreatment,
                 list);


         mapToTreatmentTreatName( output,
                 treatmentName,
                 customTreatment,
                 treatmentNameQuestion);
         mapToTreatmentTreatDt( output,
                 TRT_TREATMENT_DT,
                 TRT_DURATION_AMT,
                 TRT_DURATION_UNIT_CD);
         mapToTreatmentTreatFrequency( output,
                 TRT_FREQUENCY_AMT_CD );

        int performerCounter=0;

        if (input.getMsgTreatmentOrganizations().size() > 0 ||  input.getMsgTreatmentProviders().size() > 0) {
            var model = mapToTreatmentProviderAndParticipant( output,
                     input,
                     performerCounter);

            output = model.getOutput();
        }

        CdaTreatmentAdministrationMapper mapper = new CdaTreatmentAdministrationMapper();
        mapper.setAdministration(output);
        mapper.setText(list);
        return mapper;
    }

    private TreatmentProviderAndParticipant mapToTreatmentProviderAndParticipant(POCDMT000040SubstanceAdministration output,
                                                  EcrSelectedTreatment input,
                                                  int performerCounter
                                                  ) throws EcrCdaXmlException {
        for(int i = 0; i < input.getMsgTreatmentOrganizations().size(); i++) {
            int c = 0;
            if (output.getParticipantArray().length == 0) {
                output.addNewParticipant().addNewParticipantRole().addNewId();
            } else {
                c = output.getParticipantArray().length;
                output.addNewParticipant().addNewParticipantRole().addNewId();
            }
            var ot = output.getParticipantArray(c);
            var mappedVal = this.cdaMapHelper.mapToORG( input.getMsgTreatmentOrganizations().get(i), ot);
            output.setParticipantArray(c, mappedVal);
            output.getParticipantArray(c).getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR_ORG");
            performerCounter++;
        }

        for(int i = 0; i < input.getMsgTreatmentProviders().size(); i++) {
            int c = 0;
            if (output.getParticipantArray().length == 0) {
                output.addNewParticipant().addNewParticipantRole().addNewId();
            } else {
                c = output.getParticipantArray().length;
                output.addNewParticipant().addNewParticipantRole().addNewId();
            }

            var ot = output.getParticipantArray(c);
            var mappedVal = this.cdaMapHelper.mapToPSN(input.getMsgTreatmentProviders().get(i), ot);
            output.setParticipantArray(c, mappedVal);
            output.getParticipantArray(c).getParticipantRole().getIdArray(0).setAssigningAuthorityName("LR_ORG");
            performerCounter++;
        }


        TreatmentProviderAndParticipant model = new TreatmentProviderAndParticipant();
        model.setOutput(output);
        return model;
    }

    private void mapToTreatmentTreatFrequency(POCDMT000040SubstanceAdministration output,
                                              String TRT_FREQUENCY_AMT_CD ) {
        if (!TRT_FREQUENCY_AMT_CD.isEmpty()) {
            int c = 0;
            if (output.getEffectiveTimeArray().length == 0) {
                output.addNewEffectiveTime();
            } else {
                c = output.getEffectiveTimeArray().length;
                output.addNewEffectiveTime();

            }

            var element = output.getEffectiveTimeArray(c);

            XmlObject xmlOb = XmlObject.Factory.newInstance();

            XmlCursor cursor = xmlOb.newCursor();
            cursor.toEndDoc();  // Move to the root element
            cursor.beginElement("period");

            AttributeMapper res = mapToAttributes(TRT_FREQUENCY_AMT_CD);
            if (cursor.toFirstAttribute()) {
                cursor.insertAttributeWithValue(VALUE_NAME, res.getAttribute1());
            }
            if (cursor.toNextAttribute()) {
                cursor.insertAttributeWithValue("unit", res.getAttribute2());
            }
            cursor.dispose();

            element.set(xmlOb);
            XmlCursor parentCursor = element.newCursor();
            parentCursor.toFirstChild();

            parentCursor.insertAttributeWithValue("type", "PIVL_TS");
            parentCursor.dispose();

            output.setEffectiveTimeArray(c, element);
        }
    }

    private void mapToTreatmentTreatDt(POCDMT000040SubstanceAdministration output,
                                       String TRT_TREATMENT_DT,
                                       String TRT_DURATION_AMT,
                                       String TRT_DURATION_UNIT_CD) throws EcrCdaXmlException {
        if(!TRT_TREATMENT_DT.isEmpty()){
            if (output.getEffectiveTimeArray().length == 0) {
                output.addNewEffectiveTime();
            }
            var lowElement = output.getEffectiveTimeArray(0);

            XmlObject xmlOb = XmlObject.Factory.newInstance();
            XmlCursor cursor = xmlOb.newCursor();
            cursor.toEndDoc();  // Move to the root element
            cursor.beginElement("low");
            cursor.insertAttributeWithValue(VALUE_NAME,  cdaMapHelper.mapToTsType(TRT_TREATMENT_DT).getValue());

            if (TRT_DURATION_AMT != null && !TRT_DURATION_AMT.isEmpty() && TRT_DURATION_UNIT_CD != null && !TRT_DURATION_UNIT_CD.isEmpty()) {
                cursor.toEndDoc();
                cursor.beginElement("width");
                cursor.insertAttributeWithValue(VALUE_NAME, TRT_DURATION_AMT);
                cursor.insertAttributeWithValue("unit", TRT_DURATION_UNIT_CD);
            }

            cursor.dispose();
            lowElement.set(xmlOb);

            XmlCursor parentCursor = lowElement.newCursor();
            parentCursor.toFirstChild();
            parentCursor.insertAttributeWithValue("type", "IVL_TS");
            parentCursor.dispose();

            output.setEffectiveTimeArray(0, lowElement);
        }
    }

    private void mapToTreatmentTreatName(POCDMT000040SubstanceAdministration output,
                                         String treatmentName,
                                         String customTreatment,
                                         String treatmentNameQuestion) throws EcrCdaXmlException {
        if (!treatmentName.isEmpty()) {
            if  (output.getConsumable() == null) {
                output.addNewConsumable().addNewManufacturedProduct().addNewManufacturedLabeledDrug().addNewCode();
            }
            var ce = this.cdaMapHelper.mapToCEAnswerType(
                    treatmentName,
                    treatmentNameQuestion
            );
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().setCode(ce);

        } else {
            if  (output.getConsumable() == null) {
                output.addNewConsumable().addNewManufacturedProduct().addNewManufacturedLabeledDrug().addNewCode();
                output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().addNewName();
            }
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getCode().setNullFlavor("OTH");
            output.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getName().set(cdaMapHelper.mapToCData(customTreatment));
        }
    }

    private StrucDocText mapToTreatmentCustomTreat(String customTreatment,
                                           StrucDocText list) throws EcrCdaXmlException {
        if(!customTreatment.isEmpty()){
            int c = 0;
            if (list == null) {
                list = StrucDocText.Factory.newInstance();
                list.addNewList();
            } else {
                if ( list.getListArray().length == 0){
                    list.addNewList();
                } else {
                    c = list.getListArray().length;
                    list.addNewList();
                }
            }
            list.getListArray(c).addNewItem();
            list.getListArray(c).addNewCaption();
            StrucDocItem item = StrucDocItem.Factory.newInstance();
            XmlCursor cursor = item.newCursor();
            cursor.setTextValue(CDATA + customTreatment + CDATA);
            cursor.dispose();
            list.getListArray(c).setItemArray(0, item);
            list.getListArray(c).getCaption().set(cdaMapHelper.mapToCData("CDA Treatment Information Section"));
        }

        return list;
    }

    private void mapToTreatmentFieldCheckDoseAmt(EcrSelectedTreatment input,
                                                 POCDMT000040SubstanceAdministration output) {
        String dosageSt = input.getMsgTreatment().getTrtDosageAmt().toString();
        if(!dosageSt.isEmpty()) {
            if (output.getDoseQuantity() == null) {
                output.addNewDoseQuantity();
            }
            output.getDoseQuantity().setValue(input.getMsgTreatment().getTrtDosageAmt());
        }
    }

    private void mapToTreatmentFieldCheckLocalId(EcrSelectedTreatment input,
                                                 POCDMT000040SubstanceAdministration output) {
        int c = 0;
        if (output.getIdArray().length == 0) {
            output.addNewId();
        }else {
            c = output.getIdArray().length;
            output.addNewId();
        }
        output.getIdArray(c).setRoot(ID_ROOT);
        output.getIdArray(c).setAssigningAuthorityName("LR");
        output.getIdArray(c).setExtension(input.getMsgTreatment().getTrtLocalId());
    }

    private TreatmentField mapToTreatmentFieldCheck(EcrSelectedTreatment input, POCDMT000040SubstanceAdministration output,
                                          String name,
                                          String value,
                                          TreatmentField param) throws EcrCdaXmlException {
        String treatmentUid = param.getTreatmentUid();
        String TRT_TREATMENT_DT = param.getTrtTreatmentDt();
        String TRT_FREQUENCY_AMT_CD = param.getTrtFrequencyAmtCd();
        String TRT_DOSAGE_UNIT_CD = param.getTrtDosageUnitCd();
        String TRT_DURATION_AMT = param.getTrtDurationAmt();
        String TRT_DURATION_UNIT_CD = param.getTrtDurationUnitCd();
        String treatmentName = param.getTreatmentName();
        String treatmentNameQuestion = param.getTreatmentNameQuestion();
        String customTreatment = param.getCustomTreatment();


        if(name.equals("trtTreatmentDt")  && value != null && input.getMsgTreatment().getTrtTreatmentDt() != null) {
            TRT_TREATMENT_DT= input.getMsgTreatment().getTrtTreatmentDt().toString();
        }
        if(name.equals("trtFrequencyAmtCd")  && value != null && input.getMsgTreatment().getTrtFrequencyAmtCd() != null && !input.getMsgTreatment().getTrtFrequencyAmtCd().isEmpty()) {
            TRT_FREQUENCY_AMT_CD= input.getMsgTreatment().getTrtFrequencyAmtCd();
        }
        if(name.equals("trtDosageUnitCd") && value != null && input.getMsgTreatment().getTrtDosageUnitCd() != null && !input.getMsgTreatment().getTrtDosageUnitCd().isEmpty()) {
            TRT_DOSAGE_UNIT_CD= input.getMsgTreatment().getTrtDosageUnitCd();
            output.getDoseQuantity().setUnit(TRT_DOSAGE_UNIT_CD);
        }
        if(name.equals("trtDosageAmt") && value != null && input.getMsgTreatment().getTrtDosageAmt() != null) {
            mapToTreatmentFieldCheckDoseAmt( input,
                     output);
        }
        if(name.equals("trtDrugCd") && value != null && input.getMsgTreatment().getTrtDrugCd() != null && !input.getMsgTreatment().getTrtDrugCd().isEmpty()) {
            treatmentNameQuestion = this.cdaMapHelper.mapToQuestionId("TRT_DRUG_CD");
            treatmentName = input.getMsgTreatment().getTrtDrugCd();
        }
        if(name.equals("trtLocalId")  && value != null&& input.getMsgTreatment().getTrtLocalId() != null && !input.getMsgTreatment().getTrtLocalId().isEmpty()) {
            mapToTreatmentFieldCheckLocalId( input,
                     output);
            treatmentUid=input.getMsgTreatment().getTrtLocalId();
        }
        if(name.equals("trtCustomTreatmentTxt")  && value != null && input.getMsgTreatment().getTrtCustomTreatmentTxt() != null && !input.getMsgTreatment().getTrtCustomTreatmentTxt().isEmpty()) {
            customTreatment= input.getMsgTreatment().getTrtCustomTreatmentTxt();
        }
        if(name.equals("trtDurationAmt") && value != null && input.getMsgTreatment().getTrtDurationAmt() != null) {
            TRT_DURATION_AMT = input.getMsgTreatment().getTrtDurationAmt().toString();
        }
        if(name.equals("trtDurationUnitCd") && value != null && input.getMsgTreatment().getTrtDurationUnitCd() != null && !input.getMsgTreatment().getTrtDurationUnitCd().isEmpty()) {
            TRT_DURATION_UNIT_CD = input.getMsgTreatment().getTrtDurationUnitCd();
        }

        param.setTreatmentUid(treatmentUid);
        param.setTrtTreatmentDt(TRT_TREATMENT_DT);
        param.setTrtFrequencyAmtCd(TRT_FREQUENCY_AMT_CD);
        param.setTrtDosageUnitCd(TRT_DOSAGE_UNIT_CD);
        param.setTrtDurationAmt(TRT_DURATION_AMT);
        param.setTrtDurationUnitCd(TRT_DURATION_UNIT_CD);
        param.setTreatmentName(treatmentName);
        param.setTreatmentNameQuestion(treatmentNameQuestion);
        param.setCustomTreatment(customTreatment);
        param.setOutput(output);
        return param;

    }

    private AttributeMapper mapToAttributes(String input) {
        AttributeMapper model = new AttributeMapper();
        if (!input.isEmpty()) {
            switch (input) {
                case "BID", "Q12H" -> {
                    model.setAttribute1("12");
                    model.setAttribute2("h");
                }
                case "5ID" -> {
                    model.setAttribute1("4.5");
                    model.setAttribute2("h");
                }
                case "TID", "Q8H" -> {
                    model.setAttribute1("8");
                    model.setAttribute2("h");
                }
                case "QW" -> {
                    model.setAttribute1("1");
                    model.setAttribute2("wk");
                }
                case "QID", "Q6H" -> {
                    model.setAttribute1("6");
                    model.setAttribute2("h");
                }
                case "QD" -> {
                    model.setAttribute1("1");
                    model.setAttribute2("d");
                }
                case "Q5D" -> {
                    model.setAttribute1("1.4");
                    model.setAttribute2("d");
                }
                case "Q4H" -> {
                    model.setAttribute1("4");
                    model.setAttribute2("h");
                }
                case "Q3D" -> {
                    model.setAttribute1("3.5");
                    model.setAttribute2("d");
                }
                case "Once" -> {
                    model.setAttribute1("24");
                    model.setAttribute2("h");
                }
                default -> {
                    model.setAttribute1("");
                    model.setAttribute2("");
                }
            }

        }
        return model;
    }


}
