package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
    @Value("${nbs.user}")
    private String nbsUser = "";
    private final IAuthUserService authUserService;
    private final QueryHelper queryHelper;
    private final ILookupService lookupService;
    private final UidPoolManager uidPoolManager;

    public SchedulerService(IAuthUserService authUserService, QueryHelper queryHelper, ILookupService lookupService, UidPoolManager uidPoolManager) {
        this.authUserService = authUserService;
        this.queryHelper = queryHelper;
        this.lookupService = lookupService;
        this.uidPoolManager = uidPoolManager;
    }

    @Scheduled(fixedDelay = 1800000) // every 30 min
    public void populateAuthUser() throws DataProcessingException {
        AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
        AuthUtil.setGlobalAuthUser(profile);
        logger.info("Completed populateAuthUser");
    }

    @Scheduled(fixedDelay = 3600000) // every 1 hr
    public void populateHashPAJList() throws DataProcessingException {
        logger.info("Started populateHashPAJList");
        OdseCache.OWNER_LIST_HASHED_PA_J =  queryHelper.getHashedPAJList(false);
        OdseCache.GUEST_LIST_HASHED_PA_J = queryHelper.getHashedPAJList(true);
        logger.info("Completed populateHashPAJList");
    }

    @Scheduled(fixedDelay = 3600000) // every 1 hr
    public void populateDMBQuestionMap() {
        logger.info("Started populateDMBQuestionMap");
        OdseCache.DMB_QUESTION_MAP = lookupService.getDMBQuestionMapAfterPublish();
        logger.info("Completed populateDMBQuestionMap");
    }

    @Scheduled(fixedDelay = 60000)
    public void refillUid() {
        uidPoolManager.periodicRefill();
    }

}
