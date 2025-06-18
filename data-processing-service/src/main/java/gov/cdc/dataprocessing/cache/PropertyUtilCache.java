package gov.cdc.dataprocessing.cache;

import java.util.ArrayList;

public class PropertyUtilCache {
    public static ArrayList<Object> cachedHivList = new ArrayList<>();

    public static int kafkaFailedCheckStep1 = 0;
    public static int kafkaFailedCheckStep2 = 0;
    public static int kafkaFailedCheckStep3 = 0;
}
