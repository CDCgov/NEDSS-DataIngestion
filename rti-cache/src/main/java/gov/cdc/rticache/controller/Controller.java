package gov.cdc.rticache.controller;

import gov.cdc.rticache.constant.LocalIdClass;
import gov.cdc.rticache.constant.ObjectName;
import gov.cdc.rticache.exception.RtiCacheException;
import gov.cdc.rticache.service.interfaces.IManagerCacheService;
import gov.cdc.rticache.service.interfaces.IOdseIdGeneratorWCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class Controller {
    private final IManagerCacheService managerCacheService;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorWCacheService;

    @Autowired
    public Controller(IManagerCacheService managerCacheService, IOdseIdGeneratorWCacheService odseIdGeneratorWCacheService) {
        this.managerCacheService = managerCacheService;
        this.odseIdGeneratorWCacheService = odseIdGeneratorWCacheService;
    }

    @GetMapping("/status")
    public ResponseEntity<String> getRtiCache() {
        log.info("Data Processing Service Status OK");
        return ResponseEntity.status(HttpStatus.OK).body("Data Processing Service Status OK");
    }

    @GetMapping(path = "/srte/cache/{objectName}/string")
    public ResponseEntity<String> getSrteCacheMapString(@PathVariable String objectName, @RequestParam String key) throws RtiCacheException {
        var res =  managerCacheService.getCache(ObjectName.valueOf(objectName), key);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(path = "/srte/cache/{objectName}/object")
    public ResponseEntity<Object> getSrteCacheObject(@PathVariable String objectName, @RequestParam String key) throws RtiCacheException {
        var res =  managerCacheService.getCacheObject(ObjectName.valueOf(objectName), key);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(path = "/srte/cache/{objectName}/contain")
    public ResponseEntity<Boolean> getSrteCacheMapContain(@PathVariable String objectName, @RequestParam String key) throws RtiCacheException {
        var res =  managerCacheService.containKey(ObjectName.valueOf(objectName), key);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(path = "/odse/localId")
    public ResponseEntity<Object> getOdseLocalId(@RequestParam String localIdClass, @RequestParam boolean geApplied) throws RtiCacheException {
        var res = odseIdGeneratorWCacheService.getValidLocalUid(LocalIdClass.valueOf(localIdClass), geApplied);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(path = "/odse/cache")
    public ResponseEntity<String> getOdseCache() throws RtiCacheException {
        return ResponseEntity.status(HttpStatus.OK).body("Data Processing Service Status OK");
    }
}