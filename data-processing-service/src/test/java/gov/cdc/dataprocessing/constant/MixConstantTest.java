package gov.cdc.dataprocessing.constant;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import org.junit.jupiter.api.Test;

import static gov.cdc.dataprocessing.constant.NBSConstantUtil.configFileName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MixConstantTest {
    @Test
    void testConst() {
        assertNotNull( CTConstants.StdInitiatedWithoutInterviewLong);
        assertNotNull( CTConstants.DeleteStdContactInvCheck);
        assertNotNull( configFileName);
        assertNotNull(NEDSSConstant.QuestionGroupCodes.GROUP_DEM);
        assertNotNull(NEDSSConstant.QuestionGroupCodes.GROUP_MSG);
        assertNotNull(NEDSSConstant.QuestionGroupCodes.GROUP_INV);
        assertNotNull(NEDSSConstant.QuestionGroupCodes.GROUP_DEM);
        assertNotNull(NEDSSConstant.QuestionEntryMethod.USER);
        assertNotNull(NEDSSConstant.QuestionEntryMethod.SYSTEM);


    }

    @Test
    void testCanadaEnum() {
        assertNotNull(NEDSSConstant.CANADA.valueOf("CAN"));
        assertNotNull(NEDSSConstant.CANADA.valueOf("CA"));
        assertNotNull(NEDSSConstant.CANADA.valueOf("PHVS_STATEPROVINCEOFEXPOSURE_CDC_CAN"));
    }

    @Test
    void testUsaEnum() {
        assertNotNull(NEDSSConstant.USA.valueOf("USA"));
        assertNotNull(NEDSSConstant.USA.valueOf("US"));
        assertNotNull(NEDSSConstant.USA.valueOf("PHVS_STATEPROVINCEOFEXPOSURE_CDC_US"));
    }

    @Test
    void testMexicoEnum() {
        assertNotNull(NEDSSConstant.MEXICO.valueOf("MEX"));
        assertNotNull(NEDSSConstant.MEXICO.valueOf("MX"));
        assertNotNull(NEDSSConstant.MEXICO.valueOf("PHVS_STATEPROVINCEOFEXPOSURE_CDC_MEX"));
    }

    @Test
    void testClosureInvestgrEnum() {
        assertNotNull(NEDSSConstant.CLOSURE_INVESTGR.valueOf("ClosureInvestgrOfPHC"));
        assertNotNull(NEDSSConstant.CLOSURE_INVESTGR.valueOf("NBS197"));
    }

    @Test
    void testCurrentInvestgrEnum() {
        assertNotNull(NEDSSConstant.CURRENT_INVESTGR.valueOf("InvestgrOfPHC"));
        assertNotNull(NEDSSConstant.CURRENT_INVESTGR.valueOf("INV180"));
    }

    @Test
    void testContainerTypeEnum() {
        assertNotNull(NEDSSConstant.ContainerType.valueOf("Case"));
        assertNotNull(NEDSSConstant.ContainerType.valueOf("LabReport"));
        assertNotNull(NEDSSConstant.ContainerType.valueOf("Contac"));
        assertNotNull(NEDSSConstant.ContainerType.valueOf("GROUP_DEM"));
    }
}
