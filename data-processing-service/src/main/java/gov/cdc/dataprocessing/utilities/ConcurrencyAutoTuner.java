//package gov.cdc.dataprocessing.utilities;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ConcurrencyAutoTuner {
//
//    @Autowired
//    private VirtualThreadLimiter limiter;
//
//    @Scheduled(fixedRate = 5000) // every 5 seconds
//    public void adjustConcurrencyLimit() {
//        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//        long maxMemory = Runtime.getRuntime().maxMemory();
//        double usagePercent = (double) usedMemory / maxMemory;
//
//        int newLimit;
//        if (usagePercent > 0.90) {
//            newLimit = 5; // reduce if memory pressure
//        } else if (usagePercent > 0.75) {
//            newLimit = 8;
//        } else {
//            newLimit = 20; // expand when safe
//        }
//
//        limiter.setLimit(newLimit);
//    }
//}