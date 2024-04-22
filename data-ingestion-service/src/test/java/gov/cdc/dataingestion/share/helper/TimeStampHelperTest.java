package gov.cdc.dataingestion.share.helper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TimeStampHelperTest {

    @ParameterizedTest
    @ValueSource(strings = {"UTC", ""})
    @NullSource
    void testGetCurrentTimeStampWithEnvTimeZone(String timeStamp) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<TimeStampHelper> pcc = TimeStampHelper.class.getDeclaredConstructor();
        pcc.setAccessible(true);
        TimeStampHelper privateConstructorInstance = pcc.newInstance();
        privateConstructorInstance.setEnvTimeZone(timeStamp);
        assertEquals(timeStamp, privateConstructorInstance.getEnvTimeZone());
        assertNotNull(TimeStampHelper.getCurrentTimeStamp());
    }
}