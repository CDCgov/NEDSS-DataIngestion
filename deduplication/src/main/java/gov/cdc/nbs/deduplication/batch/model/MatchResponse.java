package gov.cdc.nbs.deduplication.batch.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.UUID;

public record MatchResponse(
    @JsonProperty("prediction") Prediction prediction,
    @JsonProperty("person_reference_id") UUID personReferenceId,
    @JsonProperty("results") List<LinkResult> results) {
  public enum Prediction {
    MATCH("certain"),
    POSSIBLE_MATCH("possible"),
    NO_MATCH("certainly-not");

    private final String value;

    Prediction(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @JsonCreator
    public static Prediction fromValue(String value) {
      for (Prediction prediction : Prediction.values()) {
        if (prediction.value.equals(value)) {
          return prediction;
        }
      }
      throw new IllegalArgumentException("Unknown value: " + value);
    }
  }
}
