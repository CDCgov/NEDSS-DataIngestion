package gov.cdc.dataprocessing.service.implementation.entity;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PhysicalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.PostalLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.TeleLocatorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.OdseIdGeneratorService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EntityLocatorParticipationServiceTest {
    @Mock
    private  EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    @Mock
    private  TeleLocatorRepository teleLocatorRepository;
    @Mock
    private  PostalLocatorRepository postalLocatorRepository;
    @Mock
    private  PhysicalLocatorRepository physicalLocatorRepository;
    @Mock
    private  OdseIdGeneratorService odseIdGeneratorService;

    @Mock
    private PersonRepository personRepository;
    @InjectMocks
    private EntityLocatorParticipationService entityLocatorParticipationService;
    @Mock
    AuthUtil authUtil;

    private Collection<EntityLocatorParticipationDto> locatorCollection;


    @BeforeEach
    void setUp() {
        locatorCollection = new ArrayList<>();

        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(entityLocatorParticipationRepository, teleLocatorRepository, postalLocatorRepository, physicalLocatorRepository,
                odseIdGeneratorService, authUtil, personRepository);
    }

    @Test
    void updateEntityLocatorParticipation_Success() throws DataProcessingException {
        Long uid = 10L;
        Collection<EntityLocatorParticipationDto> locatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.PHYSICAL);
        var physicalDto = new PhysicalLocatorDto();
        locator.setThePhysicalLocatorDto(physicalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.POSTAL);
        locator.setCd(NEDSSConstant.HOME);
        var postalDto = new PostalLocatorDto();
        postalDto.setStreetAddr1("TEST");
        locator.setThePostalLocatorDto(postalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        var teleDto = new TeleLocatorDto();
        locator.setClassCd(NEDSSConstant.TELE);
        locator.setTheTeleLocatorDto(teleDto);
        locatorCollection.add(locator);


        List<EntityLocatorParticipation> entityPatCol = new ArrayList<>();
        EntityLocatorParticipation entityPat = new EntityLocatorParticipation();
        entityPat.setClassCd(NEDSSConstant.PHYSICAL);
        entityPat.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        entityPat.setLocatorUid(11L);
        entityPatCol.add(entityPat);
        entityPat = new EntityLocatorParticipation();
        entityPat.setClassCd(NEDSSConstant.POSTAL);
        entityPat.setCd(NEDSSConstant.HOME);
        entityPat.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        entityPat.setLocatorUid(12L);
        entityPatCol.add(entityPat);
        entityPat = new EntityLocatorParticipation();
        entityPat.setClassCd(NEDSSConstant.TELE);
        entityPat.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        entityPat.setLocatorUid(13L);
        entityPatCol.add(entityPat);

        when(entityLocatorParticipationRepository.findByParentUid(10L)).thenReturn(Optional.of(entityPatCol));

        LocalUidGenerator localUid = new LocalUidGenerator();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON)).thenReturn(localUid);

        entityLocatorParticipationService.updateEntityLocatorParticipation(locatorCollection, uid);

        verify(physicalLocatorRepository, times(1)).save(any());
        verify(postalLocatorRepository, times(1)).save(any());
        verify(teleLocatorRepository, times(1)).save(any());

    }

    @Test
    void updateEntityLocatorParticipation_Success2() throws DataProcessingException {
        Long uid = 10L;
        Collection<EntityLocatorParticipationDto> locatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.PHYSICAL);
        var physicalDto = new PhysicalLocatorDto();
        locator.setThePhysicalLocatorDto(physicalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.POSTAL);
        locator.setCd(NEDSSConstant.HOME);
        var postalDto = new PostalLocatorDto();
        postalDto.setStreetAddr1("TEST");
        locator.setThePostalLocatorDto(postalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        var teleDto = new TeleLocatorDto();
        locator.setClassCd(NEDSSConstant.TELE);
        locator.setTheTeleLocatorDto(teleDto);
        locatorCollection.add(locator);


        List<EntityLocatorParticipation> entityPatCol = new ArrayList<>();
        EntityLocatorParticipation entityPat = new EntityLocatorParticipation();
        entityPat.setClassCd(NEDSSConstant.PHYSICAL);
        entityPat.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        entityPat.setLocatorUid(11L);
        entityPatCol.add(entityPat);
        entityPat = new EntityLocatorParticipation();
        entityPat.setClassCd(NEDSSConstant.POSTAL);
        entityPat.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        entityPat.setLocatorUid(12L);
        entityPat.setCd(NEDSSConstant.HOME);
        entityPatCol.add(entityPat);
        entityPat = new EntityLocatorParticipation();
        entityPat.setClassCd(NEDSSConstant.TELE);
        entityPat.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        entityPat.setLocatorUid(13L);
        entityPatCol.add(entityPat);

        when(entityLocatorParticipationRepository.findByParentUid(10L)).thenReturn(Optional.of(entityPatCol));

        LocalUidGenerator localUid = new LocalUidGenerator();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON)).thenReturn(localUid);

        var phyCol = new ArrayList<PhysicalLocator>();
        var phy = new PhysicalLocator();
        phy.setImageTxt("TEST");
        phyCol.add(phy);
        when(physicalLocatorRepository.findByPhysicalLocatorUids(Collections.singletonList(11L)))
                .thenReturn(Optional.of(phyCol));

        var pstCol = new ArrayList<PostalLocator>();
        var pst = new PostalLocator();
        pst.setCityCd("TEST");
        pst.setCityDescTxt("TEST");
        pst.setCntryCd("TEST");
        pst.setCntryDescTxt("TEST");
        pst.setCntyCd("TEST");
        pst.setCntyDescTxt("TEST");
        pst.setStateCd("TEST");
        pst.setStreetAddr1("TEST");
        pst.setStreetAddr2("TEST");
        pst.setZipCd("TEST");
        pstCol.add(pst);
        when(postalLocatorRepository.findByPostalLocatorUids(Collections.singletonList(12L)))
                .thenReturn(Optional.of(pstCol));


        var teleCol = new ArrayList<TeleLocator>();
        var tele = new TeleLocator();
        tele.setCntryCd("TEST");
        tele.setEmailAddress("TEST");
        tele.setExtensionTxt("TEST");
        tele.setPhoneNbrTxt("TEST");
        tele.setUrlAddress("TEST");
        teleCol.add(tele);
        when(teleLocatorRepository.findByTeleLocatorUids(Collections.singletonList(13L)))
                .thenReturn(Optional.of(teleCol));

        entityLocatorParticipationService.updateEntityLocatorParticipation(locatorCollection, uid);

        verify(physicalLocatorRepository, times(1)).save(any());
        verify(postalLocatorRepository, times(1)).save(any());
        verify(teleLocatorRepository, times(1)).save(any());


    }

    @Test
    void createEntityLocatorParticipation_Success() throws DataProcessingException {
        Long uid = 10L;
        Collection<EntityLocatorParticipationDto> locatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.PHYSICAL);
        var physicalDto = new PhysicalLocatorDto();
        locator.setThePhysicalLocatorDto(physicalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.POSTAL);
        var postTal = new PostalLocatorDto();
        locator.setThePostalLocatorDto(postTal);
        var postalDto = new PostalLocatorDto();
        postalDto.setStreetAddr1("Blah");
        locator.setThePostalLocatorDto(postalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        var teleDto = new TeleLocatorDto();
        teleDto.setPhoneNbrTxt("Blah");
        locator.setClassCd(NEDSSConstant.TELE);
        locator.setTheTeleLocatorDto(teleDto);
        locatorCollection.add(locator);

        LocalUidGenerator localUid = new LocalUidGenerator();
        localUid.setSeedValueNbr(1L);
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON)).thenReturn(localUid);


        entityLocatorParticipationService.createEntityLocatorParticipation(locatorCollection, uid);


        verify(physicalLocatorRepository, times(1)).save(any());
        verify(postalLocatorRepository, times(1)).save(any());
        verify(teleLocatorRepository, times(1)).save(any());
    }


    @Test
    void createEntityLocatorParticipation_Success_2() throws DataProcessingException {
        Long uid = 10L;
        Collection<EntityLocatorParticipationDto> locatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.PHYSICAL);
        var physicalDto = new PhysicalLocatorDto();
        locator.setThePhysicalLocatorDto(physicalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.POSTAL);
        var postTal = new PostalLocatorDto();
        locator.setThePostalLocatorDto(postTal);
        var postalDto = new PostalLocatorDto();
        locator.setThePostalLocatorDto(postalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        var teleDto = new TeleLocatorDto();
        locator.setClassCd(NEDSSConstant.TELE);
        locator.setTheTeleLocatorDto(teleDto);
        locatorCollection.add(locator);

        LocalUidGenerator localUid = new LocalUidGenerator();
        localUid.setSeedValueNbr(1L);
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON)).thenReturn(localUid);


        entityLocatorParticipationService.createEntityLocatorParticipation(locatorCollection, uid);


        verify(physicalLocatorRepository, times(1)).save(any());
        verify(postalLocatorRepository, times(1)).save(any());
        verify(teleLocatorRepository, times(0)).save(any());
    }


    @Test
    void createEntityLocatorParticipation_Success_3() throws DataProcessingException {
        Long uid = 10L;
        Collection<EntityLocatorParticipationDto> locatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.PHYSICAL);
        var physicalDto = new PhysicalLocatorDto();
        locator.setThePhysicalLocatorDto(physicalDto);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.POSTAL);
        locator.setThePostalLocatorDto(null);
        locatorCollection.add(locator);
        locator = new EntityLocatorParticipationDto();
        locator.setClassCd(NEDSSConstant.TELE);
        locator.setTheTeleLocatorDto(null);
        locatorCollection.add(locator);

        LocalUidGenerator localUid = new LocalUidGenerator();
        localUid.setSeedValueNbr(1L);
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON)).thenReturn(localUid);


        entityLocatorParticipationService.createEntityLocatorParticipation(locatorCollection, uid);


        verify(physicalLocatorRepository, times(1)).save(any());
        verify(postalLocatorRepository, times(0)).save(any());
        verify(teleLocatorRepository, times(0)).save(any());
    }


    @Test
    void findEntityLocatorById_Success() {
        long uid = 10L;
        var test = new ArrayList<EntityLocatorParticipation>();
        when(entityLocatorParticipationRepository.findByParentUid(uid)).thenReturn(Optional.of(test));

        var res = entityLocatorParticipationService.findEntityLocatorById(uid);
        assertNotNull(res);


    }


    @Test
    void testDeleteEntityLocatorParticipationWhenDeletePostalIsEmpty() {
        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, 1L);
        verify(personRepository, never()).findByPersonUid(anyLong());
    }

    @Test
    void testDeleteEntityLocatorParticipationWhenPersonResIsEmpty() {
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.empty());

        EntityLocatorParticipationDto dto = createEntityLocatorParticipationDto(NEDSSConstant.POSTAL, true, "BIR");
        locatorCollection.add(dto);

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, 1L);

        verify(postalLocatorRepository).deletePostalLocatorById(any());

    }

    @Test
    void testDeleteEntityLocatorParticipationWhenPostalRevisionIsEmpty() {
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(new Person())));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.empty());

        EntityLocatorParticipationDto dto = createEntityLocatorParticipationDto(NEDSSConstant.POSTAL, true, "BIR");
        locatorCollection.add(dto);

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, 1L);

        verify(postalLocatorRepository).deletePostalLocatorById(any());

    }

    @Test
    void testDeleteEntityLocatorParticipationWhenEntityMprEntityResIsEmpty() {
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(new Person())));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(new PostalLocator())));
        when(entityLocatorParticipationRepository.findByParentUid(anyLong())).thenReturn(Optional.empty());

        EntityLocatorParticipationDto dto = createEntityLocatorParticipationDto(NEDSSConstant.POSTAL, true, "BIR");
        locatorCollection.add(dto);

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, 1L);

        verify(postalLocatorRepository).deletePostalLocatorById(any());
    }

    @Test
    void testDeleteEntityLocatorParticipationWhenEntityMprResIsEmpty() {
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(new Person())));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(new PostalLocator())));
        when(entityLocatorParticipationRepository.findByParentUid(anyLong())).thenReturn(Optional.of(List.of(new EntityLocatorParticipation())));
        when(entityLocatorParticipationRepository.findLocatorUidsByEntityUid(anyLong())).thenReturn(Optional.empty());

        EntityLocatorParticipationDto dto = createEntityLocatorParticipationDto(NEDSSConstant.POSTAL, true, "BIR");
        locatorCollection.add(dto);

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, 1L);

        verify(postalLocatorRepository).deletePostalLocatorById(any());
    }
    private EntityLocatorParticipationDto createEntityLocatorParticipationDto(String classCd, boolean itDelete, String useCd) {
        EntityLocatorParticipationDto dto = new EntityLocatorParticipationDto();
        dto.setClassCd(classCd);
        dto.setItDelete(itDelete);
        dto.setUseCd(useCd);
        return dto;
    }

    @Test
    void testDeleteEntityLocatorParticipationWhenConditionsAreMet() {
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(new Person())));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(new PostalLocator())));
        when(entityLocatorParticipationRepository.findByParentUid(anyLong())).thenReturn(Optional.of(List.of(new EntityLocatorParticipation())));
        when(entityLocatorParticipationRepository.findLocatorUidsByEntityUid(anyLong())).thenReturn(Optional.of(List.of(1L)));

        EntityLocatorParticipationDto dto = new EntityLocatorParticipationDto();
        dto.setClassCd(NEDSSConstant.POSTAL);
        dto.setItDelete(true);
        dto.setUseCd("BIR");
        locatorCollection.add(dto);

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, 1L);

        verify(postalLocatorRepository).deletePostalLocatorById(any());
        verify(entityLocatorParticipationRepository).deleteLocatorById(any(), any());
    }

    @Test
    void testDeleteEntityLocatorParticipationWhenExceptionThrown() {
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(new Person())));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(new PostalLocator())));
        when(entityLocatorParticipationRepository.findByParentUid(anyLong())).thenReturn(Optional.of(List.of(new EntityLocatorParticipation())));
        when(entityLocatorParticipationRepository.findLocatorUidsByEntityUid(anyLong())).thenReturn(Optional.of(List.of(1L)));

        EntityLocatorParticipationDto dto = new EntityLocatorParticipationDto();
        dto.setClassCd(NEDSSConstant.POSTAL);
        dto.setItDelete(true);
        dto.setUseCd("BIR");
        locatorCollection.add(dto);

        doThrow(new RuntimeException()).when(postalLocatorRepository).deletePostalLocatorById(any());

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, 1L);

        verify(postalLocatorRepository).deletePostalLocatorById(any());
        verify(entityLocatorParticipationRepository, never()).deleteLocatorById(any(), any());
    }

    @Test
    void testDeleteEntityLocatorParticipationWithSpecificScenario() {
        Long parentUid = 1L;
        Long patientUid = 2L;

        EntityLocatorParticipationDto dto = createEntityLocatorParticipationDto(NEDSSConstant.POSTAL, true, "BIR");
        dto.setLocatorUid(1L);
        locatorCollection.add(dto);

        EntityLocatorParticipation entityLocator = new EntityLocatorParticipation();
        entityLocator.setLocatorUid(1L);
        entityLocator.setCd("H");
        entityLocator.setUseCd("BIR");

        PostalLocator postalLocator = new PostalLocator();
        postalLocator.setPostalLocatorUid(1L);
        postalLocator.setCityCd("CITY");
        postalLocator.setCityDescTxt("City Description");
        postalLocator.setCntryCd("Country");
        postalLocator.setCntryDescTxt("Country Description");
        postalLocator.setCntyCd("County");
        postalLocator.setCntyDescTxt("County Description");
        postalLocator.setStateCd("State");
        postalLocator.setStreetAddr1("Street 1");
        postalLocator.setStreetAddr2("Street 2");
        postalLocator.setZipCd("Zip");


        var p = new Person();
        p.setPersonParentUid(parentUid);
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(p)));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(postalLocator)));
        when(entityLocatorParticipationRepository.findByParentUid(parentUid)).thenReturn(Optional.of(List.of(entityLocator)));
        when(entityLocatorParticipationRepository.findLocatorUidsByEntityUid(parentUid)).thenReturn(Optional.of(List.of(1L)));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(postalLocator)));

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, patientUid);

        verify(postalLocatorRepository, times(2)).deletePostalLocatorById(1L);
        verify(entityLocatorParticipationRepository).deleteLocatorById(parentUid, 1L);
    }

    @Test
    void testDeleteEntityLocatorParticipationWhenExceptionThrownInDeleteOperations() {
        Long parentUid = 1L;
        Long patientUid = 2L;

        EntityLocatorParticipationDto dto = createEntityLocatorParticipationDto(NEDSSConstant.POSTAL, true, "BIR");
        dto.setLocatorUid(1L);
        locatorCollection.add(dto);

        EntityLocatorParticipation entityLocator = new EntityLocatorParticipation();
        entityLocator.setLocatorUid(1L);
        entityLocator.setCd("H");
        entityLocator.setUseCd("BIR");

        PostalLocator postalLocator = new PostalLocator();
        postalLocator.setPostalLocatorUid(1L);
        postalLocator.setCityCd("CITY");
        postalLocator.setCityDescTxt("City Description");
        postalLocator.setCntryCd("Country");
        postalLocator.setCntryDescTxt("Country Description");
        postalLocator.setCntyCd("County");
        postalLocator.setCntyDescTxt("County Description");
        postalLocator.setStateCd("State");
        postalLocator.setStreetAddr1("Street 1");
        postalLocator.setStreetAddr2("Street 2");
        postalLocator.setZipCd("Zip");
        var p = new Person();
        p.setPersonParentUid(parentUid);
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(p)));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(postalLocator)));
        when(entityLocatorParticipationRepository.findByParentUid(parentUid)).thenReturn(Optional.of(List.of(entityLocator)));
        when(entityLocatorParticipationRepository.findLocatorUidsByEntityUid(parentUid)).thenReturn(Optional.of(List.of(1L)));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(postalLocator)));

        doThrow(new RuntimeException()).when(postalLocatorRepository).deletePostalLocatorById(anyLong());

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, patientUid);

        verify(entityLocatorParticipationRepository, never()).deleteLocatorById(parentUid, 1L);
    }

    @Test
    void testDeleteEntityLocatorParticipationWhenBirCheckIsEmpty() {
        Long parentUid = 1L;
        Long patientUid = 2L;

        EntityLocatorParticipationDto dto = createEntityLocatorParticipationDto(NEDSSConstant.POSTAL, true, "BIR");
        dto.setLocatorUid(1L);
        locatorCollection.add(dto);

        EntityLocatorParticipation entityLocator = new EntityLocatorParticipation();
        entityLocator.setLocatorUid(1L);
        entityLocator.setCd("H");
        entityLocator.setUseCd("HOME");

        PostalLocator postalLocator = new PostalLocator();
        postalLocator.setPostalLocatorUid(1L);
        postalLocator.setCityCd("CITY");
        postalLocator.setCityDescTxt("City Description");
        postalLocator.setCntryCd("Country");
        postalLocator.setCntryDescTxt("Country Description");
        postalLocator.setCntyCd("County");
        postalLocator.setCntyDescTxt("County Description");
        postalLocator.setStateCd("State");
        postalLocator.setStreetAddr1("Street 1");
        postalLocator.setStreetAddr2("Street 2");
        postalLocator.setZipCd("Zip");
        var p = new Person();
        p.setPersonParentUid(parentUid);
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(p)));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(postalLocator)));
        when(entityLocatorParticipationRepository.findByParentUid(parentUid)).thenReturn(Optional.of(List.of(entityLocator)));
        when(entityLocatorParticipationRepository.findLocatorUidsByEntityUid(parentUid)).thenReturn(Optional.of(List.of(1L)));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(postalLocator)));

        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, patientUid);

        verify(postalLocatorRepository).deletePostalLocatorById(any());
    }

    @Test
    void testDeleteEntityLocatorParticipationWhenElseConditionIsMet() {
        Long parentUid = 1L;
        Long patientUid = 2L;

        EntityLocatorParticipationDto dto = createEntityLocatorParticipationDto(NEDSSConstant.POSTAL, true, "BIR");
        dto.setLocatorUid(1L);
        locatorCollection.add(dto);

        EntityLocatorParticipation entityLocator = new EntityLocatorParticipation();
        entityLocator.setLocatorUid(1L);
        entityLocator.setCd("H");
        entityLocator.setUseCd("BIR");

        PostalLocator postalLocator = new PostalLocator();
        postalLocator.setPostalLocatorUid(1L);
        postalLocator.setCityCd("CITY");
        postalLocator.setCityDescTxt("City Description");
        postalLocator.setCntryCd("Country");
        postalLocator.setCntryDescTxt("Country Description");
        postalLocator.setCntyCd("County");
        postalLocator.setCntyDescTxt("County Description");
        postalLocator.setStateCd("State");
        postalLocator.setStreetAddr1("Street 1");
        postalLocator.setStreetAddr2("Street 2");
        postalLocator.setZipCd("Zip");

        var p = new Person();
        p.setPersonParentUid(parentUid);
        when(personRepository.findByPersonUid(anyLong())).thenReturn(Optional.of(List.of(p)));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(postalLocator)));
        when(entityLocatorParticipationRepository.findByParentUid(parentUid)).thenReturn(Optional.of(List.of(entityLocator)));
        when(entityLocatorParticipationRepository.findLocatorUidsByEntityUid(parentUid)).thenReturn(Optional.of(List.of(1L)));
        when(postalLocatorRepository.findByPostalLocatorUids(anyList())).thenReturn(Optional.of(List.of(postalLocator)));

        // Simulate the case where birCheck is empty and there are multiple mprEntityHomeCheck
        when(entityLocatorParticipationRepository.findByParentUid(parentUid)).thenReturn(Optional.of(List.of(entityLocator, entityLocator)));

        // Uncommented the else block in the actual code for this test
        entityLocatorParticipationService.deleteEntityLocatorParticipation(locatorCollection, patientUid);

        // Uncomment the verification if else block is active in the actual method
        // verify(postalLocatorRepository).updatePostalStatus(anyLong(), eq("INACTIVE"));
        // verify(entityLocatorParticipationRepository).updateLocatorStatus(anyLong(), anyLong(), eq("INACTIVE"), eq("I"));
    }
}
