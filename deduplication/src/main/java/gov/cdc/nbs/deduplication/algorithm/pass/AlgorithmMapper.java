package gov.cdc.nbs.deduplication.algorithm.pass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements.DataElement;
import gov.cdc.nbs.deduplication.algorithm.pass.exception.PassModificationException;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm.DibbsPass;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm.Func;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm.Kwargs;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm.Rule;
import gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs.DibbsAlgorithm.SimilarityMeasure;
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
                List.of(0.5, 0.85), // Belongingness ratio - will be changing
                algorithm.passes()
                        .stream()
                        .filter(Pass::active)
                        .map(p -> mapPass(p, dataElements))
                        .toList());
    }

    DibbsPass mapPass(Pass pass, DataElements dataElements) {
        Map<String, Double> thresholds = new HashMap<>();
        Map<String, Double> logOdds = new HashMap<>();

        pass.matchingCriteria().forEach(m -> {
            DataElement dataElement = findDataElement(m.matchingAttribute(), dataElements);
            thresholds.put(m.matchingAttribute().toString(), dataElement.threshold());
            logOdds.put(m.matchingAttribute().toString(), dataElement.logOdds());
        });

        List<Evaluator> evaluators = pass.matchingCriteria()
                .stream()
                .map(m -> new Evaluator(
                        m.matchingAttribute(),
                        m.method().equals(MatchingMethod.EXACT) ? Func.EXACT : Func.FUZZY))
                .toList();

        return new DibbsPass(
                pass.blockingCriteria(),
                evaluators,
                Rule.PROBABILISTIC,
                new Kwargs(
                        SimilarityMeasure.JAROWINKLER,
                        thresholds,
                        1.0, // This is not being provided by the UI
                        logOdds));
    }

    DataElement findDataElement(MatchingAttribute matchingAttribute, DataElements dataElements) {
        return switch (matchingAttribute) {
            case ACCOUNT_NUMBER -> dataElements.accountNumber();
            case ADDRESS -> dataElements.streetAddress1();
            case BIRTHDATE -> dataElements.dateOfBirth();
            case CITY -> dataElements.city();
            case COUNTY -> dataElements.county();
            case DRIVERS_LICENSE_NUMBER -> dataElements.driversLicenseNumber();
            case EMAIL -> dataElements.email();
            case FIRST_NAME -> dataElements.firstName();
            case LAST_NAME -> dataElements.lastName();
            case MEDICAID_NUMBER -> dataElements.medicaidNumber();
            case MEDICAL_RECORD_NUMBER -> dataElements.medicalRecordNumber();
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
            case TELECOM -> dataElements.telecom();
            case VISA_PASSPORT -> dataElements.visaPassport();
            case WIC_IDENTIFIER -> dataElements.wicIdentifier();
            case ZIP -> dataElements.zip();
            default -> throw new PassModificationException("Unsupported matching attribute encountered");
        };
    }
}
