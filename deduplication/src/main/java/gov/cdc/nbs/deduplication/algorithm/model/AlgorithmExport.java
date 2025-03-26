package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;

public record AlgorithmExport(
        DataElements dataElements,
        Algorithm algorithm) {

}
