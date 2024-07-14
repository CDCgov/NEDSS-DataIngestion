package gov.cdc.dataprocessing.model.dto.lookup;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.io.*;

@Getter
@Setter
@SuppressWarnings("all")
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

    public Object deepCopy() throws CloneNotSupportedException, IOException, ClassNotFoundException
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
