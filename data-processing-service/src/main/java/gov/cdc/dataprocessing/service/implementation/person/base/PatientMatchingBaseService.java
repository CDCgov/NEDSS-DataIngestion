package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.MPRUpdateContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.model.person.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class PatientMatchingBaseService extends MatchingBaseService{
    private static final Logger logger = LoggerFactory.getLogger(PatientMatchingBaseService.class);

    public PatientMatchingBaseService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueService cachingValueService,
            PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService, prepareAssocModelHelper);
    }

    public Long setPatientRevision(PersonContainer personVO, String businessTriggerCd, String personType) throws DataProcessingException {
        PersonContainer mprPersonVO;
        Long mprPersonUid;
        Long personUid;
        try {
            PersonDto personDT = personVO.getThePersonDto();


            // NOTE: SHOULD NOT HIT THIS ONE
            if (personDT.getPersonParentUid() == null) {
                mprPersonVO = this.cloneVO(personVO);
                // as per shannon comments should not reflect on mpr
                mprPersonVO.getThePersonDto().setDescription(null);
                mprPersonVO.getThePersonDto().setAsOfDateAdmin(null);
                mprPersonVO.getThePersonDto().setAgeReported(null);
                mprPersonVO.getThePersonDto().setAgeReportedUnitCd(null);
//                if (mprPersonVO.getThePersonDto().getCurrSexCd() == null || mprPersonVO.getThePersonDto().getCurrSexCd().trim().length() == 0)
//                {
//                    mprPersonVO.getThePersonDto().setAsOfDateSex(null);
//                }
//
//
//                mprPersonUid = this.setPersonInternal(mprPersonVO,
//                        NBSBOLookup.PATIENT, "PAT_CR");
//
//                mprPersonVO = this.getPersonInternal(mprPersonUid);
//                personVO.getThePersonDto().setPersonParentUid(mprPersonUid);
//                personVO.getThePersonDto().setLocalId(
//                        mprPersonVO.getThePersonDto().getLocalId());
            }
            else {
                if (businessTriggerCd != null
                        && (businessTriggerCd.equals("PAT_CR") || businessTriggerCd
                        .equals("PAT_EDIT"))) {
                    this.updateWithRevision(personVO, personType);
                }

                if (personVO.getThePersonDto().getLocalId() == null || personVO.getThePersonDto().getLocalId().trim().length() == 0)
                {
                    mprPersonUid = personVO.getThePersonDto().getPersonParentUid();
                    mprPersonVO = getPatientRepositoryUtil().loadPerson(mprPersonUid);
                    personVO.getThePersonDto().setLocalId(mprPersonVO.getThePersonDto().getLocalId());
                }
            }


            personUid = this.setPersonInternal(personVO, NBSBOLookup.PATIENT, businessTriggerCd, personType);

            // NOTE: SHOULD NOT HIT THIS ONE EITHER
            if (personVO.getThePersonDto() != null && (personVO.getThePersonDto().getElectronicInd() != null
                    && !personVO.getThePersonDto().getElectronicInd().equals(NEDSSConstant.ELECTRONIC_IND_ELR)))
            {
                // ldf code
                // begin
//                LDFHelper ldfHelper = LDFHelper.getInstance();
//                ldfHelper.setLDFCollection(personVO.getTheStateDefinedFieldDataDTCollection(), personVO.getLdfUids(),
//                        NEDSSConstant.PATIENT_LDF, null, personUid, nbsSecurityObj);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        // ldf code end
        return personUid;
    }

    @SuppressWarnings("java:S3776")
    private Long setPersonInternal(PersonContainer personVO, String businessObjLookupName, String businessTriggerCd,
                                   String personType) throws DataProcessingException {
        Long personUID = -1L;
        String localId = "";
        boolean isELRCase = false;
        try {
            if (personVO.isItNew() || personVO.isItDirty()) {

                // changed as per shannon and chase, keep the temp localid and
                // set it back to personDT after prepareVOUtils
                if (personVO.getThePersonDto().isItNew() && !(businessObjLookupName.equalsIgnoreCase(NEDSSConstant.businessObjLookupNamePROVIDER)))
                {
                    localId = personVO.getThePersonDto().getLocalId();
                }

                if(localId==null){
                    personVO.getThePersonDto().setEdxInd("Y");
                    isELRCase= true;
                }

                RootDtoInterface personDT = getPrepareAssocModelHelper().prepareVO(
                        personVO.getThePersonDto(), businessObjLookupName,
                        businessTriggerCd, "PERSON", "BASE", personVO.getThePersonDto().getVersionCtrlNbr());

                if (personVO.getThePersonDto().isItNew()
                        && !(businessObjLookupName
                        .equalsIgnoreCase(NEDSSConstant.businessObjLookupNamePROVIDER)))
                {
                    personDT.setLocalId(localId);
                }

                personVO.setThePersonDto((PersonDto) personDT);

                Collection<EntityLocatorParticipationDto> collEntityLocatorPar;
                Collection<RoleDto> colRole;
                Collection<ParticipationDto> colParticipation;

                collEntityLocatorPar = personVO.getTheEntityLocatorParticipationDtoCollection();
                colRole = personVO.getTheRoleDtoCollection();
                colParticipation = personVO.getTheParticipationDtoCollection();

                if (collEntityLocatorPar != null) {
                    getEntityHelper().iterateELPDTForEntityLocatorParticipation(collEntityLocatorPar);
                    personVO.setTheEntityLocatorParticipationDtoCollection(collEntityLocatorPar);
                }

                if (colRole != null) {
                    getEntityHelper().iterateRDT(colRole);
                    personVO.setTheRoleDtoCollection(colRole);
                }

                if (colParticipation != null) {
                    getEntityHelper().iteratePDTForParticipation(colParticipation);
                    personVO.setTheParticipationDtoCollection(colParticipation);
                }

                personVO = getPatientRepositoryUtil().preparePersonNameBeforePersistence(personVO);


                if (personVO.isItNew()) {
                    var res =   getPatientRepositoryUtil().createPerson(personVO);
                    personUID = res.getPersonUid();
                    logger.debug(" EntityControllerEJB.setProvider() Person Created");
                } else {
                    getPatientRepositoryUtil().updateExistingPerson(personVO);
                    personUID = personVO.getThePersonDto()
                            .getPersonUid();
                    logger.debug(" EntityControllerEJB.setProvider() Person Updated");
                }
                if(isELRCase && personType.equals(NEDSSConstant.PAT))
                {
                    personVO.getThePersonDto().setPersonUid(personUID);
                    personVO.getThePersonDto().setPersonParentUid(personUID);
                    setPersonHashCdPatient(personVO);
                }
                else if (personType.equals(NEDSSConstant.NOK))
                {
                    personVO.getThePersonDto().setPersonUid(personUID);
                    personVO.getThePersonDto().setPersonParentUid(personUID);
                    setPersonHashCdNok(personVO);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
           
        }
        return personUID;
    }



    private boolean updateWithRevision(PersonContainer newRevision, String personType) throws DataProcessingException {
        try {
            Long mprUID = null;
            if(newRevision == null)
            {
                throw new DataProcessingException("Please provide a not null person object, newRevision is: "+ newRevision);
            }
            else
            {
                //Get the mpr uid
                PersonDto personDT = newRevision.getThePersonDto();
                if (personDT != null)
                {
                    mprUID = personDT.getPersonParentUid();
                }
            }

            if(mprUID == null && newRevision.getThePersonDto() != null)
            {
                throw new DataProcessingException("A person parent uid expected for this person uid: "+ newRevision.getThePersonDto().getPersonUid());
            }

            //Retrieve a mpr with the mprUID
            PersonContainer mpr = getPatientRepositoryUtil().loadPerson(mprUID);

            logger.debug("mpr is: " + mpr);

            if(mpr != null) //With the MPR, update...
            {
                mpr.setMPRUpdateValid(newRevision.isMPRUpdateValid());
                //localId need to be same for MPR and Revision and it need to be set at backend
                newRevision.getThePersonDto().setLocalId(mpr.getThePersonDto().getLocalId());
                return update(mpr, newRevision, personType);
            }
            else //No MPR.
            {
                throw new DataProcessingException("Cannot get a mpr for this person parent uid: "+ mprUID);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    //Updates the mpr, based on the new revision, using the default handler
    private boolean update(PersonContainer mpr, PersonContainer newRevision, String personType)
            throws DataProcessingException
    {
        try {
            Collection<PersonContainer> aNewRevisionList = new ArrayList<> ();
            aNewRevisionList.add(newRevision);

            MPRUpdateContainer mprUpdateVO = new MPRUpdateContainer(mpr, aNewRevisionList);
            if(process(mprUpdateVO))
            {
                return saveMPR(mpr, personType);
            }
            else
            {
                return false;
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }
    private boolean saveMPR(PersonContainer mpr, String personType) throws DataProcessingException {
        return storeMPR(mpr, NEDSSConstant.PAT_EDIT, personType);
    }

    private boolean storeMPR(PersonContainer mpr, String businessTriggerCd, String personType) throws DataProcessingException
    {
        try
        {
            return setMPR(mpr, businessTriggerCd, personType) != null;
        }
        catch(Exception rex)
        {
            throw new DataProcessingException(rex.getMessage(),rex);
        }
    }

    private Long setMPR(PersonContainer personVO, String businessTriggerCd, String personType) throws
            DataProcessingException {
        try {
            Long personUID = null;
            personVO.getThePersonDto().setEdxInd(NEDSSConstant.EDX_IND);
            if(personVO.isExt()){
                personVO.setItNew(false);
                personVO.setItDirty(false);
                if(personVO.isExt()){
                    if(personVO.getThePersonNameDtoCollection()!=null){
                        Collection<PersonNameDto> coll = personVO.getThePersonNameDtoCollection();
                        for (PersonNameDto personNameDT : coll) {
                            personNameDT.setItDirty(true);
                            personNameDT.setItNew(false);


                        }
                    }
                }
            }
            if(personVO.isMPRUpdateValid()){
                personUID = this.setPersonInternal(personVO, NBSBOLookup.PATIENT,
                        businessTriggerCd, personType);
                if(personVO.getThePersonDto().getPersonParentUid()==null)
                    personVO.getThePersonDto().setPersonParentUid(personUID);
                {
                    setPersonHashCdPatient(personVO);
                }

            }
            return personUID;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }


    private boolean process(MPRUpdateContainer mprUpdateVO)
    {
            PersonContainer mpr = mprUpdateVO.getMpr();
            Collection<EntityLocatorParticipationDto>  col = mpr.getTheEntityLocatorParticipationDtoCollection();
            if(col!=null && col.size()>0)
            {
                for (EntityLocatorParticipationDto entityLocatorParticipationDT : col) {
                    if ((entityLocatorParticipationDT.getThePhysicalLocatorDto() != null && entityLocatorParticipationDT.getThePhysicalLocatorDto().isItDirty()) ||
                            (entityLocatorParticipationDT.getTheTeleLocatorDto() != null && entityLocatorParticipationDT.getTheTeleLocatorDto().isItDirty()) ||
                            (entityLocatorParticipationDT.getThePostalLocatorDto() != null && entityLocatorParticipationDT.getThePostalLocatorDto().isItDirty()))
                        entityLocatorParticipationDT.setItDirty(true);
                }
            }
            mpr.setItDelete(false);
            mpr.setItNew(false);
            mpr.setItDirty(true);
            mpr.getThePersonDto().setItDirty(true);

        return true;
    }

    private PersonContainer cloneVO(PersonContainer personVO)
            throws DataProcessingException {
        try {
            if (personVO != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(personVO);
                ByteArrayInputStream bais = new ByteArrayInputStream(
                        baos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object clonePersonVO = ois.readObject();

                return (PersonContainer) clonePersonVO;
            } else
                return personVO;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    protected PersonId updateExistingPerson(PersonContainer personContainer, String businessTriggerCd, Long personParentUid) throws DataProcessingException {
        PersonId personId = new PersonId();
        PersonContainer personObj = personContainer.deepClone();
        if (businessTriggerCd != null && (businessTriggerCd.equals("PAT_CR") || businessTriggerCd.equals("PAT_EDIT"))) {
            personId = getPersonInternalV2(personParentUid);

            personObj.setMPRUpdateValid(true);

            personObj.getThePersonDto().setPersonUid(personId.getPersonId());
            personObj.getThePersonDto().setPersonParentUid(personId.getPersonParentId());
            personObj.getThePersonDto().setLocalId(personId.getLocalId());


            prepUpdatingExistingPerson(personObj);
        }
        return personId;
    }

    @SuppressWarnings("java:S6541")
    protected String getLNmFnmDobCurSexStr(PersonContainer personContainer) {
        String namedobcursexStr = null;
        String carrot = "^";
        if (personContainer.getThePersonDto() != null) {
            PersonDto personDto = personContainer.getThePersonDto();
            if (personDto.getCd() != null
                    && personDto.getCd().equals(NEDSSConstant.PAT)) {
                if (personContainer.getThePersonNameDtoCollection() != null
                        && personContainer.getThePersonNameDtoCollection().size() > 0) {
                    Collection<PersonNameDto> personNameDtoColl = personContainer
                            .getThePersonNameDtoCollection();
                    Iterator<PersonNameDto> personNameIterator = personNameDtoColl.iterator();
                    Timestamp asofDate = null;
                    while (personNameIterator.hasNext()) {
                        PersonNameDto personNameDto =  personNameIterator
                                .next();
                        if (personNameDto.getNmUseCd() == null)
                        {
                            String Message = "personNameDT.getNmUseCd() is null";
                            logger.debug(Message);
                        }
                        if (personNameDto.getNmUseCd() != null
                                && personNameDto.getNmUseCd().equalsIgnoreCase("L")
                                && personNameDto.getRecordStatusCd() != null
                                && personNameDto.getRecordStatusCd().equals(
                                NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                            if (asofDate == null
                                    || (asofDate.getTime() < personNameDto
                                    .getAsOfDate().getTime())) {
                                if ((personNameDto.getLastNm() != null)
                                        && (!personNameDto.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDto.getFirstNm() != null)
                                        && (!personNameDto.getFirstNm().trim()
                                        .equals(""))
                                        && (personDto.getBirthTime() != null)
                                        && (personDto.getCurrSexCd() != null)
                                        && (!personDto.getCurrSexCd().trim()
                                        .equals(""))) {
                                    namedobcursexStr = personNameDto.getLastNm()
                                            + carrot
                                            + personNameDto.getFirstNm()
                                            + carrot + personDto.getBirthTime()
                                            + carrot + personDto.getCurrSexCd();
                                    asofDate = personNameDto.getAsOfDate();
                                }
                            } else if (asofDate.before(personNameDto
                                    .getAsOfDate())) {
                                if ((personNameDto.getLastNm() != null)
                                        && (!personNameDto.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDto.getFirstNm() != null)
                                        && (!personNameDto.getFirstNm().trim()
                                        .equals(""))
                                        && (personDto.getBirthTime() != null)
                                        && (personDto.getCurrSexCd() != null)
                                        && (!personDto.getCurrSexCd().trim()
                                        .equals(""))) {
                                    namedobcursexStr = personNameDto.getLastNm()
                                            + carrot
                                            + personNameDto.getFirstNm()
                                            + carrot + personDto.getBirthTime()
                                            + carrot + personDto.getCurrSexCd();
                                    asofDate = personNameDto.getAsOfDate();

                                }

                            }

                        }
                    }
                }
            }
        }
        return namedobcursexStr;
    }
    protected void setPersonHashCdPatient(PersonContainer personContainer) throws DataProcessingException {
        try {
            long personUid = personContainer.getThePersonDto().getPersonParentUid();

            // DELETE Patient Matching Hash String
            getEdxPatientMatchRepositoryUtil().deleteEdxPatientMatchDTColl(personUid);
            try {
                if(personContainer.getThePersonDto().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)){
                    personContainer.getThePersonDto().setPersonUid(personUid);
                    // INSERTING Patient Matching Hash String
                    setPersonToMatchEntityPatient(personContainer);
                }
            } catch (Exception e) {
                //per defect #1836 change to warning..
                logger.warn("Unable to setPatientHashCd for personUid: "+personUid);
                logger.warn("Exception in setPatientToEntityMatch -> unhandled exception: " +e.getMessage());
            }
        } catch (Exception e) {
            logger.error("EntityControllerEJB.setPatientHashCd: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }
    protected PersonId setAndCreateNewPerson(PersonContainer psn) throws DataProcessingException {
        PersonId personUID = new PersonId();
        PersonContainer personContainer = psn.deepClone();
        Person person;
        Collection<EntityLocatorParticipationDto> elpDTCol = personContainer.getTheEntityLocatorParticipationDtoCollection();
        Collection<RoleDto> rDTCol = personContainer.getTheRoleDtoCollection();
        Collection<ParticipationDto> pDTCol = personContainer.getTheParticipationDtoCollection();
        Collection<EntityLocatorParticipationDto> colEntityLocatorParticipation;
        Collection<RoleDto> colRole;
        Collection<ParticipationDto> colParticipation;
        // NOTE: Sorting out Collection such as: Entity Locator Participation, Role, Participation
        if (elpDTCol != null) {
            colEntityLocatorParticipation = getEntityHelper().iterateELPDTForEntityLocatorParticipation(elpDTCol);
            personContainer.setTheEntityLocatorParticipationDtoCollection(colEntityLocatorParticipation);
        }
        if (rDTCol != null) {
            colRole = getEntityHelper().iterateRDT(rDTCol);
            personContainer.setTheRoleDtoCollection(colRole);
        }
        if (pDTCol != null) {
            colParticipation = getEntityHelper().iteratePDTForParticipation(pDTCol);
            personContainer.setTheParticipationDtoCollection(colParticipation);
        }
        person = getPatientRepositoryUtil().createPerson(personContainer);
        personUID.setPersonId(person.getPersonUid());
        personUID.setPersonParentId(person.getPersonParentUid());
        personUID.setLocalId(person.getLocalId());

        logger.debug(" EntityControllerEJB.setPerson() Person Created");
        return personUID;

    }

    private PersonId getPersonInternalV2(Long personUID) throws DataProcessingException {
        PersonId personId;
        try {
            Person person = null;
            if (personUID != null)
            {
                person = getPatientRepositoryUtil().findExistingPersonByUid(personUID);
            }
            if (person != null)
            {
                personId = new PersonId();
                personId.setPersonParentId(person.getPersonParentUid());
                personId.setPersonId(person.getPersonUid());
                personId.setLocalId(person.getLocalId());
            }
            else {
                throw new DataProcessingException("Existing Patient Not Found");
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return personId;

    }
    private void prepUpdatingExistingPerson(PersonContainer personContainer) throws DataProcessingException {
        PersonDto personDto = personContainer.getThePersonDto();

        personContainer.setThePersonDto(personDto);
        Collection<EntityLocatorParticipationDto> collEntityLocatorPar;
        Collection<RoleDto> colRole;
        Collection<ParticipationDto> colParticipation;

        collEntityLocatorPar = personContainer.getTheEntityLocatorParticipationDtoCollection();
        colRole = personContainer.getTheRoleDtoCollection();
        colParticipation = personContainer.getTheParticipationDtoCollection();

        if (collEntityLocatorPar != null) {
            getEntityHelper().iterateELPDTForEntityLocatorParticipation(collEntityLocatorPar);
            personContainer.setTheEntityLocatorParticipationDtoCollection(collEntityLocatorPar);
        }

        if (colRole != null) {
            getEntityHelper().iterateRDT(colRole);
            personContainer.setTheRoleDtoCollection(colRole);
        }

        if (colParticipation != null) {
            getEntityHelper().iteratePDTForParticipation(colParticipation);
            personContainer.setTheParticipationDtoCollection(colParticipation);
        }

        personContainer = getPatientRepositoryUtil().preparePersonNameBeforePersistence(personContainer);
        getPatientRepositoryUtil().updateExistingPerson(personContainer);

    }
    private void setPersonToMatchEntityPatient(PersonContainer personContainer) throws DataProcessingException {
        Long patientUid = personContainer.getThePersonDto().getPersonUid();
        EdxPatientMatchDto edxPatientMatchDto;
        String cdDescTxt = personContainer.thePersonDto.getCdDescTxt();
        if (cdDescTxt == null || cdDescTxt.equalsIgnoreCase("") || !cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
            String identifierStr;
            int identifierStrhshCd = 0;
            List<String> identifierStrList = getIdentifier(personContainer);
            if (identifierStrList != null && !identifierStrList.isEmpty()) {
                for (String s : identifierStrList) {
                    identifierStr =  s;
                    if (identifierStr != null) {
                        identifierStr = identifierStr.toUpperCase();
                        identifierStrhshCd = identifierStr.hashCode();
                    }

                    if (identifierStr != null) {
                        edxPatientMatchDto = new EdxPatientMatchDto();
                        edxPatientMatchDto.setPatientUid(patientUid);
                        edxPatientMatchDto.setTypeCd(NEDSSConstant.PAT);
                        edxPatientMatchDto.setMatchString(identifierStr);
                        edxPatientMatchDto.setMatchStringHashCode((long) identifierStrhshCd);
                        try {
                            getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDto);
                        } catch (Exception e) {
                            logger.error("Error in creating the setEdxPatientMatchDT with identifierStr:"
                                    + identifierStr + " " + e.getMessage());
                            throw new DataProcessingException(e.getMessage(), e);
                        }

                    }
                }
            }

            // Matching with last name ,first name ,date of birth and current
            // sex

            String namesdobcursexStr;
            int namesdobcursexStrhshCd = 0;
            namesdobcursexStr = getLNmFnmDobCurSexStr(personContainer);
            if (namesdobcursexStr != null) {
                namesdobcursexStr = namesdobcursexStr.toUpperCase();
                namesdobcursexStrhshCd = namesdobcursexStr.hashCode();
            }

            if (namesdobcursexStr != null) {
                edxPatientMatchDto = new EdxPatientMatchDto();
                edxPatientMatchDto.setPatientUid(patientUid);
                edxPatientMatchDto.setTypeCd(NEDSSConstant.PAT);
                edxPatientMatchDto.setMatchString(namesdobcursexStr);
                edxPatientMatchDto.setMatchStringHashCode((long) namesdobcursexStrhshCd);
                try {
                    getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDto);
                } catch (Exception e) {
                    logger.error("Error in creating the setEdxPatientMatchDT with namesdobcursexStr:" + namesdobcursexStr + " " + e.getMessage());
                    throw new DataProcessingException(e.getMessage(), e);
                }

            }
        }
    }

    protected void setPersonHashCdNok(PersonContainer personContainer) throws DataProcessingException {
        try {
            long personUid = personContainer.getThePersonDto().getPersonParentUid();

            // DELETE Patient Matching Hash String
            getEdxPatientMatchRepositoryUtil().deleteEdxPatientMatchDTColl(personUid);
            try {
                if(personContainer.getThePersonDto().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)){
                    personContainer.getThePersonDto().setPersonUid(personUid);
                    // INSERTING Patient Matching Hash String
                    setPersonToMatchEntityNok(personContainer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("java:S3776")
    private void setPersonToMatchEntityNok(PersonContainer personContainer) throws DataProcessingException {
        Long patientUid = personContainer.getThePersonDto().getPersonUid();
        EdxPatientMatchDto edxPatientMatchDto;
        String cdDescTxt = personContainer.thePersonDto.getCdDescTxt();
        if (cdDescTxt != null && cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
            String nameAddStrSt1 ;
            int nameAddStrSt1hshCd;
            List<String> nameAddressStreetOneStrList = nameAddressStreetOneNOK(personContainer);
            if (nameAddressStreetOneStrList != null
                    && !nameAddressStreetOneStrList.isEmpty()) {
                for (String s : nameAddressStreetOneStrList) {
                    nameAddStrSt1 = s;
                    if (nameAddStrSt1 != null) {
                        nameAddStrSt1 = nameAddStrSt1.toUpperCase();
                        nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
                        edxPatientMatchDto = new EdxPatientMatchDto();
                        edxPatientMatchDto.setPatientUid(patientUid);
                        edxPatientMatchDto.setTypeCd(NEDSSConstant.NOK);
                        edxPatientMatchDto.setMatchString(nameAddStrSt1);
                        edxPatientMatchDto.setMatchStringHashCode((long) nameAddStrSt1hshCd);
                        try {
                            getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDto);
                        } catch (Exception e) {
                            throw new DataProcessingException(e.getMessage(), e);
                        }
                    }
                }
            }
            List<String> nameTelePhoneStrList = telePhoneTxtNOK(personContainer);
            String nameTelePhone;
            int nameTelePhonehshCd;
            if (nameTelePhoneStrList != null && !nameTelePhoneStrList.isEmpty()) {
                for (String s : nameTelePhoneStrList) {
                    nameTelePhone = s;
                    if (nameTelePhone != null) {
                        nameTelePhone = nameTelePhone.toUpperCase();
                        nameTelePhonehshCd = nameTelePhone.hashCode();
                        edxPatientMatchDto = new EdxPatientMatchDto();
                        edxPatientMatchDto.setPatientUid(patientUid);
                        edxPatientMatchDto.setTypeCd(NEDSSConstant.NOK);
                        edxPatientMatchDto.setMatchString(nameTelePhone);
                        edxPatientMatchDto.setMatchStringHashCode((long) nameTelePhonehshCd);
                        try {
                            getEdxPatientMatchRepositoryUtil().setEdxPatientMatchDT(edxPatientMatchDto);
                        } catch (Exception e) {
                            throw new DataProcessingException(e.getMessage(), e);
                        }

                    }
                }// for loop
            }

        }// end of method
    }


    @SuppressWarnings("java:S3776")
    protected List<String> nameAddressStreetOneNOK(PersonContainer personContainer) {
        String nameAddStr = null;
        String carrot = "^";
        List<String> nameAddressStreetOnelNOKist = new ArrayList<>();
        if (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {

            for (EntityLocatorParticipationDto entLocPartDT : personContainer.getTheEntityLocatorParticipationDtoCollection()) {
                if (entLocPartDT.getClassCd() != null
                        && entLocPartDT.getRecordStatusCd() != null
                        && entLocPartDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)
                        && entLocPartDT.getClassCd().equals(NEDSSConstant.POSTAL)
                        && entLocPartDT.getCd() != null)
                {
                    PostalLocatorDto postLocDT = entLocPartDT.getThePostalLocatorDto();
                    if (postLocDT != null
                            && (postLocDT.getStreetAddr1() != null
                            && !postLocDT.getStreetAddr1().equals(""))
                            && (postLocDT.getCityDescTxt() != null
                            && !postLocDT.getCityDescTxt().equals(""))
                            && (postLocDT.getStateCd() != null
                            && !postLocDT.getStateCd().equals(""))
                            && (postLocDT.getZipCd() != null
                            && !postLocDT.getZipCd().equals("")))
                    {
                        nameAddStr = carrot + postLocDT.getStreetAddr1() + carrot + postLocDT.getCityDescTxt() + carrot
                                + postLocDT.getStateCd() + carrot + postLocDT.getZipCd();
                    }
                }
            }
            if (nameAddStr != null)
            {
                nameAddStr = getNamesStr(personContainer) + nameAddStr;
            }
            nameAddressStreetOnelNOKist.add(nameAddStr);

        }

        return nameAddressStreetOnelNOKist;
    }

    @SuppressWarnings("java:S3776")
    protected List<String> telePhoneTxtNOK(PersonContainer personContainer) {
        String carrot = "^";
        List<String> telePhoneTxtList = new ArrayList<>();

        if (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            for (EntityLocatorParticipationDto entLocPartDT : personContainer.getTheEntityLocatorParticipationDtoCollection()) {
                if (entLocPartDT.getClassCd() != null && entLocPartDT.getClassCd().equals(NEDSSConstant.TELE)
                        && entLocPartDT.getRecordStatusCd() != null && entLocPartDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)) {

                    TeleLocatorDto teleLocDT = entLocPartDT.getTheTeleLocatorDto();
                    if (teleLocDT != null && teleLocDT.getPhoneNbrTxt() != null && !teleLocDT.getPhoneNbrTxt().isEmpty()) {
                        StringBuilder nameTeleStr = new StringBuilder();
                        nameTeleStr.append(carrot).append(teleLocDT.getPhoneNbrTxt());

                        String namesStr = getNamesStr(personContainer);
                        if (namesStr != null) {
                            nameTeleStr.insert(0, namesStr);
                            telePhoneTxtList.add(nameTeleStr.toString());
                        } else {
                            return null; //NOSONAR
                        }
                    }
                }
            }
        }

        return telePhoneTxtList;
    }





}
