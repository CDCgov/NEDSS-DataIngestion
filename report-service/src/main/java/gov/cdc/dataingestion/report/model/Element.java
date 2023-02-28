package gov.cdc.dataingestion.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.catalina.mapper.Mapper;

import java.util.ArrayList;

/**
 * An element can either be a new element or one based
 *     on previously defined element
 * A name of form [A-Za-z0-9_]+ is a new element
 * A name of form [A-Za-z0-9_]+.[A-Za-z0-9_]+ is an element
 *   based on a previously defined element
 */
public class Element {

  /**
   * Name
   */
  private String name;

    /**
     * Type of the element
     */
   private Type type;

  /**
   * value set
   * Either valueSet or altValues must be defined for a CODE type
   */
  private  String valueSet;

  /**
   *  Value set reference.
   */
   private ValueSet valueSetRef; // set during fixup

 /**
  * Alt values
  */
  private ArrayList<Value> altValues;

  /**
   * table and tableColumn must be defined for a TABLE type
   */
   private  String table;

  // private  LookupTable tableRef; // set during fixup
  /**
   * Table column
   */
 private  String tableColumn; // set during fixup

  /**
   * Cardinality
   */
   private  Cardinality cardinality;

  /**
   * Pii
   */
   private  Boolean pii;

  /**
   * phi
   */
   private  Boolean phi;

  /**
   * used to truncate outgoing formatted String fields.
   * null == no length limit.
   */
   private  Integer maxLength;

  /**
   * default value
   */
  @JsonProperty("default")
   private String defaultSchema;

 /**
  * default overrides value
  */
   private  Boolean defaultOverridesValue;

 /**
  * mapper
  */
   private  String mapper;

 /**
  * Mapper overrides value
  */
   private  Boolean mapperOverridesValue;

 /**
  * mapper ref
  * set during fixup
  */
   private Mapper mapperRef;

 /**
  * Mapper args
  * set during fixup
  */
 private  ArrayList<String> mapperArgs;

 /**
  * Reference
  */
 private  String reference;

 /**
  * Reference Url.
  */
 private  String referenceUrl;

 /**
  * hhs guidance field.
  */
 private  String hhsGuidanceField;

 /**
  * nat flat file field.
  */
 private  String natFlatFileField;

 /**
  * Hl7 field.
  * Format specific information used to format output
  *  HL7 specific information
  */
 private  String hl7Field;

 /**
  * Hl7 Output Fields.
  */
 private ArrayList<String> hl7OutputFields;

 /**
  * Hl7A OE Question.
  */
 private  String hl7AOEQuestion;

    /**
     * The header fields that correspond to an element.
     * A element can output to multiple CSV fields.
     * The first field is considered the primary field. It is used
     * on input define the element
     */
   private  ArrayList<CsvField> csvFields;

    /**
     * FHIR specific information
     */
   private  String fhirField;

    /**
     * a field to let us incorporate documentation data (markdown)
     *   in the schema files so we can generate documentation off of the file
     */
   private  String  documentation;

    /**
     * used for the concatenation mapper. the element carries this
     *  value around and into the mapper itself so the interface for the
     *  mapper remains as generic as possible
     */
   private  String delimiter;

    /**
     *  used to be able to send blank values for fields
     *  that get validated/normalized
     *   in serializers. for instance, a badly formatted
     *   yet optional date field.
     */
   private  Boolean nullifyValue;

