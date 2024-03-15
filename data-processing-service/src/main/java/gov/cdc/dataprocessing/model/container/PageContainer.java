package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.BasePamContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageContainer extends BasePamContainer {

    private boolean isCurrInvestgtrDynamic;
    private static final long serialVersionUID = 1L;

}
