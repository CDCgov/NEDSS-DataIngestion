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

}