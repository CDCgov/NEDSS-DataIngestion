package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dt.StateDefinedFieldDataDT;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Getter
@Setter
public class LdfBaseVO extends  AbstractVO{
    private static final long serialVersionUID = 1L;
    private Collection<Object> ldfUids;

    public Collection<Object> getTheStateDefinedFieldDataDTCollection() {
        return ldfs;
    }

    /* Read all input ldfs. Descard one with no value entered by user */
    public void setTheStateDefinedFieldDataDTCollection(Collection<Object> newLdfs) {
        if(newLdfs != null && newLdfs.size() > 0){
            ldfs = new ArrayList<Object> ();
            ldfUids = new ArrayList<Object>();
            Iterator<Object> itr = newLdfs.iterator();
            while (itr.hasNext()) {
                StateDefinedFieldDataDT dt = (StateDefinedFieldDataDT) itr.next();
                ldfUids.add(dt.getLdfUid());
                if (dt != null && dt.getLdfValue() != null
                        && dt.getLdfValue().trim().length() != 0) {
                    ldfs.add(dt);
                }
            }
        }
    }
}
