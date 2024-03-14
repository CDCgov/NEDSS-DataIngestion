package gov.cdc.dataprocessing.model.container;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
public class BaseContainer implements Serializable, Cloneable
{
    public BaseContainer()
    {

    }
    private static final long serialVersionUID = 1L;
    protected boolean itNew;
    protected boolean itOld;
    protected boolean itDirty;
    protected boolean itDelete;
    protected String superClassType;
    protected Collection<Object> ldfs;
    /**
     @param objectname1
     @param objectname2
     @param voClass
     @return boolean
     @roseuid 3BB8B67D021A
     */
//    public abstract boolean isEqual(java.lang.Object objectname1, java.lang.Object objectname2, java.lang.Class<?> voClass);
}
