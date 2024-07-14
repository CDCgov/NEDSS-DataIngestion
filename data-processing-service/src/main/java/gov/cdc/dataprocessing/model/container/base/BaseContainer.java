package gov.cdc.dataprocessing.model.container.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@SuppressWarnings("all")
public class BaseContainer implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    protected boolean itNew;
    protected boolean itOld;
    protected boolean itDirty;
    protected boolean itDelete;
    protected String superClassType;
    protected Collection<Object> ldfs;
    public BaseContainer() {

    }

}
