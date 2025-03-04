package gov.cdc.dataprocessing.utilities.component.nbs;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.NbsDocumentContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocumentHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsDocumentHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsDocumentRepository;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.springframework.stereotype.Component;
@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class NbsDocumentRepositoryUtil {
    private final CustomRepository customRepository;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final ParticipationRepositoryUtil participationRepositoryUtil;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final NbsDocumentRepository nbsDocumentRepository;

    private final NbsDocumentHistRepository nbsDocumentHistRepository;

    public NbsDocumentRepositoryUtil(CustomRepository customRepository,
                                     PatientRepositoryUtil patientRepositoryUtil,
                                     ParticipationRepositoryUtil participationRepositoryUtil,
                                     PrepareAssocModelHelper prepareAssocModelHelper,
                                     NbsDocumentRepository nbsDocumentRepository,
                                     NbsDocumentHistRepository nbsDocumentHistRepository) {
        this.customRepository = customRepository;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.nbsDocumentRepository = nbsDocumentRepository;
        this.nbsDocumentHistRepository = nbsDocumentHistRepository;
    }

    public NbsDocumentContainer getNBSDocumentWithoutActRelationship(Long nbsDocUid) throws  DataProcessingException {
        try {
            NbsDocumentContainer nbsDocumentVO;
            PersonContainer personVO ;
            ParticipationDto participationDt ;

            nbsDocumentVO = customRepository.getNbsDocument(nbsDocUid);
            Long personUid = nbsDocumentVO.getPatientVO().getThePersonDto().getPersonUid();
            personVO = patientRepositoryUtil.loadPerson(personUid);
            nbsDocumentVO.setPatientVO(personVO);
            participationDt = participationRepositoryUtil.getParticipation(personUid, nbsDocUid);

            nbsDocumentVO.setParticipationDT(participationDt);
            return nbsDocumentVO;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    public Long updateDocumentWithOutthePatient(NbsDocumentContainer nbsDocVO) throws DataProcessingException {
        Long nbsDocUid = null;

        try {
            NbsDocumentContainer nbSOldDocumentVO = customRepository.getNbsDocument(nbsDocVO.getNbsDocumentDT().getNbsDocumentUid());

            if (nbSOldDocumentVO != null) {
                NBSDocumentDto nbsDocumentDT = nbsDocVO.getNbsDocumentDT();
                nbsDocumentDT.setSuperclass("ACT");
                RootDtoInterface rootDTInterface = nbsDocVO.getNbsDocumentDT();
                String businessObjLookupName = NBSBOLookup.DOCUMENT;
                String businessTriggerCd ;
                businessTriggerCd = "DOC_PROCESS";

                if (nbsDocumentDT.getRecordStatusCd() != null
                        && nbsDocumentDT.getRecordStatusCd().equals(
                        NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE))
                {
                    businessTriggerCd = "DOC_DEL";
                }
                if (nbsDocVO.isFromSecurityQueue())
                {
                    businessTriggerCd = "DOC_IN_PROCESS";
                }
                String tableName = "NBS_DOCUMENT";
                String moduleCd = "BASE";
                nbsDocumentDT =  (NBSDocumentDto) prepareAssocModelHelper.prepareVO(
                        rootDTInterface, businessObjLookupName,
                        businessTriggerCd, tableName, moduleCd, rootDTInterface.getVersionCtrlNbr());

                // update the record
                nbsDocUid = updateNbsDocument(nbsDocumentDT);

                // insert the old record in the history
                insertNBSDocumentHist(nbSOldDocumentVO.getNbsDocumentDT());
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return nbsDocUid;

    }

    public void insertNBSDocumentHist(NBSDocumentDto nbsDocumentDto) {
        var nbs = new NbsDocumentHist(nbsDocumentDto);
        nbsDocumentHistRepository.save(nbs);
    }
    public Long updateNbsDocument(NBSDocumentDto nbsDocumentDto) {
        var nbs = new NbsDocument(nbsDocumentDto);
        nbsDocumentRepository.save(nbs);
        return nbs.getNbsDocumentUid();
    }

}
