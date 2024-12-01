package gov.cdc.nbs.dibbs.nbs_deduplication.model;

import lombok.Data;

@Data
public class PostalLocatorDto {
  private String streetAddr1;
  private String streetAddr2;
  private String cityDescTxt;
  private String stateCd;
  private String cntryDescTxt;
  private String zipCd;
}
