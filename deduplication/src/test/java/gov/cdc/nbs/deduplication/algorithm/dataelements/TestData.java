package gov.cdc.nbs.deduplication.algorithm.dataelements;

import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements;
import gov.cdc.nbs.deduplication.algorithm.dataelements.model.DataElements.DataElement;

public class TestData {

  public static final DataElements DATA_ELEMENTS = new DataElements(
      new DataElement(true, 240.0, 9.0),
      new DataElement(true, 20.0, 4.0),
      new DataElement(true, 1.1, 19.625),
      new DataElement(true, 2.2, 29.47),
      new DataElement(true, 3.3, 39.74),
      new DataElement(true, 4.4, 49.72),
      new DataElement(true, 5.5, 59.975),
      new DataElement(true, 20.0, 5.0),
      new DataElement(true, 6.6, 69.23231),
      new DataElement(true, 7.0, 79.552),
      new DataElement(true, 8.8, 89.24),
      new DataElement(true, 9.9, 99.87),
      new DataElement(true, 10.1, 9.01),
      new DataElement(true, 11.2, 1.23),
      new DataElement(true, 12.3, 2.55),
      new DataElement(true, 13.4, 3.44),
      new DataElement(true, 14.5, 3.0),
      new DataElement(true, 15.6, 4.84),
      new DataElement(true, 16.7, 5.224),
      new DataElement(true, 17.8, 6.94),
      new DataElement(true, 20.11, 8.584),
      new DataElement(true, 21.12, .324),
      new DataElement(true, 22.13, 1.14),
      new DataElement(true, 23.14, 2.4),
      new DataElement(true, 24.15, 3.784),
      new DataElement(true, 25.16, 4.84));

  public static final DataElements SPARSE_DATA_ELEMENTS = new DataElements(
      new DataElement(true, 240.0, 9.0),
      new DataElement(true, 20.0, 4.0),
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      new DataElement(true, 24.15, 3.784),
      new DataElement(true, 25.16, 4.84));

}
