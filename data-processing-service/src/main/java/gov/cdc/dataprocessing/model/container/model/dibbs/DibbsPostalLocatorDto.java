package gov.cdc.dataprocessing.model.container.model.dibbs;


import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DibbsPostalLocatorDto {
  private String streetAddr1;
  private String streetAddr2;
  private String cityDescTxt;
  private String stateCd;
  private String cntryDescTxt;
  private String zipCd;

  public DibbsPostalLocatorDto(PostalLocatorDto postalLocatorDto){
    if(postalLocatorDto != null) {
      this.streetAddr1 = postalLocatorDto.getStreetAddr1();
      this.streetAddr2 = postalLocatorDto.getStreetAddr2();
      this.cityDescTxt = postalLocatorDto.getCityDescTxt();
      this.stateCd = postalLocatorDto.getStateCd();
      this.cntryDescTxt = postalLocatorDto.getCntryDescTxt();
      this.zipCd = postalLocatorDto.getZipCd();
    }
  }
}
