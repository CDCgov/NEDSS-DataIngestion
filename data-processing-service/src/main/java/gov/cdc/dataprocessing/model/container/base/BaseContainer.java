package gov.cdc.dataprocessing.model.container.base;

import java.io.Serializable;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseContainer implements Serializable, Cloneable {
  public BaseContainer() {}

  private static final long serialVersionUID = 1L;
  protected boolean itNew;
  protected boolean itOld;
  protected boolean itDirty;
  protected boolean itDelete;
  protected String superClassType;
  protected Collection<Object> ldfs;
}
