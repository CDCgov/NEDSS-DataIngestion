package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class LdfBaseContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private Collection<Object> ldfUids;

    public Collection<Object> getTheStateDefinedFieldDataDTCollection() {
        return ldfs;
    }

    /* Read all input ldfs. Descard one with no value entered by user */
    public void setTheStateDefinedFieldDataDTCollection(List<StateDefinedFieldDataDto> newLdfs) {
        if(newLdfs != null && !newLdfs.isEmpty()){
            ldfs = new ArrayList<Object> ();
            ldfUids = new ArrayList<Object>();
            Iterator<StateDefinedFieldDataDto> itr = newLdfs.iterator();
            while (itr.hasNext()) {
                StateDefinedFieldDataDto dt = (StateDefinedFieldDataDto) itr.next();
                ldfUids.add(dt.getLdfUid());
                if (dt != null && dt.getLdfValue() != null
                        && dt.getLdfValue().trim().length() != 0) {
                    ldfs.add(dt);
                }
            }
        }
    }
}
