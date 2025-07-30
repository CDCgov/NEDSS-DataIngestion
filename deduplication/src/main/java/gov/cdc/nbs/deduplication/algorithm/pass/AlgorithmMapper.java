package gov.cdc.nbs.deduplication.algorithm.pass;

import java.util.Arrays;
import java.util.List;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements.DataElement;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.AlgorithmContext;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.AlgorithmContext.Advanced;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.AlgorithmContext.LogOdd;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.DibbsPass;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Func;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.Rule;
import gov.cdc.nbs.deduplication.algorithm.model.DibbsAlgorithm.SimilarityMeasure;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingMethod;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.Pass;

public class AlgorithmMapper {
  private final String algorithmName;
  private final boolean includeMultipleMatches;

  public AlgorithmMapper(String algorithmName, boolean includeMultipleMatches) {
    this.algorithmName = algorithmName;
    this.includeMultipleMatches = includeMultipleMatches;
  }

  // Converts NBS Algorithm to DIBBs algorithm format
  public DibbsAlgorithm map(Algorithm algorithm, DataElements dataElements) {
    return new DibbsAlgorithm(
        algorithmName,
        "Algorithm used by NBS",
        false, // updated to true after initial insert
        mapContext(dataElements),
        algorithm.passes()
            .stream()
            .filter(Pass::active)
            .map(p -> mapPass(p, dataElements))
            .toList());
  }

  AlgorithmContext mapContext(DataElements dataElements) {
    List<LogOdd> logOdds = Arrays.stream(MatchingAttribute.values())
        .map(a -> {
          DataElement dataElement = findDataElement(a, dataElements);
          return new LogOdd(a.toString(), dataElement != null ? dataElement.logOdds() : null);
        })
        .filter(lo -> lo.value() != null)
        .toList();

    return new AlgorithmContext(
        includeMultipleMatches,
        logOdds,
        new Advanced(
            SimilarityMeasure.JAROWINKLER,
            0.0,
            0.0));
  }

  DibbsPass mapPass(Pass pass, DataElements dataElements) {
    List<Evaluator> evaluators = pass.matchingCriteria()
        .stream()
        .map(m -> new Evaluator(
            m.attribute(),
            m.method().equals(MatchingMethod.EXACT) ? Func.EXACT : Func.FUZZY,
            m.threshold()))
        .toList();

    List<Double> bounds = calculateBounds(pass, dataElements);

    return new DibbsPass(
        pass.name(),
        pass.blockingCriteria(),
        evaluators,
        Rule.PROBABILISTIC,
        bounds);
  }

  // Convert the the lower and upper bound from UI format (0 -> total log odds) to
  // the format expected by RL (0.00 -> 1.00)
  List<Double> calculateBounds(
      Pass pass,
      DataElements dataElements) {
    final double totalLogOdds = pass.matchingCriteria()
        .stream()
        .mapToDouble(a -> findDataElement(a.attribute(), dataElements).logOdds())
        .sum();

    final double lowerBound = pass.lowerBound() / totalLogOdds;
    final double upperBound = pass.upperBound() / totalLogOdds;

    return List.of(lowerBound, upperBound);
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
      case IDENTIFIER -> dataElements.identifier();
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
