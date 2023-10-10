package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaInterviewMapper;
import gov.cdc.dataingestion.nbs.ecr.model.InterviewAnswerMapper;
import gov.cdc.dataingestion.nbs.ecr.model.InterviewAnswerMultiMapper;
import gov.cdc.dataingestion.nbs.ecr.model.interview.*;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaInterviewMappingHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedInterview;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgInterviewAnswerDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgInterviewAnswerRepeatDto;
import gov.cdc.nedss.phdc.cda.*;
import org.apache.xmlbeans.XmlCursor;

import javax.xml.namespace.QName;
import java.util.Map;

import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.*;
import static gov.cdc.dataingestion.nbs.ecr.constant.CdaConstantValue.CHANGE;

public class CdaInterviewMappingHelper implements ICdaInterviewMappingHelper {
    ICdaMapHelper cdaMapHelper;
    public CdaInterviewMappingHelper(ICdaMapHelper cdaMapHelper) {
        this.cdaMapHelper = cdaMapHelper;
    }


    public CdaInterviewMapper mapToInterviewTop(EcrSelectedRecord input, POCDMT000040ClinicalDocument1 clinicalDocument,
                                                 int interviewCounter, int componentCounter)
            throws EcrCdaXmlException {
        try {
            CdaInterviewMapper mapper = new CdaInterviewMapper();
            if(input.getMsgInterviews() != null && !input.getMsgInterviews().isEmpty()) {
                for(int i = 0; i < input.getMsgInterviews().size(); i++) {
                    var interviewTop = mapToInterviewTopFieldCheck(clinicalDocument);
                    clinicalDocument = interviewTop.getClinicalDocument();
                    int c = interviewTop.getC();

                    if (interviewCounter < 1) {
                        interviewCounter = componentCounter + 1;
                        componentCounter++;

                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCode("IXS");
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getCode().setDisplayName("Interviews");

                        if (clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle() == null) {
                            clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().addNewTitle();
                        }
                        clinicalDocument.getComponent().getStructuredBody().getComponentArray(c).getSection().getTitle().set(cdaMapHelper.mapToStringData("INTERVIEW SECTION"));
                    }

                    POCDMT000040Component3 ot = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c);

                    POCDMT000040Component3 output = mapToInterview(input.getMsgInterviews().get(i), ot);
                    clinicalDocument.getComponent().getStructuredBody().setComponentArray(c, output);
                }
            }

            mapper.setClinicalDocument(clinicalDocument);
            mapper.setInterviewCounter(interviewCounter);
            mapper.setComponentCounter(componentCounter);
            return mapper;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }

    }

    private InterviewTopField mapToInterviewTopFieldCheck(POCDMT000040ClinicalDocument1 clinicalDocument) {
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

        InterviewTopField model = new InterviewTopField();
        model.setClinicalDocument(clinicalDocument);
        model.setC(c);
        return model;
    }

    private POCDMT000040Component3 mapToInterview(EcrSelectedInterview in, POCDMT000040Component3 out) throws EcrCdaXmlException {

        int sectionEntryCounter= out.getSection().getEntryArray().length;

        if (out.getSection().getEntryArray().length == 0) {
            out.getSection().addNewEntry().addNewEncounter().addNewCode();
            out.getSection().getEntryArray(0).getEncounter().addNewId();
        }

        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().setClassCode("ENC");
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().setMoodCode(XDocumentEncounterMood.EVN);
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getCode().setCode("54520-2");
        out.getSection().getEntryArray(0).getEncounter().getIdArray(0).setRoot("LR");

        int entryCounter= 0;

        if (in.getMsgInterview().getDataMap() == null || in.getMsgInterview().getDataMap().size() == 0) {
            in.getMsgInterview().initDataMap();
        }
        for (Map.Entry<String, Object> entry : in.getMsgInterview().getDataMap().entrySet()) {

            String name = entry.getKey();

            var interviewFieldP1 =  mapToInterviewFieldCheckP1 (in,  out,
                     name,
             sectionEntryCounter,
             entryCounter);

            out = interviewFieldP1.getOut();
            entryCounter = interviewFieldP1.getEntryCounter();
        }

        int questionGroupCounter=0;
        int answerGroupCounter=0;
        String OldRepeatQuestionId=CHANGE;
        int sectionCounter = 0;
        int providerRoleCounter=0;


        if (!in.getMsgInterviewProviders().isEmpty() || !in.getMsgInterviewAnswers().isEmpty() || !in.getMsgInterviewAnswerRepeats().isEmpty()) {
            mapToInterviewProvider( in,  out,
             sectionCounter,  providerRoleCounter,
             sectionEntryCounter);

            var interviewAns = mapToInterviewAnswer( in,  out,
             sectionCounter,
             entryCounter
                    );

            out = interviewAns.getOut();

            for(int i = 0; i < in.getMsgInterviewAnswerRepeats().size(); i++) {
                var element = out.getSection().getEntryArray(sectionEntryCounter).getEncounter();
                var mapped = mapToInterviewMultiSelectObservation(in.getMsgInterviewAnswerRepeats().get(i),
                        answerGroupCounter,
                        questionGroupCounter,
                        sectionCounter,
                        OldRepeatQuestionId,
                        element);

                answerGroupCounter = mapped.getAnswerGroupCounter();
                questionGroupCounter = mapped.getQuestionGroupCounter();
                sectionCounter = mapped.getSectionCounter();
                OldRepeatQuestionId = mapped.getQuestionId();
                out.getSection().getEntryArray(sectionEntryCounter).setEncounter(mapped.getComponent());

            }
        }


        return out;
    }


    private InterviewAnswer mapToInterviewAnswer(EcrSelectedInterview in, POCDMT000040Component3 out,
                                      int sectionCounter,
                                      int entryCounter) throws EcrCdaXmlException {
        for(int i = 0; i < in.getMsgInterviewAnswers().size(); i++) {
            var element = out.getSection().getEntryArray(sectionCounter).getEncounter();
            var ot = mapToInterviewObservation(in.getMsgInterviewAnswers().get(i), entryCounter,
                    element );

            entryCounter = ot.getCounter();
            out.getSection().getEntryArray(sectionCounter).setEncounter(ot.getComponent());
        }

        InterviewAnswer model = new InterviewAnswer();
        model.setOut(out);

        return model;
    }

    private void mapToInterviewProvider(EcrSelectedInterview in, POCDMT000040Component3 out,
                                        int sectionCounter, int providerRoleCounter,
                                        int sectionEntryCounter) throws EcrCdaXmlException {
        for(int i = 0; i < in.getMsgInterviewProviders().size(); i++) {
            if ( out.getSection().getEntryArray(sectionCounter).getEncounter().getParticipantArray().length == 0) {
                out.getSection().getEntryArray(sectionCounter).getEncounter().addNewParticipant().addNewParticipantRole().addNewCode();
            } else {
                providerRoleCounter = out.getSection().getEntryArray(sectionCounter).getEncounter().getParticipantArray().length;
                out.getSection().getEntryArray(sectionCounter).getEncounter().addNewParticipant().addNewParticipantRole().addNewCode();
            }

            var element = out.getSection().getEntryArray(sectionCounter).getEncounter().getParticipantArray(providerRoleCounter);

            POCDMT000040Participant2 ot = this.cdaMapHelper.mapToPSN(in.getMsgInterviewProviders().get(i), element);

            out.getSection().getEntryArray(sectionCounter).getEncounter().setParticipantArray(providerRoleCounter, ot);
            var element2 = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getParticipantArray(providerRoleCounter)
                    .getParticipantRole().getCode();
            CE ce = cdaMapHelper.mapToCEQuestionType("IXS102", element2);
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getParticipantArray(providerRoleCounter)
                    .getParticipantRole().setCode(ce);
            providerRoleCounter=providerRoleCounter+1;
        }
    }

    private InterviewField mapToInterviewFieldCheckP1 (EcrSelectedInterview in, POCDMT000040Component3 out,
                                             String name,
                                             int sectionEntryCounter,
                                             int entryCounter) throws EcrCdaXmlException {
        if(validateInterviewGeneric(name, in)){
            mapToInterviewFieldCheckP1GenericCheck(out, in, sectionEntryCounter);
        }
        else if (name.equals("ixsLocalId")  && in.getMsgInterview().getIxsLocalId() != null && !in.getMsgInterview().getIxsLocalId().isEmpty()){
            mapToInterviewFieldCheckP1IxsLocalId(out, in, sectionEntryCounter);
        }
        else if (name.equals("ixsStatusCd")  && in.getMsgInterview().getIxsStatusCd() != null && !in.getMsgInterview().getIxsStatusCd().isEmpty()){
             mapToInterviewFieldCheckP1StatusCd(out, in, sectionEntryCounter);
        }
        else if (name.equals("ixsInterviewDt")  && in.getMsgInterview().getIxsInterviewDt() != null){
            mapToInterviewFieldCheckP1InterviewDt(out, in, sectionEntryCounter);
        }
        else if (name.equals("ixsIntervieweeRoleCd")  && in.getMsgInterview().getIxsIntervieweeRoleCd() != null && !in.getMsgInterview().getIxsIntervieweeRoleCd().isEmpty()){
            String questionCode = this.cdaMapHelper.mapToQuestionId("IXS_INTERVIEWEE_ROLE_CD");

            var interviewRole = mapToInterviewFieldCheckP1InterviewRole(out, sectionEntryCounter);
            int c = interviewRole.getC();
            out = interviewRole.getOut();

            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
            var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
            this.cdaMapHelper.mapToObservation(questionCode, in.getMsgInterview().getIxsIntervieweeRoleCd(), obs);
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
            entryCounter= entryCounter+ 1;
        }
        else if (validateInterviewType(name, in)){
            String questionCode = this.cdaMapHelper.mapToQuestionId("IXS_INTERVIEW_TYPE_CD");

            var interviewType = mapToInterviewFieldCheckP1Type(out, sectionEntryCounter);
            int c = interviewType.getC();
            out = interviewType.getOut();

            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
            var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
            this.cdaMapHelper.mapToObservation(questionCode, in.getMsgInterview().getIxsInterviewTypeCd(), obs);
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
            entryCounter= entryCounter+ 1;

        }
        else if (validateInterviewLoc(name, in)){
            String questionCode = this.cdaMapHelper.mapToQuestionId("IXS_INTERVIEW_LOC_CD");

            var interviewType = mapToInterviewFieldCheckP1Loc(out, sectionEntryCounter);
            int c = interviewType.getC();
            out = interviewType.getOut();

            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setTypeCode(XActRelationshipEntryRelationship.COMP);
            var obs = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).getObservation();
            this.cdaMapHelper.mapToObservation(questionCode, in.getMsgInterview().getIxsInterviewLocCd(), obs);
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray(c).setObservation(obs);
            entryCounter= entryCounter+ 1;
        }
        InterviewField model = new InterviewField();
        model.setEntryCounter(entryCounter);
        model.setOut(out);
        return model;
    }

    private boolean validateInterviewGeneric(String name, EcrSelectedInterview in) {
        return (name.equals("msgContainerUid") && in.getMsgInterview().getMsgContainerUid() != null )
                || (name.equals("ixsAuthorId")  && in.getMsgInterview().getIxsAuthorId() != null)
                || (name.equals("ixsEffectiveTime")  && in.getMsgInterview().getIxsEffectiveTime() != null);
    }

    private boolean validateInterviewType(String name, EcrSelectedInterview in) {
        return name.equals("ixsInterviewTypeCd")  && in.getMsgInterview().getIxsInterviewTypeCd() != null
                && !in.getMsgInterview().getIxsInterviewTypeCd().isEmpty();
    }

    private boolean validateInterviewLoc(String name, EcrSelectedInterview in) {
        return name.equals("ixsInterviewLocCd")  && in.getMsgInterview().getIxsInterviewLocCd() != null
                && !in.getMsgInterview().getIxsInterviewLocCd().isEmpty();
    }

    private InterviewRole mapToInterviewFieldCheckP1Loc(POCDMT000040Component3 out, int sectionEntryCounter) {
        int c = 0;
        if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
        } else {
            c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
        }
        InterviewRole model = new InterviewRole();
        model.setC(c);
        model.setOut(out);
        return model;
    }
    private InterviewRole mapToInterviewFieldCheckP1Type(POCDMT000040Component3 out, int sectionEntryCounter) {
        int c = 0;
        if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
        } else {
            c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
        }
        InterviewRole model = new InterviewRole();
        model.setC(c);
        model.setOut(out);
        return model;
    }

    private InterviewRole mapToInterviewFieldCheckP1InterviewRole(POCDMT000040Component3 out, int sectionEntryCounter) {
        int c = 0;
        if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length == 0) {
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
        } else {
            c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEntryRelationshipArray().length;
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEntryRelationship().addNewObservation();
        }
        InterviewRole model = new InterviewRole();
        model.setC(c);
        model.setOut(out);
        return model;
    }

    private void mapToInterviewFieldCheckP1InterviewDt(POCDMT000040Component3 out, EcrSelectedInterview in, int sectionEntryCounter) throws EcrCdaXmlException {
        var ts = cdaMapHelper.mapToTsType(in.getMsgInterview().getIxsInterviewDt().toString());
        if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEffectiveTime() == null) {
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewEffectiveTime();
        }
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getEffectiveTime().setValue(ts.getValue());
    }

    private void mapToInterviewFieldCheckP1StatusCd(POCDMT000040Component3 out, EcrSelectedInterview in, int sectionEntryCounter) {
        if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getStatusCode() == null) {
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewStatusCode();
        }
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getStatusCode().setCode(in.getMsgInterview().getIxsStatusCd());
    }

    private void mapToInterviewFieldCheckP1IxsLocalId(POCDMT000040Component3 out, EcrSelectedInterview in, int sectionEntryCounter) {
        int c = 0;
        if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray().length == 0) {
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewId();
        } else {
            c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray().length ;
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewId();
        }
        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray(c).setExtension(in.getMsgInterview().getIxsLocalId());
    }

    private void mapToInterviewFieldCheckP1GenericCheck(POCDMT000040Component3 out, EcrSelectedInterview in, int sectionEntryCounter) {
        int c = 0;
        if (out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray().length == 0) {
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewId();
        } else {
            c = out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray().length ;
            out.getSection().getEntryArray(sectionEntryCounter).getEncounter().addNewId();
        }

        out.getSection().getEntryArray(sectionEntryCounter).getEncounter().getIdArray(c).setExtension(in.getMsgInterview().getMsgContainerUid().toString());
    }

    private InterviewAnswerMultiMapper mapToInterviewMultiSelectObservation(EcrMsgInterviewAnswerRepeatDto in,
                                                                            Integer answerGroupCounter,
                                                                            Integer questionGroupCounter,
                                                                            Integer sectionCounter,
                                                                            String questionId,
                                                                            POCDMT000040Encounter out) throws EcrCdaXmlException {
        int componentCounter = 0;
        String dataType="DATE";
        int seqNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;
        String questionIdentifier="";

        InterviewAnswerMultiMapper model = new InterviewAnswerMultiMapper();

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = this.cdaMapHelper.getValueFromMap(entry);

            var param = new InterviewMultiObs();
            param.setAnswerGroupSeqNbr(answerGroupSeqNbr);
            param.setAnswerGroupCounter(answerGroupCounter);
            param.setQuestionGroupSeqNbr(questionGroupSeqNbr);
            param.setQuestionGroupCounter(questionGroupCounter);
            param.setSectionCounter(sectionCounter);
            param.setComponentCounter(componentCounter);
            param.setDataType(dataType);
            param.setSeqNbr(seqNbr);
            var interviewMultiObsP1 = mapToInterviewMultiSelectObservationFieldP1( in,
                     out,
                     name,
                     param
            );
            out = interviewMultiObsP1.getOut();
            answerGroupSeqNbr = interviewMultiObsP1.getAnswerGroupSeqNbr();
            answerGroupCounter = interviewMultiObsP1.getAnswerGroupCounter();
            questionGroupSeqNbr = interviewMultiObsP1.getQuestionGroupSeqNbr();
            questionGroupCounter = interviewMultiObsP1.getQuestionGroupCounter();
            sectionCounter = interviewMultiObsP1.getSectionCounter();
            componentCounter = interviewMultiObsP1.getComponentCounter();
            dataType = interviewMultiObsP1.getDataType();
            seqNbr = interviewMultiObsP1.getSeqNbr();

            var param2 = new InterviewMultiObs(dataType, sectionCounter, componentCounter, seqNbr, questionIdentifier);
            var interviewMultiObsP2 = mapToInterviewMultiSelectObservationFieldP2( in,
                     out,
                     name,
                     value,
                     param2);
            out = interviewMultiObsP2.getOut();
            dataType = interviewMultiObsP2.getDataType();
            sectionCounter = interviewMultiObsP2.getSectionCounter();
            seqNbr = interviewMultiObsP2.getSeqNbr();
            questionIdentifier = interviewMultiObsP2.getQuestionIdentifier();

            var param3 = new InterviewMultiObs(questionIdentifier, questionId, sectionCounter, componentCounter);
            var interviewMultiObsP3 = mapToInterviewMultiSelectObservationFieldP3(
                     out,
                     name,
                     value,
                     param3);

            out = interviewMultiObsP3.getOut();
            questionIdentifier = interviewMultiObsP3.getQuestionIdentifier();
            sectionCounter = interviewMultiObsP3.getSectionCounter();
            componentCounter = interviewMultiObsP3.getComponentCounter();
        }

        model.setAnswerGroupCounter(answerGroupCounter);
        model.setQuestionGroupCounter(questionGroupCounter);
        model.setSectionCounter(sectionCounter);
        model.setQuestionId(questionId);
        model.setComponent(out);

        return model;
    }

    private InterviewMultiObs mapToInterviewMultiSelectObservationFieldP3(
                                                             POCDMT000040Encounter out,
                                                             String name,
                                                             String value,
                                                             InterviewMultiObs param) {
        String questionIdentifier = param.getQuestionIdentifier();
        String questionId = param.getQuestionId();
        int sectionCounter = param.getSectionCounter();
        int componentCounter = param.getComponentCounter();
        switch (name) {
            case COL_QUES_IDENTIFIER -> {
                questionIdentifier = value;
                if (value.equals(questionId)) {
                    // IGNORE
                } else {
                    if (questionId.equals(CHANGE)) {
                        // IGNORE
                    } else {
                        sectionCounter = sectionCounter + 1;
                    }

                    questionId = value;
                }
                if (out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode() == null) {
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().addNewCode();
                }
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setClassCode("OBS");
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCode(value);
            }
            case COL_QUES_CODE_SYSTEM_CD ->
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCodeSystem(value);
            case COL_QUES_CODE_SYSTEM_DESC_TXT ->
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setCodeSystemName(value);
            case COL_QUES_DISPLAY_TXT ->
                    out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getCode().setDisplayName(value);
        }

        param.setQuestionIdentifier(questionIdentifier);
        param.setQuestionId(questionId);
        param.setSectionCounter(sectionCounter);
        param.setComponentCounter(componentCounter);
        param.setOut(out);

        return param;
    }

    private InterviewMultiObs mapToInterviewMultiSelectObservationFieldP2(EcrMsgInterviewAnswerRepeatDto in,
                                                             POCDMT000040Encounter out,
                                                             String name,
                                                             String value,
                                                             InterviewMultiObs param) throws EcrCdaXmlException {
        String dataType = param.getDataType();
        int sectionCounter = param.getComponentCounter();
        int componentCounter = param.getComponentCounter();
        int seqNbr = param.getSeqNbr();
        String questionIdentifier = param.getQuestionIdentifier();
        if(dataType.equalsIgnoreCase(DATA_TYPE_CODE) || dataType.equalsIgnoreCase("CODED_COUNTY")){
            mapToInterviewMultiSelectObservationFieldP2CodedCounty( name,
                     in,
                     out,
             sectionCounter,
             componentCounter,
             seqNbr);
        }
        else if ((dataType.equalsIgnoreCase("TEXT") || dataType.equalsIgnoreCase(DATA_TYPE_NUMERIC)) &&
                name.equals(COL_ANS_TXT)){
            mapToInterviewMultiSelectObservationFieldP2Text( out,
                     value,
                     questionIdentifier,
             componentCounter,
             sectionCounter);
        }
        else if(dataType.equalsIgnoreCase("DATE")){
            mapToInterviewMultiSelectObservationFieldP2Date( name,
                     in,
                     out,
                     value,
             sectionCounter,
             componentCounter);
        }

        param.setDataType(dataType);
        param.setSectionCounter(sectionCounter);
        param.setComponentCounter(componentCounter);
        param.setSeqNbr(seqNbr);
        param.setQuestionIdentifier(questionIdentifier);
        param.setOut(out);
        return param;
    }

    private void mapToInterviewMultiSelectObservationFieldP2Date(String name,
                                                                 EcrMsgInterviewAnswerRepeatDto in,
                                                                 POCDMT000040Encounter out,
                                                                 String value,
                                                                 int sectionCounter,
                                                                 int componentCounter) throws EcrCdaXmlException {
        if(name.equals(COL_ANS_TXT)){
            if (out.getEntryRelationshipArray(sectionCounter).getOrganizer() == null) {
                out.getEntryRelationshipArray(sectionCounter).addNewOrganizer().addNewComponent().addNewObservation().addNewValue();
            }
            var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(0);
            XmlCursor cursor = element.newCursor();
            cursor.toFirstChild();
            cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "TS");
            cursor.setAttributeText(new QName("", value), null);

            String newValue = cdaMapHelper.mapToTsType(in.getAnswerTxt()).toString();
            cursor.setAttributeText(new QName("", value), newValue);

            cursor.dispose();
        }

    }

    private void mapToInterviewMultiSelectObservationFieldP2Text(  POCDMT000040Encounter out,
                                                                   String value,
                                                                   String questionIdentifier,
                                                                   int componentCounter,
                                                                   int sectionCounter) {
        if(questionIdentifier.equalsIgnoreCase("NBS243") ||
                questionIdentifier.equalsIgnoreCase("NBS290")) {

            var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();
            var ot = cdaMapHelper.mapToObservationPlace(value,
                    element);
            out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation) ot);

        }
        else {
            var element = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation();

            var ot = cdaMapHelper.mapToSTValue(value,element);
            out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).setObservation((POCDMT000040Observation)ot);
        }
    }

    private void mapToInterviewMultiSelectObservationFieldP2CodedCounty(String name,
                                                                        EcrMsgInterviewAnswerRepeatDto in,
                                                                        POCDMT000040Encounter out,
                                                                        int sectionCounter,
                                                                        int componentCounter,
                                                                        int seqNbr) {
        CE ce = CE.Factory.newInstance();
        if (name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
            ce.getTranslationArray(0).setCode(in.getAnswerTxt());
        }
        else if (name.equals(COL_ANS_CODE_SYSTEM_CD) && !in.getAnsCodeSystemCd().isEmpty()) {
            ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
        }
        else if (name.equals(COL_ANS_CODE_SYSTEM_DESC_TXT) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
            ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
        }
        else if (name.equals(COL_ANS_DISPLAY_TXT) && !in.getAnsDisplayTxt().isEmpty()) {
            ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
        }
        else if (name.equals(COL_ANS_TO_CODE) && !in.getAnsToCode().isEmpty()) {
            ce.setCode(in.getAnsToCode());
        }
        else if (name.equals(COL_ANS_TO_CODE_SYSTEM_CD) && !in.getAnsToCodeSystemCd().isEmpty()) {
            ce.setCodeSystem(in.getAnsToCodeSystemCd());
        }
        else if (validateMultiSelectObservationFieldP2AnsCodeDesc(name, in)) {
            ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
        }
        else if (validateMultiSelectObservationFieldP2AnsDisplayNm(name, in)) {
            ce.setDisplayName(in.getAnsToDisplayNm());

        }
        out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray(componentCounter).getObservation().getValueArray(seqNbr).set(ce);
    }

    private boolean validateMultiSelectObservationFieldP2AnsCodeDesc(String name, EcrMsgInterviewAnswerRepeatDto in) {
        return name.equals(COL_ANS_TO_CODE_SYSTEM_DESC_TXT) && !in.getAnsToCodeSystemDescTxt().isEmpty();
    }

    private boolean validateMultiSelectObservationFieldP2AnsDisplayNm(String name, EcrMsgInterviewAnswerRepeatDto in) {
        return name.equals(COL_ANS_TO_DISPLAY_NM) && !in.getAnsToDisplayNm().isEmpty();
    }

    private InterviewMultiObs mapToInterviewMultiSelectObservationFieldP1(EcrMsgInterviewAnswerRepeatDto in,
                                                             POCDMT000040Encounter out,
                                                             String name,
                                                             InterviewMultiObs param
                                                             ) {

        int answerGroupSeqNbr = param.getAnswerGroupSeqNbr();
        int answerGroupCounter = param.getAnswerGroupCounter();
        int questionGroupSeqNbr = param.getQuestionGroupSeqNbr();
        int questionGroupCounter = param.getQuestionGroupCounter();
        int sectionCounter = param.getSectionCounter();
        int componentCounter = param.getComponentCounter();
        String dataType = param.getDataType();
        int seqNbr = param.getSeqNbr();

        if(name.equals(COL_QUES_GROUP_SEQ_NBR) && !in.getQuestionGroupSeqNbr().isEmpty()){
            questionGroupSeqNbr= Integer.parseInt(in.getQuestionGroupSeqNbr());
        }
        else if(name.equals(COL_ANS_GROUP_SEQ_NBR) && !in.getAnswerGroupSeqNbr().isEmpty()){
            answerGroupSeqNbr= Integer.parseInt(in.getAnswerGroupSeqNbr());
            if((answerGroupSeqNbr==answerGroupCounter) &&
                    (questionGroupSeqNbr ==questionGroupCounter))
            {
                componentCounter = out.getEntryRelationshipArray(sectionCounter).getOrganizer().getComponentArray().length;
            }
            else
            {
                sectionCounter = out.getEntryRelationshipArray().length - 1;
                questionGroupCounter=questionGroupSeqNbr ;
                answerGroupCounter=answerGroupSeqNbr;

                mapToInterviewMultiSelectObservationFieldP1DocCheckOrg(sectionCounter, out);

                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCode(String.valueOf(questionGroupSeqNbr));
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCode("1234567RPT");
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCodeSystem(CLINICAL_CODE_SYSTEM);
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setCodeSystemName(CLINICAL_CODE_SYSTEM_NAME);
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode().setDisplayName("Generic Repeating Questions Section");

                out.getEntryRelationshipArray(sectionCounter).setTypeCode(XActRelationshipEntryRelationship.COMP);
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().setClassCode(XActClassDocumentEntryOrganizer.CLUSTER);
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().setMoodCode("EVN");
                out.getEntryRelationshipArray(sectionCounter).getOrganizer().getStatusCode().setCode("completed");
                componentCounter=0;

            }
        }

        else if(name.equals(COL_DATA_TYPE) && !in.getDataType().isEmpty() ){
            dataType= in.getDataType();
        }else if(name.equals(COL_SEQ_NBR) && !in.getSeqNbr().isEmpty()){
            seqNbr= Integer.parseInt(in.getSeqNbr()) ;
        }

        param.setOut(out);
        param.setAnswerGroupSeqNbr(answerGroupSeqNbr);
        param.setAnswerGroupCounter(answerGroupCounter);
        param.setQuestionGroupSeqNbr(questionGroupSeqNbr);
        param.setQuestionGroupCounter(questionGroupCounter);
        param.setSectionCounter(sectionCounter);
        param.setComponentCounter(componentCounter);
        param.setDataType(dataType);
        param.setSeqNbr(seqNbr);
        return param;

    }

    private void mapToInterviewMultiSelectObservationFieldP1DocCheckOrg(int sectionCounter, POCDMT000040Encounter out) {
        if (out.getEntryRelationshipArray(sectionCounter).getOrganizer() == null) {
            out.getEntryRelationshipArray(sectionCounter).addNewOrganizer();
        }


        if (out.getEntryRelationshipArray(sectionCounter).getOrganizer().getCode() == null) {
            out.getEntryRelationshipArray(sectionCounter).getOrganizer().addNewCode();
        }

        if (out.getEntryRelationshipArray(sectionCounter).getOrganizer().getStatusCode() == null) {
            out.getEntryRelationshipArray(sectionCounter).getOrganizer().addNewStatusCode();
        }
    }

    private InterviewAnswerMapper mapToInterviewObservation(EcrMsgInterviewAnswerDto in, int counter,
                                                            POCDMT000040Encounter out) throws EcrCdaXmlException {
        String questionSeq = CHANGE;
        String dataType="";
        int sequenceNbr = 0;
        int questionGroupSeqNbr = 0;
        int answerGroupSeqNbr = 0;

        InterviewAnswerMapper model = new InterviewAnswerMapper();

        var sizeArr = out.getEntryRelationshipArray().length;

        if (sizeArr > 0 && sizeArr == counter) {
            out.addNewEntryRelationship().addNewObservation();
            out.getEntryRelationshipArray(counter).getObservation().addNewCode();
            out.getEntryRelationshipArray(counter).getObservation().addNewStatusCode();
        }

        for (Map.Entry<String, Object> entry : in.getDataMap().entrySet()) {
            String name = entry.getKey();
            String value = this.cdaMapHelper.getValueFromMap(entry);

            InterviewObs param = new InterviewObs(questionGroupSeqNbr, answerGroupSeqNbr, dataType, sequenceNbr);
            var invObsP1 =  mapToInterviewObservationFieldP1(in,
                     name,
                     param);
            questionGroupSeqNbr = invObsP1.getQuestionGroupSeqNbr();
            answerGroupSeqNbr = invObsP1.getAnswerGroupSeqNbr();
            dataType = invObsP1.getDataType();
            sequenceNbr = invObsP1.getSequenceNbr();

            InterviewObs param2 = new InterviewObs(dataType, counter, sequenceNbr);
            var invObsP2 = mapToInterviewObservationFieldP2( in,
                     out,
                     name,
                     value,
                     param2);
            out = invObsP2.getOut();
            dataType = invObsP2.getDataType();
            counter = invObsP2.getCounter();
            sequenceNbr = invObsP2.getSequenceNbr();


            InterviewObs param3 = new InterviewObs(questionSeq, counter);
            var invObsP3 = mapToInterviewObservationFieldP3( in,
                     out,
                     name,
                     value,
                     param3);
            out = invObsP3.getOut();
            questionSeq = invObsP3.getQuestionSeq();
            counter = invObsP3.getCounter();

        }

        model.setComponent(out);
        model.setCounter(counter);
        model.setQuestionSeq(questionSeq);
        return model;
    }

    private InterviewObs mapToInterviewObservationFieldP3(EcrMsgInterviewAnswerDto in,
                                                  POCDMT000040Encounter out,
                                                  String name,
                                                  String value,
                                                  InterviewObs param) {
        String questionSeq = param.getQuestionSeq();
        int counter = param.getCounter();
        if(name.equals(COL_QUES_IDENTIFIER) && !in.getQuestionIdentifier().isEmpty()){
            if(in.getQuestionIdentifier().equals(value)){
                //IGNORE
            }else{
                if(questionSeq.equals(CHANGE)){
                    //IGNORE
                }else{
                    counter =  counter+1;
                }

                questionSeq =value;

                out.getEntryRelationshipArray(counter).getObservation().setClassCode("OBS");
                out.getEntryRelationshipArray(counter).getObservation().setMoodCode(XActMoodDocumentObservation.EVN);
                out.getEntryRelationshipArray(counter).getObservation().getCode().setCode(in.getQuestionIdentifier());
            }
        }
        else if(name.equals(COL_QUES_CODE_SYSTEM_CD) && !in.getQuesCodeSystemCd().isEmpty()){
            out.getEntryRelationshipArray(counter).getObservation().getCode().setCodeSystem(in.getQuesCodeSystemCd());
        }
        else if(name.equals(COL_QUES_CODE_SYSTEM_DESC_TXT) && !in.getQuesCodeSystemDescTxt().isEmpty()){
            out.getEntryRelationshipArray(counter).getObservation().getCode().setCodeSystemName(in.getQuesCodeSystemDescTxt());
        }
        else if(name.equals(COL_QUES_DISPLAY_TXT) && !in.getQuesDisplayTxt().isEmpty()){
            out.getEntryRelationshipArray(counter).getObservation().getCode().setDisplayName(in.getQuesDisplayTxt());
        }

        param.setQuestionSeq(questionSeq);
        param.setCounter(counter);
        param.setOut(out);

        return param;
    }

    private InterviewObs mapToInterviewObservationFieldP2(EcrMsgInterviewAnswerDto in,
                                                  POCDMT000040Encounter out,
                                                  String name,
                                                  String value,
                                                  InterviewObs param) throws EcrCdaXmlException {

        String dataType = param.getDataType();
        int counter = param.getCounter();
        int sequenceNbr = param.getSequenceNbr();
        if(dataType.equalsIgnoreCase(DATA_TYPE_CODE) || dataType.equalsIgnoreCase(COUNTY)){
            mapToInterviewObservationFieldP2County( name,
                     out,
                     in,
             counter,
             sequenceNbr);

        }
        else if(
                dataType.equals("TEXT") ||
                        dataType.equals(DATA_TYPE_NUMERIC)){
            if(name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()){
                var element = out.getEntryRelationshipArray(counter).getObservation();
                var ot = cdaMapHelper.mapToSTValue(value, element);
                out.getEntryRelationshipArray(counter).setObservation((POCDMT000040Observation) ot);
            }

        }
        else if(dataType.equalsIgnoreCase(  "DATE")){
            mapToInterviewObservationFieldP2Date( name,
                     out,
                     in,
             counter,
             value);
        }

        param.setDataType(dataType);
        param.setCounter(counter);
        param.setSequenceNbr(sequenceNbr);
        param.setOut(out);
        return param;
    }

    private void mapToInterviewObservationFieldP2Date(String name,
                                                 POCDMT000040Encounter out,
                                                 EcrMsgInterviewAnswerDto in,
                                                 int counter,
                                                 String value) throws EcrCdaXmlException {
        if(name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()){
            var element = out.getEntryRelationshipArray(counter).getObservation().getValueArray(0);
            XmlCursor cursor = element.newCursor();
            cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "TS");
            cursor.setAttributeText(new QName("name"), value);  // This is an assumption based on the original code

            if(!in.getAnswerTxt().isEmpty()){
                String newValue = cdaMapHelper.mapToTsType(in.getAnswerTxt()).toString();
                cursor.setAttributeText(new QName("name"), value);
                cursor.setTextValue(newValue);
            }
            else {
                element = out.getEntryRelationshipArray(counter).getObservation().getValueArray(0);
                cursor = element.newCursor();
                cursor.setAttributeText(new QName(NAME_SPACE_URL, "type"), "ST");

                if(!in.getAnswerTxt().isEmpty()) {
                    cursor.setTextValue(CDATA + in.getAnswerTxt() + CDATA);
                }
            }

            out.getEntryRelationshipArray(counter).getObservation().setValueArray(0, element);
            cursor.dispose();
        }
    }

    private void mapToInterviewObservationFieldP2County(String name,
                                                        POCDMT000040Encounter out,
                                                        EcrMsgInterviewAnswerDto in,
                                                        int counter,
                                                        int sequenceNbr) {
        CE ce = CE.Factory.newInstance();
        if (name.equals(COL_ANS_TXT) && !in.getAnswerTxt().isEmpty()) {
            ce.getTranslationArray(0).setCode(in.getAnswerTxt());
        }
        else if (name.equals(COL_ANS_CODE_SYSTEM_CD) && !in.getAnsCodeSystemCd().isEmpty()) {
            ce.getTranslationArray(0).setCodeSystem(in.getAnsCodeSystemCd());
        }
        else if (name.equals(COL_ANS_CODE_SYSTEM_DESC_TXT) && !in.getAnsCodeSystemDescTxt().isEmpty()) {
            ce.getTranslationArray(0).setCodeSystemName(in.getAnsCodeSystemDescTxt());
        }
        else if (name.equals(COL_ANS_DISPLAY_TXT) && !in.getAnsDisplayTxt().isEmpty()) {
            ce.getTranslationArray(0).setDisplayName(in.getAnsDisplayTxt());
        }
        else if (name.equals(COL_ANS_TO_CODE) && !in.getAnsToCode().isEmpty()) {
            ce.setCode(in.getAnsToCode());
        }
        else if (name.equals(COL_ANS_TO_CODE_SYSTEM_CD) && !in.getAnsToCodeSystemCd().isEmpty()) {
            ce.setCodeSystem(in.getAnsToCodeSystemCd());
        }
        else if (name.equals(COL_ANS_TO_CODE_SYSTEM_DESC_TXT) && !in.getAnsToCodeSystemDescTxt().isEmpty()) {
            ce.setCodeSystemName(in.getAnsToCodeSystemDescTxt());
        }
        else if (validateMapToInterviewObservationFieldP2CountyAnsDisplayNm(name, in)) {
            ce.setDisplayName(in.getAnsToDisplayNm());

        }
        out.getEntryRelationshipArray(counter).getObservation().getValueArray(sequenceNbr).set(ce);
    }

    private boolean validateMapToInterviewObservationFieldP2CountyAnsDisplayNm(String name, EcrMsgInterviewAnswerDto in) {
        return name.equals(COL_ANS_TO_DISPLAY_NM) && !in.getAnsToDisplayNm().isEmpty();
    }

    private InterviewObs mapToInterviewObservationFieldP1(EcrMsgInterviewAnswerDto in,
                                                  String name,
                                                  InterviewObs param) {
        int questionGroupSeqNbr = param.getQuestionGroupSeqNbr();
        int answerGroupSeqNbr = param.getAnswerGroupSeqNbr();
        String dataType = param.getDataType();
        int sequenceNbr = param.getSequenceNbr();

        if(name.equals(COL_QUES_GROUP_SEQ_NBR) && !in.getQuestionGroupSeqNbr().isEmpty()){
            questionGroupSeqNbr= Integer.parseInt(in.getQuestionGroupSeqNbr());
        }
        else if(name.equals(COL_ANS_GROUP_SEQ_NBR) && !in.getAnswerGroupSeqNbr().isEmpty() ){
            answerGroupSeqNbr= Integer.parseInt(in.getAnswerGroupSeqNbr());
        }
        else if(name.equals(COL_DATA_TYPE) && !in.getDataType().isEmpty()){
            dataType=in.getDataType();
        }
        else if(name.equals(COL_SEQ_NBR) && !in.getSeqNbr().isEmpty()){
            sequenceNbr= Integer.parseInt(in.getSeqNbr());
            if(sequenceNbr>0) {
                sequenceNbr =sequenceNbr-1;
            }
        }

        param.setQuestionGroupSeqNbr(questionGroupSeqNbr);
        param.setAnswerGroupSeqNbr(answerGroupSeqNbr);
        param.setDataType(dataType);
        param.setSequenceNbr(sequenceNbr);

        return param;
    }


}
