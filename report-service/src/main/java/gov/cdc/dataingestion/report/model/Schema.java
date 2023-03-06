package gov.cdc.dataingestion.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Schema details.
 */
public class Schema {
    /**
     * Name.
     */
    private  String name;

    /**
     * Topic.
     */
    private String topic;

    /**
     * List of elements.
     */
    private ArrayList<Element> elements;

    /**
     * Tracking element.
     * the element to use for tracking this test
     */
    private String trackingElement;

    /**
     * Description.
     */
    private String description;

    /**
     * Reference Url.
     */
    private String referenceUrl;

    /**
     * extends
     */
    @JsonProperty("extends")
    private String extendsSchema;

    /**
     * extends Ref
     */
    private Schema extendsRef;

    /**
     * Based on schema
     */
    private String basedOn;

    /**
     * Based on ref schema.
     */
    private Schema basedOnRef;

    public String getName() {
        return name;
    }

    public String getExtendsSchema() {
        return extendsSchema;
    }

    public void setExtendsSchema(final String extendsSchema) {
        this.extendsSchema = extendsSchema;
    }


    public Schema(
            final String name,
            final String topic,
            final ArrayList<Element> elements,
            final String trackingElement,
            final String description,
            final String referenceUrl,
            final String extendsSchema,
            final Schema extendsRef,
            final String basedOn,
            final Schema basedOnRef) {
        this.name = name;
        this.topic = topic;
        this.elements = elements;
        this.trackingElement = trackingElement;
        this.description = description;
        this.referenceUrl = referenceUrl;
        this.extendsSchema = extendsSchema;
        this.extendsRef = extendsRef;
        this.basedOn = basedOn;
        this.basedOnRef = basedOnRef;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(final ArrayList<Element> elements) {
        this.elements = elements;
    }

    public String getTrackingElement() {
        return trackingElement;
    }

    public void setTrackingElement(final String trackingElement) {
        this.trackingElement = trackingElement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(final String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public Schema getExtendsRef() {
        return extendsRef;
    }

    public void setExtendsRef(final Schema extendsRef) {
        this.extendsRef = extendsRef;
    }

    public String getBasedOn() {
        return basedOn;
    }

    public void setBasedOn(final String basedOn) {
        this.basedOn = basedOn;
    }

    public Schema getBasedOnRef() {
        return basedOnRef;
    }

    public void setBasedOnRef(final Schema basedOnRef) {
        this.basedOnRef = basedOnRef;
    }
}
