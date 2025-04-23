package gov.cdc.nbs.deduplication.algorithm.pass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements.DataElement;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.DibbsPass;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Func;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Kwargs;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Rule;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.SimilarityMeasure;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingMethod;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.Pass;

public class AlgorithmMapper {
  private final String algorithmName;

  public AlgorithmMapper(String algorithmName) {
    this.algorithmName = algorithmName;
  }

  // Converts NBS Algorithm to DIBBs algorithm format
  public DibbsAlgorithm map(Algorithm algorithm, DataElements dataElements) {
    return new DibbsAlgorithm(
        algorithmName,
        "Algorithm used by NBS",
        false, // updated to true after initial insert
        true,
        algorithm.passes()
            .stream()
            .filter(Pass::active)
            .map(p -> mapPass(p, dataElements))
            .toList(),
        0.0, // Default to no allowed missing. Will have to discuss with PO on how to handle
        0.0);
  }

  DibbsPass mapPass(Pass pass, DataElements dataElements) {
    Map<String, Double> thresholds = new HashMap<>();
    Map<String, Double> logOdds = new HashMap<>();

    pass.matchingCriteria().forEach(m -> {
      DataElement dataElement = findDataElement(m.attribute(), dataElements);
      thresholds.put(m.attribute().toString(), dataElement.threshold());
      logOdds.put(m.attribute().toString(), dataElement.logOdds());
    });

    List<Evaluator> evaluators = pass.matchingCriteria()
        .stream()
        .map(m -> new Evaluator(
            m.attribute(),
            m.method().equals(MatchingMethod.EXACT) ? Func.EXACT : Func.FUZZY))
        .toList();

    return new DibbsPass(
        pass.blockingCriteria(),
        evaluators,
        Rule.PROBABILISTIC,
        List.of(pass.lowerBound(), pass.upperBound()),
        new Kwargs(
            SimilarityMeasure.JAROWINKLER,
            thresholds,
            logOdds));
  }

  DataElement findDataElement(MatchingAttribute matchingAttribute, DataElements dataElements) {
    if (matchingAttribute == null) {
      throw new PassModificationException("Invalid MatchingAttribute");
    }
    return switch (matchingAttribute) {
      case ACCOUNT_NUMBER -> dataElements.accountNumber();
      case ADDRESS -> dataElements.address();
      case BIRTHDATE -> dataElements.dateOfBirth();
      case CITY -> dataElements.city();
      case COUNTY -> dataElements.county();
      case DRIVERS_LICENSE_NUMBER -> dataElements.driversLicenseNumber();
      case EMAIL -> dataElements.email();
      case FIRST_NAME -> dataElements.firstName();
      case LAST_NAME -> dataElements.lastName();
      case MEDICAID_NUMBER -> dataElements.medicaidNumber();
      case MEDICAL_RECORD_NUMBER -> dataElements.medicalRecordNumber();
      case MEDICARE_NUMBER -> dataElements.medicareNumber();
      case NATIONAL_UNIQUE_INDIVIDUAL_IDENTIFIER -> dataElements.nationalUniqueIdentifier();
      case PATIENT_EXTERNAL_IDENTIFIER -> dataElements.patientExternalIdentifier();
      case PATIENT_INTERNAL_IDENTIFIER -> dataElements.patientInternalIdentifier();
      case PERSON_NUMBER -> dataElements.personNumber();
      case PHONE -> dataElements.telephone();
      case RACE -> dataElements.race();
      case SEX -> dataElements.sex();
      case SOCIAL_SECURITY -> dataElements.socialSecurity();
      case STATE -> dataElements.state();
      case SUFFIX -> dataElements.suffix();
      case VISA_PASSPORT -> dataElements.visaPassport();
      case WIC_IDENTIFIER -> dataElements.wicIdentifier();
      case ZIP -> dataElements.zip();
      default -> throw new PassModificationException("Unsupported matching attribute encountered");
    };
  }
}
