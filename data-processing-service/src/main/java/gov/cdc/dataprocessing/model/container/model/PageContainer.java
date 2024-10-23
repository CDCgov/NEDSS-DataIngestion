package gov.cdc.dataprocessing.model.container.model;


import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class PageContainer extends BasePamContainer {

    private boolean isCurrInvestgtrDynamic;
    private static final long serialVersionUID = 1L;

}
