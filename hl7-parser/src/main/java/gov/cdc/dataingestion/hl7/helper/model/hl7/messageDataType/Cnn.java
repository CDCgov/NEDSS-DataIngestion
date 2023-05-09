package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.CNN;
import lombok.Getter;

@Getter
public class Cnn {
    String idNumber;
    String familyName;
    String givenName;
    String secondAndFurtherGivenNameOrInitial;
    String suffix;
    String prefix;
    String degree;
    String sourceTable;
    String assignAuthorityNamespaceId;
    String assignAuthorityUniversalId;
    String assignAuthorityUniversalIdType;
    public Cnn(CNN cnn) {
        this.idNumber = cnn.getIDNumber().getValue();
        this.familyName = cnn.getFamilyName().getValue();
        this.givenName = cnn.getGivenName().getValue();
        this.secondAndFurtherGivenNameOrInitial = cnn.getSecondAndFurtherGivenNamesOrInitialsThereof().getValue();
        this.suffix = cnn.getSuffixEgJRorIII().getValue();
        this.prefix = cnn.getPrefixEgDR().getValue();
        this.degree = cnn.getDegreeEgMD().getValue();
        this.sourceTable = cnn.getSourceTable().getValue();
        this.assignAuthorityNamespaceId = cnn.getAssigningAuthorityNamespaceID().getValue();
        this.assignAuthorityUniversalId = cnn.getAssigningAuthorityUniversalID().getValue();
        this.assignAuthorityUniversalIdType = cnn.getAssigningAuthorityUniversalIDType().getValue();
    }
}
