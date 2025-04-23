package gov.cdc.nbs.deduplication.algorithm.pass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import gov.cdc.nbs.deduplication.algorithm.dataelements.TestData;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.DibbsPass;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Func;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Rule;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.SimilarityMeasure;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.BlockingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingAttributeEntry;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingMethod;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.Pass;

class AlgorithmMapperTest {

  private final AlgorithmMapper mapper = new AlgorithmMapper("nbs");

  @Test
  void should_set_name() {
    DibbsAlgorithm actual = mapper.map(new Algorithm(new ArrayList<>()), TestData.DATA_ELEMENTS);
    assertThat(actual.label()).isEqualTo("nbs");
  }

  @Test
  void should_set_description() {
    DibbsAlgorithm actual = mapper.map(new Algorithm(new ArrayList<>()), TestData.DATA_ELEMENTS);
    assertThat(actual.description()).isEqualTo("Algorithm used by NBS");
  }

  @Test
  void should_set_default() {
    DibbsAlgorithm actual = mapper.map(new Algorithm(new ArrayList<>()), TestData.DATA_ELEMENTS);
    assertThat(actual.isDefault()).isFalse();
  }

  @Test
  void should_set_multiple_matches() {
    DibbsAlgorithm actual = mapper.map(new Algorithm(new ArrayList<>()), TestData.DATA_ELEMENTS);
    assertThat(actual.includeMultipleMatches()).isTrue();
  }

  @Test
  void should_set_missing_thresholds() {
    DibbsAlgorithm actual = mapper.map(new Algorithm(new ArrayList<>()), TestData.DATA_ELEMENTS);
    assertThat(actual.missingAllowedProportion()).isEqualTo(0.0);
    assertThat(actual.missingPointsProportion()).isEqualTo(0.0);
  }

  @Test
  void should_set_passes() {
    DibbsAlgorithm actual = mapper.map(new Algorithm(new ArrayList<>()), TestData.DATA_ELEMENTS);
    assertThat(actual.passes()).isEmpty();
  }

  @Test
  void should_map_pass() {
    DibbsPass pass = mapper.mapPass(
        new Pass(
            1l,
            "pass name",
            "pass description",
            true,
            List.of(BlockingAttribute.ADDRESS, BlockingAttribute.SEX),
            List.of(
                new MatchingAttributeEntry(MatchingAttribute.FIRST_NAME, MatchingMethod.EXACT),
                new MatchingAttributeEntry(MatchingAttribute.LAST_NAME, MatchingMethod.JAROWINKLER)),
            0.25,
            0.90),
        TestData.DATA_ELEMENTS);

    assertThat(pass.blockingKeys()).isEqualTo(List.of(BlockingAttribute.ADDRESS, BlockingAttribute.SEX));

    assertThat(pass.evaluators()).satisfiesExactly(
        e1 -> assertThat(e1).isEqualTo(new Evaluator(MatchingAttribute.FIRST_NAME, Func.EXACT)),
        e2 -> assertThat(e2).isEqualTo(new Evaluator(MatchingAttribute.LAST_NAME, Func.FUZZY)));

    assertThat(pass.evaluators().get(0).func())
        .hasToString("func:recordlinker.linking.matchers.compare_probabilistic_exact_match");
    assertThat(pass.evaluators().get(1).func())
        .hasToString("func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match");

    assertThat(pass.rule()).isEqualTo(Rule.PROBABILISTIC);
    assertThat(pass.matchWindow()).containsExactly(0.25, 0.90);

    assertThat(pass.kwargs().similarityMeasure()).isEqualTo(SimilarityMeasure.JAROWINKLER);
    assertThat(pass.kwargs().thresholds()).containsOnly(
        entry(MatchingAttribute.FIRST_NAME.toString(), TestData.DATA_ELEMENTS.firstName().threshold()),
        entry(MatchingAttribute.LAST_NAME.toString(), TestData.DATA_ELEMENTS.lastName().threshold()));

    assertThat(pass.kwargs().logOdds()).containsOnly(
        entry(MatchingAttribute.FIRST_NAME.toString(), TestData.DATA_ELEMENTS.firstName().logOdds()),
        entry(MatchingAttribute.LAST_NAME.toString(), TestData.DATA_ELEMENTS.lastName().logOdds()));

  }

  @Test
  void should_map_data_element() {
    assertThat(mapper.findDataElement(MatchingAttribute.FIRST_NAME, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.firstName());
    assertThat(mapper.findDataElement(MatchingAttribute.LAST_NAME, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.lastName());
    assertThat(mapper.findDataElement(MatchingAttribute.SUFFIX, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.suffix());
    assertThat(mapper.findDataElement(MatchingAttribute.BIRTHDATE, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.dateOfBirth());
    assertThat(mapper.findDataElement(MatchingAttribute.SEX, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.sex());
    assertThat(mapper.findDataElement(MatchingAttribute.RACE, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.race());
    assertThat(mapper.findDataElement(MatchingAttribute.ADDRESS, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.address());
    assertThat(mapper.findDataElement(MatchingAttribute.CITY, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.city());
    assertThat(mapper.findDataElement(MatchingAttribute.STATE, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.state());
    assertThat(mapper.findDataElement(MatchingAttribute.ZIP, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.zip());
    assertThat(mapper.findDataElement(MatchingAttribute.COUNTY, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.county());
    assertThat(mapper.findDataElement(MatchingAttribute.PHONE, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.telephone());
    assertThat(mapper.findDataElement(MatchingAttribute.EMAIL, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.email());
    validateIdentifications();
  }

  private void validateIdentifications() {
    assertThat(mapper.findDataElement(MatchingAttribute.ACCOUNT_NUMBER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.accountNumber());
    assertThat(mapper.findDataElement(MatchingAttribute.DRIVERS_LICENSE_NUMBER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.driversLicenseNumber());
    assertThat(mapper.findDataElement(MatchingAttribute.MEDICAID_NUMBER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.medicaidNumber());
    assertThat(mapper.findDataElement(MatchingAttribute.MEDICAL_RECORD_NUMBER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.medicalRecordNumber());
    assertThat(mapper.findDataElement(MatchingAttribute.MEDICARE_NUMBER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.medicareNumber());
    assertThat(
        mapper.findDataElement(MatchingAttribute.NATIONAL_UNIQUE_INDIVIDUAL_IDENTIFIER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.nationalUniqueIdentifier());
    assertThat(mapper.findDataElement(MatchingAttribute.PATIENT_EXTERNAL_IDENTIFIER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.patientExternalIdentifier());
    assertThat(mapper.findDataElement(MatchingAttribute.PATIENT_INTERNAL_IDENTIFIER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.patientInternalIdentifier());
    assertThat(mapper.findDataElement(MatchingAttribute.PERSON_NUMBER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.personNumber());
    assertThat(mapper.findDataElement(MatchingAttribute.SOCIAL_SECURITY, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.socialSecurity());
    assertThat(mapper.findDataElement(MatchingAttribute.VISA_PASSPORT, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.visaPassport());
    assertThat(mapper.findDataElement(MatchingAttribute.WIC_IDENTIFIER, TestData.DATA_ELEMENTS))
        .isEqualTo(TestData.DATA_ELEMENTS.wicIdentifier());
  }

  @Test
  void should_fail_to_map() {
    PassModificationException ex = assertThrows(
        PassModificationException.class,
        () -> mapper.findDataElement(null, TestData.DATA_ELEMENTS));
    assertThat(ex.getMessage()).isEqualTo("Invalid MatchingAttribute");
  }
}
