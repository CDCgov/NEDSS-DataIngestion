package gov.cdc.dataprocessing.service.interfaces.manager;

import java.util.concurrent.CompletableFuture;

public interface IManagerCacheService {
    CompletableFuture<Void> loadAndInitCachedValueAsync();
}
