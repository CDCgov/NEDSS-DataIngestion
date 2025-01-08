package gov.cdc.dataprocessing.utilities.time;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimeStampUtilTest {
    @Test
    void getTimeStamp() {
        var test = TimeStampUtil.getCurrentTimeStamp("America/New_York");
        var testAz = TimeStampUtil.getCurrentTimeStamp("America/Phoenix");

        Assertions.assertTrue(test.after(testAz));
    }

    @Test
    void getCurrentTimeStampPlusOneHourTest() {
        var test = TimeStampUtil.getCurrentTimeStampPlusOneHour("America/New_York");
        var testBeforeOneHour = TimeStampUtil.getCurrentTimeStamp("America/New_York");
        Assertions.assertTrue(test.after(testBeforeOneHour));
    }

    @Test
    void getCurrentTimeStampPlusOneDayTest() {
        var test = TimeStampUtil.getCurrentTimeStampPlusOneDay("America/New_York");
        var testBeforeOneDay = TimeStampUtil.getCurrentTimeStamp("America/New_York");
        Assertions.assertTrue(test.after(testBeforeOneDay));
    }

    @Test
    void convertTimestampToStringTest() {
        var test = TimeStampUtil.convertTimestampToString("America/New_York");
        Assertions.assertNotNull(test);
    }

    @Test
    void convertStringToTimestampTest() throws DataProcessingException {
        var timeStr = "01/08/2025 16:15:01";
        var test = TimeStampUtil.convertStringToTimestamp(timeStr);
        Assertions.assertNotNull(test);
    }

    @Test
    void timestampOrgNameDtoTest() {
        var org = new OrganizationNameDto("America/New_York");

        var statusTime = org.getStatusTime();
        var recordStatusTime = org.getRecordStatusTime();
        var lastChange = org.getLastChgTime();

        Assertions.assertNotNull(statusTime);
        Assertions.assertNotNull(recordStatusTime);
        Assertions.assertNotNull(lastChange);
    }

    @Test
    void timestampEntityIdTest() {
        var entity = new EntityId(new EntityIdDto(), "America/New_York");
        var time = entity.getRecordStatusTime();
        Assertions.assertNotNull(time);
    }
}
