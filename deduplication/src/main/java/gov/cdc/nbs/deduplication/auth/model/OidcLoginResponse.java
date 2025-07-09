package gov.cdc.nbs.deduplication.auth.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record OidcLoginResponse(
    @JsonAlias("access_token") String accessToken) {

}
