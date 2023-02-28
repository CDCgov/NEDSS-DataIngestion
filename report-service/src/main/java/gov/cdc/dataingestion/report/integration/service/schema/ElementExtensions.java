package gov.cdc.dataingestion.report.integration.service.schema;

import gov.cdc.dataingestion.report.model.Element;
import org.checkerframework.common.returnsreceiver.qual.This;

/**
 * Extension for Elements.
 */
public final class ElementExtensions {

    private ElementExtensions() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Adds missing elements from base element.
      * @param thiz element to update
     * @param baseElement base element.
     * @return Element with merged properties from base.
     */
    public static Element mergeWithBase(
            @This final Element thiz,
            final Element baseElement) {
        return new Element(
                thiz.getName(),
                thiz.getType() != null
                        ? thiz.getType()
                        : baseElement.getType(),
                thiz.getValueSet() != null  ? thiz.getValueSet()
                        : baseElement.getValueSet(),
                thiz.getValueSetRef() != null ? thiz.getValueSetRef()
                        : baseElement.getValueSetRef(),
                thiz.getAltValues() != null ? thiz.getAltValues()
                        : baseElement.getAltValues(),
                thiz.getTable() != null
                        ? thiz.getTable()
                        : baseElement.getTable(),
                thiz.getTableColumn() != null ? thiz.getTableColumn()
                        : baseElement.getTableColumn(),
                thiz.getCardinality() != null ? thiz.getCardinality()
                        : baseElement.getCardinality(),
                thiz.getPii() != null ? thiz.getPii()
                        : baseElement.getPii(),
                thiz.getPhi() != null ? thiz.getPhi() : baseElement.getPhi(),
                thiz.getMaxLength() != null
                        ? thiz.getMaxLength()
                        : baseElement.getMaxLength(),
                thiz.getDefaultSchema() != null ? thiz.getDefaultSchema()
                        :  baseElement.getDefaultSchema(),
                thiz.getDefaultOverridesValue() != null
                        ?  thiz.getDefaultOverridesValue()
                        : baseElement.getDefaultOverridesValue(),
                thiz.getMapper() != null
                        ? thiz.getMapper()
                        : baseElement.getMapper(),
                thiz.getMapperOverridesValue() != null
                        ? thiz.getMapperOverridesValue()
                        : baseElement.getMapperOverridesValue(),
                thiz.getMapperRef() != null
                        ? thiz.getMapperRef()
                        : baseElement.getMapperRef(),
                thiz.getMapperArgs() != null
                        ? thiz.getMapperArgs()
                        : baseElement.getMapperArgs(),
                thiz.getReference() != null
                        ? thiz.getReference()
                        : baseElement.getReference(),
                thiz.getReferenceUrl() != null
                        ? thiz.getReferenceUrl()
                        : baseElement.getReferenceUrl(),
                thiz.getHhsGuidanceField() != null
                        ? thiz.getHhsGuidanceField()
                        : baseElement.getHhsGuidanceField(),
                thiz.getNatFlatFileField() != null
                        ? thiz.getNatFlatFileField()
                        : baseElement.getNatFlatFileField(),
                thiz.getHl7Field() != null
                        ? thiz.getHl7Field()
                        : baseElement.getHl7Field(),
                thiz.getHl7OutputFields() != null
                        ?  thiz.getHl7OutputFields()
                        : baseElement.getHl7OutputFields(),
                thiz.getHl7AOEQuestion() != null
                        ?  thiz.getHl7AOEQuestion()
                        : baseElement.getHl7AOEQuestion(),
                thiz.getCsvFields() != null
                        ? thiz.getCsvFields()
                        : baseElement.getCsvFields(),
                thiz.getFhirField() != null
                        ? thiz.getFhirField()
                        : baseElement.getFhirField(),
                thiz.getDocumentation() != null
                        ?  thiz.getDocumentation()
                        : baseElement.getDocumentation(),
                thiz.getDelimiter() != null
                        ? thiz.getDelimiter()
                        : baseElement.getDelimiter(),
                thiz.getNullifyValue() != null
                        ? thiz.getNullifyValue()
                        : baseElement.getNullifyValue());
    }

}
