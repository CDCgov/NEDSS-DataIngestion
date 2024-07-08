package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PatientMatchingBaseServiceTest {

    @Mock
    private EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private CachingValueService cachingValueService;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;

    @InjectMocks
    private PatientMatchingBaseService patientMatchingBaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtil);
        Mockito.reset(entityHelper);
        Mockito.reset(patientRepositoryUtil);
        Mockito.reset(cachingValueService);
        Mockito.reset(prepareAssocModelHelper);
    }

    @Test
    void setPatientRevision_new_pat() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.setItNew(true);
        personContainer.getThePersonDto().setVersionCtrlNbr(1);

        PersonDto personDto=new PersonDto();
        personDto.setItNew(true);
        personDto.setVersionCtrlNbr(2);

        when(prepareAssocModelHelper
                .prepareVO(any(),
                        any(), any(),
                        eq("PERSON"),
                        eq("BASE"),
                        any()
                )).thenReturn(personDto);

        PersonContainer personContainerPrepare=new PersonContainer();
        personContainerPrepare.setItNew(true);
        personContainerPrepare.getThePersonDto().setVersionCtrlNbr(1);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainerPrepare.getThePersonNameDtoCollection().add(personNameDto);

        when(patientRepositoryUtil.preparePersonNameBeforePersistence(any())).thenReturn(personContainerPrepare);

        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");
        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        //call test
        patientMatchingBaseService.setPatientRevision(personContainer,"",NEDSSConstant.PAT);
    }

    @Test
    void setPatientRevision_new_nok() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.setItNew(true);
        personContainer.getThePersonDto().setVersionCtrlNbr(1);

        PersonDto personDto=new PersonDto();
        personDto.setItNew(true);
        personDto.setVersionCtrlNbr(2);

        when(prepareAssocModelHelper
                .prepareVO(any(),
                        any(), any(),
                        eq("PERSON"),
                        eq("BASE"),
                        any()
                )).thenReturn(personDto);

        PersonContainer personContainerPrepare=new PersonContainer();
        personContainerPrepare.setItNew(true);
        personContainerPrepare.getThePersonDto().setVersionCtrlNbr(1);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainerPrepare.getThePersonNameDtoCollection().add(personNameDto);

        when(patientRepositoryUtil.preparePersonNameBeforePersistence(any())).thenReturn(personContainerPrepare);

        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");
        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        patientMatchingBaseService.setPatientRevision(personContainer,"",NEDSSConstant.NOK);
    }


    @Test
    void getLNmFnmDobCurSexStr() {
        PersonContainer personContainer=new PersonContainer();
        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);
        personContainer.getThePersonDto().setCurrSexCd("M");
        personContainer.getThePersonDto().setBirthTime(new Timestamp(System.currentTimeMillis()));

        PersonNameDto personNameDto1 = new PersonNameDto();
        personNameDto1.setNmUseCd("L");
        personNameDto1.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto1.setAsOfDate(Timestamp.valueOf("2021-09-01 10:01:15"));
        personNameDto1.setLastNm("TEST_LST_NM1");
        personNameDto1.setFirstNm("TEST_FIRST_NM1");
        personContainer.getThePersonNameDtoCollection().add(personNameDto1);

        PersonNameDto personNameDto2 = new PersonNameDto();
        personNameDto2.setNmUseCd("L");
        personNameDto2.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto2.setAsOfDate(Timestamp.valueOf("2022-09-01 10:01:15"));
        personNameDto2.setLastNm("TEST_LST_NM2");
        personNameDto2.setFirstNm("TEST_FIRST_NM2");
        personContainer.getThePersonNameDtoCollection().add(personNameDto2);

        PersonNameDto personNameDto3 = new PersonNameDto();
        personNameDto3.setNmUseCd("L");
        personNameDto3.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto3.setAsOfDate(Timestamp.valueOf("2015-09-01 10:01:15"));
        personNameDto3.setLastNm("TEST_LST_NM2");
        personNameDto3.setFirstNm("TEST_FIRST_NM2");
        personContainer.getThePersonNameDtoCollection().add(personNameDto3);

        patientMatchingBaseService.getLNmFnmDobCurSexStr(personContainer);
    }

    @Test
    void updateExistingPerson_Test() throws DataProcessingException {
        var perCon = new PersonContainer();
        var businessTrigger = "PAT_CR";

        var perDt = new PersonDto();
        perDt.setPersonParentUid(10L);
        perCon.setThePersonDto(perDt);

        var perLst = new ArrayList<Person>();
        var per = new Person();
        per.setPersonParentUid(10L);
        per.setPersonUid(20L);
        per.setLocalId("TEST");
        perLst.add(per);
        per = new Person();
        per.setPersonParentUid(10L);
        per.setPersonUid(10L);
        perLst.add(per);
        per.setLocalId("TEST");

        when(patientRepositoryUtil.findPersonByParentUid(any())).thenReturn(perLst);


        var res = patientMatchingBaseService.updateExistingPerson(perCon, businessTrigger, 10L);

        assertNotNull(res);
        assertEquals(10, res.personId);

    }

    @Test
    void updateExistingPerson_Test_1() {
        var perCon = new PersonContainer();
        var businessTrigger = "PAT_EDIT";

        var perDt = new PersonDto();
        perDt.setPersonParentUid(10L);
        perCon.setThePersonDto(perDt);

        var per = new Person();
        per.setPersonParentUid(20L);
        per.setPersonUid(20L);
        per.setLocalId("TEST");
        per.setLocalId("TEST");

        when(patientRepositoryUtil.findPersonByParentUid(any())).thenReturn(new ArrayList<>());
        when(patientRepositoryUtil.findExistingPersonByUid(any())).thenReturn(per);



        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            patientMatchingBaseService.updateExistingPerson(perCon, businessTrigger, 10L);
        });


        assertNotNull(thrown);

    }

    @Test
    void updateExistingPerson_Test_2() {
        var perCon = new PersonContainer();
        var businessTrigger = "PAT_EDIT";

        var perDt = new PersonDto();
        perDt.setPersonParentUid(10L);
        perCon.setThePersonDto(perDt);

        var per = new Person();
        per.setPersonParentUid(20L);
        per.setPersonUid(20L);
        per.setLocalId("TEST");
        per.setLocalId("TEST");

        when(patientRepositoryUtil.findPersonByParentUid(any())).thenReturn(new ArrayList<>());
        when(patientRepositoryUtil.findExistingPersonByUid(any())).thenThrow(new RuntimeException("TEST"));



        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            patientMatchingBaseService.updateExistingPerson(perCon, businessTrigger, 10L);
        });


        assertNotNull(thrown);

    }

    @Test
    void setPatientRevision_Test_Else() throws DataProcessingException {
        var perCon = new PersonContainer();
        var businessTrigger = "PAT_CR";
        var personType = "TEST";

        var perDt = new PersonDto();
        perDt.setPersonParentUid(10L);
        perDt.setPersonUid(10L);
        perCon.setThePersonDto(perDt);
        perCon.setExt(true);

        var perNameCol = new ArrayList<PersonNameDto>();
        var perName = new PersonNameDto();
        perNameCol.add(perName);
        perCon.setThePersonNameDtoCollection(perNameCol);
        perCon.setMPRUpdateValid(true);
        perCon.setItDelete(true);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(perCon);


        var res = patientMatchingBaseService.setPatientRevision(perCon, businessTrigger, personType);
        assertNotNull(res);
    }

}