package gov.cdc.dataingestion.share.helper;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TimeStampHelperTest {

    @Test
    void testGetCurrentTimeStampWithEnvTimeZone() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //TimeStampHelper
        Constructor<TimeStampHelper> pcc = TimeStampHelper.class.getDeclaredConstructor();
        pcc.setAccessible(true);
        TimeStampHelper privateConstructorInstance = pcc.newInstance();
        privateConstructorInstance.setEnvTimeZone("UTC");
        assertEquals("UTC", privateConstructorInstance.getEnvTimeZone());
        assertNotNull(TimeStampHelper.getCurrentTimeStamp());
    }

    @Test
    void testGetCurrentTimeStampWithEmptyEnvTimeZone() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //TimeStampHelper
        Constructor<TimeStampHelper> pcc = TimeStampHelper.class.getDeclaredConstructor();
        pcc.setAccessible(true);
        TimeStampHelper privateConstructorInstance = pcc.newInstance();
        privateConstructorInstance.setEnvTimeZone("");
        assertEquals("", privateConstructorInstance.getEnvTimeZone());
        assertNotNull(TimeStampHelper.getCurrentTimeStamp());
    }

    @Test
    void testGetCurrentTimeStampWithNullEnvTimeZone() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //TimeStampHelper
        Constructor<TimeStampHelper> pcc = TimeStampHelper.class.getDeclaredConstructor();
        pcc.setAccessible(true);
        TimeStampHelper privateConstructorInstance = pcc.newInstance();
        privateConstructorInstance.setEnvTimeZone(null);
        assertEquals(null, privateConstructorInstance.getEnvTimeZone());
        assertNotNull(TimeStampHelper.getCurrentTimeStamp());
    }
}