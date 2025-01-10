package gov.cdc.srtedataservice.controller;

import com.google.gson.Gson;
import gov.cdc.srtedataservice.cache_model.LocalUidCacheModel;
import gov.cdc.srtedataservice.constant.LocalIdClass;
import gov.cdc.srtedataservice.constant.ObjectName;
import gov.cdc.srtedataservice.exception.RtiCacheException;
import gov.cdc.srtedataservice.service.OdseIdGeneratorWCacheService;
import gov.cdc.srtedataservice.service.interfaces.IManagerCacheService;
import gov.cdc.srtedataservice.service.interfaces.IOdseIdGeneratorWCacheService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class); // NOSONAR

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

    @GetMapping(path = "/srte/cache/string/{objectName}")
    public ResponseEntity<String> getSrteCacheMapString(@PathVariable String objectName, @RequestParam String key) throws RtiCacheException {
        var res =  managerCacheService.getCache(ObjectName.valueOf(objectName), key);
        logger.info("/srte/cache/string/{}?key={}", objectName, key);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(path = "/srte/cache/object/{objectName}")
    public ResponseEntity<Object> getSrteCacheObject(@PathVariable String objectName, @RequestParam String key) throws RtiCacheException {
        var res =  managerCacheService.getCacheObject(ObjectName.valueOf(objectName), key);
        logger.info("/srte/cache/object/{}?key={}", objectName, key);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(path = "/srte/cache/contain/{objectName}")
    public ResponseEntity<Boolean> getSrteCacheMapContain(@PathVariable String objectName, @RequestParam String key) throws RtiCacheException {
        var res =  managerCacheService.containKey(ObjectName.valueOf(objectName), key);
        logger.info("/srte/cache/contain/{}?key={}", objectName, key);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(path = "/odse/localId")
    public ResponseEntity<Object> getOdseLocalId(@RequestParam String localIdClass, @RequestParam boolean geApplied) throws RtiCacheException {
        var res = odseIdGeneratorWCacheService.getValidLocalUid(LocalIdClass.valueOf(localIdClass), geApplied);
        logger.info("/odse/localId/{}?geApplied={}", localIdClass, geApplied);
        Gson gson = new Gson();
        logger.info("Seed {}", gson.toJson(LocalUidCacheModel.localUidMap));
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}