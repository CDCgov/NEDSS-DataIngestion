package gov.cdc.nbs.deduplication.duplicates.model;

import java.util.Arrays;
import java.util.List;

public record PersonMergeData(
    String asOfDate,
    String comments,
    List<Address> address,
    List<Telecom> telecom,
    List<Name> name,
    List<Identifier> identifiers,
    List<Race> race) {

  public record Address(
      String id,
      String asOfDate,
      String useCode,
      List<String> line,
      String city,
      String state,
      String postalCode,
      String county,
      String censusTract,
      String country,
      String type,
      String comments
  ) {
  }


  public record Telecom(
      String id,
      String asOfDate,
      String useCode,
      String countryCode,
      String phoneNumber,
      String extension,
      String email,
      String url,
      String type,
      String comments
  ) {
  }


  public record Name(
      String personUid,
      String id,
      String asOfDate,
      List<String> given,
      String family,
      String secondFamily,
      String prefix,
      String suffix,
      String degree,
      String type
  ) {
  }


  public record Identifier(
      String id,
      String asOfDate,
      String value,
      String authority,
      String type
  ) {
    public static final List<String> SUPPORTED_IDENTIFIERS = Arrays.asList(
        "AC", "ACSN", "AIN", "AM", "AMA", "AN", "ANC", "AND", "ANON", "ANT", "APRN", "ASID", "BA", "BC",
        "BCFN", "BCT", "BR", "BRN", "BSNR", "CAII", "CC", "CONM", "CY", "CZ", "DC", "DCFN", "DDS", "DEA",
        "DFN", "DI", "DL", "DN", "DO", "DP", "DPM", "DR", "DS", "DSG", "EI", "EN", "ESN", "FDR", "FDRFN",
        "FGN", "FI", "FILL", "GI", "GIN", "GL", "GN", "HC", "IND", "IRISTEM", "JHN", "LACSN", "LANR", "LI",
        "LN", "LR", "MA", "MB", "MC", "MCD", "MCN", "MCR", "MCT", "MD", "MI", "MR", "MRT", "MS", "NBSNR",
        "NCT", "NE", "NH", "NI", "NII", "NIIP", "NP", "NPI", "OBI", "OD", "PA", "PC", "PCN", "PE", "PEN",
        "PGN", "PHC", "PHE", "PHO", "PI", "PIN", "PLAC", "PN", "PNT", "PPIN", "PPN", "PRC", "PRN", "PT",
        "QA", "RI", "RN", "RPH", "RR", "RRI", "RRP", "SAMN", "SB", "SID", "SL", "SN", "SNBSN", "SNO", "SP",
        "SR", "SRX", "SS", "STN", "TAX", "TN", "TPR", "TRL", "U", "UDI", "UPIN", "USID", "VN", "VP", "VS",
        "WC", "WCN", "WP", "XV", "XX"
    );
  }


  public record Race(
      String personUid,
      String id,
      String asOfDate,
      String category
  ) {
  }
}
