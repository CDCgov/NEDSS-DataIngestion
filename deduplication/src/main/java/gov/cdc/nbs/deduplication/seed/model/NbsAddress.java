package gov.cdc.nbs.deduplication.seed.model;

public record NbsAddress(
    String street,
    String street2,
    String city,
    String state,
    String zip,
    String county) {

}