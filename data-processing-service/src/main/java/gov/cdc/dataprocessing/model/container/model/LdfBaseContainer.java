package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter

public class LdfBaseContainer extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private Collection<Object> ldfUids;

    public Collection<Object> getTheStateDefinedFieldDataDTCollection() {
        return ldfs;
    }

    /* Read all input ldfs. Descard one with no value entered by user */
    public void setTheStateDefinedFieldDataDTCollection(List<StateDefinedFieldDataDto> newLdfs) {
        if(newLdfs != null && !newLdfs.isEmpty()){
            ldfs = new ArrayList<> ();
            ldfUids = new ArrayList<>();
            for (StateDefinedFieldDataDto dt : newLdfs) {
                ldfUids.add(dt.getLdfUid());
                if (dt.getLdfValue() != null && !dt.getLdfValue().trim().isEmpty()) {
                    ldfs.add(dt);
                }
            }
        }
    }
}
