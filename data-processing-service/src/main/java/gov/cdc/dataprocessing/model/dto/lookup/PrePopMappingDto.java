package gov.cdc.dataprocessing.model.dto.lookup;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.io.*;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class PrePopMappingDto extends BaseContainer {

    private static final long serialVersionUID = 1L;
    private Long lookupQuestionUid;
    private Long lookupAnswerUid;
    private String fromQuestionIdentifier;
    private String fromCodeSystemCode;
    private String fromDataType;
    private String fromFormCd;
    private String toFormCd;
    private String toQuestionIdentifier;
    private String toCodeSystemCd;
    private String toDataType;
    private String fromAnswerCode;
    private String fromAnsCodeSystemCd;
    private String toAnswerCode;
    private String toAnsCodeSystemCd;

    public PrePopMappingDto() {

    }

    public PrePopMappingDto(LookupMappingDto lookupMappingDto) {
        this.lookupQuestionUid = lookupMappingDto.getLookupQuestionUid();
        this.lookupAnswerUid = lookupMappingDto.getLookupAnswerUid();
        this.fromQuestionIdentifier = lookupMappingDto.getFromQuestionIdentifier();
        this.fromCodeSystemCode = lookupMappingDto.getFromCodeSystemCd();
        this.fromDataType = lookupMappingDto.getFromDataType();
        this.fromFormCd = lookupMappingDto.getFromFormCd();
        this.toFormCd = lookupMappingDto.getToFormCd();
        this.toQuestionIdentifier = lookupMappingDto.getToQuestionIdentifier();
        this.toCodeSystemCd = lookupMappingDto.getToCodeSystemCd();
        this.toDataType = lookupMappingDto.getToDataType();
        this.fromAnswerCode = lookupMappingDto.getFromAnswerCode();
        this.fromAnsCodeSystemCd = lookupMappingDto.getFromAnsCodeSystemCd();
        this.toAnswerCode = lookupMappingDto.getToAnswerCode();
        this.toAnsCodeSystemCd = lookupMappingDto.getToAnsCodeSystemCd();
    }

    public Object deepCopy() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object deepCopy = ois.readObject();

        return  deepCopy;
    }
}
