package gov.cdc.srtedataservice.model.container.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@SuppressWarnings("java:S3740")
public class ProgramAreaContainer implements Serializable, Comparable
{

    private String conditionCd;
    private String conditionShortNm;
    private String stateProgAreaCode;
    private String stateProgAreaCdDesc;
    private String investigationFormCd;

    @Override
    public int compareTo(Object o) {
        return getConditionShortNm().compareTo( ((ProgramAreaContainer) o).getConditionShortNm() );
    }
}
