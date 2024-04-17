package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.*;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.*;
import org.springframework.stereotype.Component;

@Component
public class PublicHealthCaseRepositoryUtil {
    private final PublicHealthCaseRepository publicHealthCaseRepository;
    private final EntityGroupRepository entityGroupRepository;
    private final PlaceRepository placeRepository;
    private final NonPersonLivingSubjectRepository nonPersonLivingSubjectRepository;
    private final ClinicalDocumentRepository clinicalDocumentRepository;
    private final ReferralRepository referralRepository;
    private final PatientEncounterRepository patientEncounterRepository;

    public PublicHealthCaseRepositoryUtil(PublicHealthCaseRepository publicHealthCaseRepository,
                                          EntityGroupRepository entityGroupRepository,
                                          PlaceRepository placeRepository,
                                          NonPersonLivingSubjectRepository nonPersonLivingSubjectRepository,
                                          ClinicalDocumentRepository clinicalDocumentRepository,
                                          ReferralRepository referralRepository,
                                          PatientEncounterRepository patientEncounterRepository) {
        this.publicHealthCaseRepository = publicHealthCaseRepository;
        this.entityGroupRepository = entityGroupRepository;
        this.placeRepository = placeRepository;
        this.nonPersonLivingSubjectRepository = nonPersonLivingSubjectRepository;
        this.clinicalDocumentRepository = clinicalDocumentRepository;
        this.referralRepository = referralRepository;
        this.patientEncounterRepository = patientEncounterRepository;
    }

    public PublicHealthCaseVO getPublicHealthCaseContainer(long publicHealthCaseUid) throws DataProcessingException {
        var phc = findPublicHealthCase(publicHealthCaseUid);
        if (phc == null) {
            throw new DataProcessingException("Public Health Case Not Exist");
        }

        boolean isStdHivProgramAreaCode= false;


        //TODO: ENV VARIABLE - STD_PROGRAM_AREAS = STD
//        if(properties.getSTDProgramAreas()!=null){
//            StringTokenizer st2 = new StringTokenizer(properties.getSTDProgramAreas(), ",");
//            if (st2 != null) {
//                while (st2.hasMoreElements()) {
//                    if (st2.nextElement().equals(phc.getProgAreaCd())) {
//                        isStdHivProgramAreaCode= true;
//                        break;
//                    }
//                }
//            }
//        }
        phc.setStdHivProgramAreaCode(isStdHivProgramAreaCode);

        PublicHealthCaseVO publicHealthCaseContainer = new PublicHealthCaseVO();
        publicHealthCaseContainer.setThePublicHealthCaseDT(phc);
        return publicHealthCaseContainer;
    }

    public PublicHealthCaseDT findPublicHealthCase(long publicHealthCaseUid) {
        var phc = publicHealthCaseRepository.findById(publicHealthCaseUid);
        return phc.map(PublicHealthCaseDT::new).orElse(null);
    }


    public EntityGroupDto getEntityGroup(long entityGroupUid) {
        var entityGrp = entityGroupRepository.findById(entityGroupUid);
        return entityGrp.map(EntityGroupDto::new).orElse(null);
    }

    public PlaceDto getPlace(long placeUid) {
        var place = placeRepository.findById(placeUid);
        return place.map(PlaceDto::new).orElse(null);
    }

    public NonPersonLivingSubjectDto getNonPersonLivingSubject(long uid) {
        var nonp = nonPersonLivingSubjectRepository.findById(uid);
        return nonp.map(NonPersonLivingSubjectDto::new).orElse(null);
    }

    public ClinicalDocumentDto getClinicalDocument(long uid) {
        var doc = clinicalDocumentRepository.findById(uid);
        return doc.map(ClinicalDocumentDto::new).orElse(null);
    }

    public ReferralDto getReferral(long uid) {
        var doc = referralRepository.findById(uid);
        return doc.map(ReferralDto::new).orElse(null);
    }

    public PatientEncounterDto getPatientEncounter(long uid) {
        var doc = patientEncounterRepository.findById(uid);
        return doc.map(PatientEncounterDto::new).orElse(null);
    }
}
