package gov.cdc.dataprocessing.model.container.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
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
