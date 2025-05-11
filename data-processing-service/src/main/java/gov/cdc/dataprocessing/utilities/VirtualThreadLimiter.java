//package gov.cdc.dataprocessing.utilities;
//
//import gov.cdc.dataprocessing.service.implementation.manager.ManagerAggregationService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.Semaphore;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Component
//public class VirtualThreadLimiter {
//    private static final Logger logger = LoggerFactory.getLogger(VirtualThreadLimiter.class);
//
//    private final AtomicInteger currentLimit = new AtomicInteger(10); // start with 10
//    private volatile Semaphore semaphore = new Semaphore(currentLimit.get());
//
//    public void run(Runnable task) {
//        if (semaphore.tryAcquire()) {
//            Thread.startVirtualThread(() -> {
//                try {
//                    task.run();
//                } finally {
//                    semaphore.release();
//                }
//            });
//        } else {
//            logger.debug("⚠️ Task skipped — concurrency limit reached.");
//        }
//    }
//
//    // Dynamically adjust the limit
//    public synchronized void setLimit(int newLimit) {
//        int oldLimit = currentLimit.get();
//        if (newLimit == oldLimit) return;
//
//        int delta = newLimit - oldLimit;
//        currentLimit.set(newLimit);
//        Semaphore newSemaphore = new Semaphore(newLimit);
//
//        // Preserve number of currently acquired permits
//        int inUse = oldLimit - semaphore.availablePermits();
//        for (int i = 0; i < inUse; i++) {
//            newSemaphore.tryAcquire(); // mark in-use slots in new semaphore
//        }
//
//        semaphore = newSemaphore;
//    }
//
//    public int getCurrentLimit() {
//        return currentLimit.get();
//    }
//}
