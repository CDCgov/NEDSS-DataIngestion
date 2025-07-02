package gov.cdc.nbs.deduplication.auth.model;

public record OidcLoginRequest(
    String clientId,
    String clientSecret,
    String username,
    String password) {

}
