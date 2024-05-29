package gov.cdc.dataprocessing.utilities.component.nbs;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.NbsDocumentContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.NbsDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.model.NbsDocumentHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.NbsDocumentHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.NbsDocumentRepository;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.springframework.stereotype.Component;

@Component
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
            NbsDocumentContainer nbsDocumentVO = null;
            PersonContainer personVO = null;
            ParticipationDto participationDt = null;

            try {
                nbsDocumentVO = customRepository.getNbsDocument(nbsDocUid);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }

            Long personUid = nbsDocumentVO.getPatientVO().getThePersonDto().getPersonUid();

            try {
                personVO = patientRepositoryUtil.loadPerson(personUid);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }

            nbsDocumentVO.setPatientVO(personVO);

            try {
                participationDt = participationRepositoryUtil.getParticipation(personUid, nbsDocUid);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }

            nbsDocumentVO.setParticipationDT(participationDt);
            return nbsDocumentVO;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    public Long updateDocumentWithOutthePatient(NbsDocumentContainer nbsDocVO) throws Exception {
        Long nbsDocUid = null;

        try {
            NbsDocumentContainer nbSOldDocumentVO = customRepository.getNbsDocument(nbsDocVO.getNbsDocumentDT().getNbsDocumentUid());

            if (nbSOldDocumentVO != null) {
                NBSDocumentDto nbsDocumentDT = nbsDocVO.getNbsDocumentDT();
                nbsDocumentDT.setSuperclass("ACT");
                RootDtoInterface rootDTInterface = nbsDocVO.getNbsDocumentDT();
                String businessObjLookupName = NBSBOLookup.DOCUMENT;
                String businessTriggerCd = null;
                businessTriggerCd = "DOC_PROCESS";

                if (nbsDocumentDT.getRecordStatusCd() != null
                        && nbsDocumentDT.getRecordStatusCd().equals(
                        NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE))
                    businessTriggerCd = "DOC_DEL";
                if (nbsDocVO.isFromSecurityQueue())
                    businessTriggerCd = "DOC_IN_PROCESS";
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
