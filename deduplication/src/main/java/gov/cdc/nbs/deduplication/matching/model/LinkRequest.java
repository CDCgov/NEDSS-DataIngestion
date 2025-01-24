package gov.cdc.nbs.deduplication.matching.model;

import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

@SuppressWarnings({ "squid:S6213" }) // we don't control the name of this field
public record LinkRequest(MpiPerson record) {

}