    public Element(
            final String name,
            final Type type,
            final String valueSet,
            final ValueSet valueSetRef,
            final ArrayList<Value> altValues,
            final String table,
            final String tableColumn,
            final Cardinality cardinality,
            final Boolean pii,
            final Boolean phi,
            final Integer maxLength,
            final String defaultSchema,
            final Boolean defaultOverridesValue,
            final String mapper,
            final Boolean mapperOverridesValue,
            final Mapper mapperRef,
            final ArrayList<String> mapperArgs,
            final String reference,
            final String referenceUrl,
            final String hhsGuidanceField,
            final String natFlatFileField,
            final String hl7Field,
            final ArrayList<String> hl7OutputFields,
            final String hl7AOEQuestion,
            final ArrayList<CsvField> csvFields,
            final String fhirField,
            final String documentation,
            final String delimiter,
            final Boolean nullifyValue) {
                this.name = name;
                this.type = type;
                this.valueSet = valueSet;
                this.valueSetRef = valueSetRef;
                this.altValues = altValues;
                this.table = table;
                this.tableColumn = tableColumn;
                this.cardinality = cardinality;
                this.pii = pii;
                this.phi = phi;
                this.maxLength = maxLength;
                this.defaultSchema = defaultSchema;
                this.defaultOverridesValue = defaultOverridesValue;
                this.mapper = mapper;
                this.mapperOverridesValue = mapperOverridesValue;
                this.mapperRef = mapperRef;
                this.mapperArgs = mapperArgs;
                this.reference = reference;
                this.referenceUrl = referenceUrl;
                this.hhsGuidanceField = hhsGuidanceField;
                this.natFlatFileField = natFlatFileField;
                this.hl7Field = hl7Field;
                this.hl7OutputFields = hl7OutputFields;
                this.hl7AOEQuestion = hl7AOEQuestion;
                this.csvFields = csvFields;
                this.fhirField = fhirField;
                this.documentation = documentation;
                this.delimiter = delimiter;
                this.nullifyValue = nullifyValue;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getValueSet() {
        return valueSet;
    }

    public void setValueSet(final String valueSet) {
        this.valueSet = valueSet;
    }

    public ValueSet getValueSetRef() {
        return valueSetRef;
    }

    public void setValueSetRef(final ValueSet valueSetRef) {
        this.valueSetRef = valueSetRef;
    }

    public ArrayList<Value> getAltValues() {
        return altValues;
    }

    public void setAltValues(final ArrayList<Value> altValues) {
        this.altValues = altValues;
    }

    public String getTable() {
        return table;
    }

    public void setTable(final String table) {
        this.table = table;
    }

    public String getTableColumn() {
        return tableColumn;
    }

    public void setTableColumn(final String tableColumn) {
        this.tableColumn = tableColumn;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(final Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    public Boolean getPii() {
        return pii;
    }

    public void setPii(final Boolean pii) {
        this.pii = pii;
    }

    public Boolean getPhi() {
        return phi;
    }

    public void setPhi(final Boolean phi) {
        this.phi = phi;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(final Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }

    public void setDefaultSchema(final String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    public Boolean getDefaultOverridesValue() {
        return defaultOverridesValue;
    }

    public void setDefaultOverridesValue(final Boolean defaultOverridesValue) {
        this.defaultOverridesValue = defaultOverridesValue;
    }

    public String getMapper() {
        return mapper;
    }

    public void setMapper(final String mapper) {
        this.mapper = mapper;
    }

    public Boolean getMapperOverridesValue() {
        return mapperOverridesValue;
    }

    public void setMapperOverridesValue(final Boolean mapperOverridesValue) {
        this.mapperOverridesValue = mapperOverridesValue;
    }

    public Mapper getMapperRef() {
        return mapperRef;
    }

    public void setMapperRef(final Mapper mapperRef) {
        this.mapperRef = mapperRef;
    }

    public ArrayList<String> getMapperArgs() {
        return mapperArgs;
    }

    public void setMapperArgs(final ArrayList<String> mapperArgs) {
        this.mapperArgs = mapperArgs;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(final String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public String getHhsGuidanceField() {
        return hhsGuidanceField;
    }

    public void setHhsGuidanceField(final String hhsGuidanceField) {
        this.hhsGuidanceField = hhsGuidanceField;
    }

    public String getNatFlatFileField() {
        return natFlatFileField;
    }

    public void setNatFlatFileField(final String natFlatFileField) {
        this.natFlatFileField = natFlatFileField;
    }

    public String getHl7Field() {
        return hl7Field;
    }

    public void setHl7Field(final String hl7Field) {
        this.hl7Field = hl7Field;
    }

    public ArrayList<String> getHl7OutputFields() {
        return hl7OutputFields;
    }

    public void setHl7OutputFields(final ArrayList<String> hl7OutputFields) {
        this.hl7OutputFields = hl7OutputFields;
    }

    public String getHl7AOEQuestion() {
        return hl7AOEQuestion;
    }

    public void setHl7AOEQuestion(final String hl7AOEQuestion) {
        this.hl7AOEQuestion = hl7AOEQuestion;
    }

    public ArrayList<CsvField> getCsvFields() {
        return csvFields;
    }

    public void setCsvFields(final ArrayList<CsvField> csvFields) {
        this.csvFields = csvFields;
    }

    public String getFhirField() {
        return fhirField;
    }

    public void setFhirField(final String fhirField) {
        this.fhirField = fhirField;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(final String documentation) {
        this.documentation = documentation;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    public Boolean getNullifyValue() {
        return nullifyValue;
    }

    public void setNullifyValue(final Boolean nullifyValue) {
        this.nullifyValue = nullifyValue;
    }
}
