package gov.cdc.dataingestion.report.model;

import java.util.List;

/**
 * Value set.
 */
public class  ValueSet {

    /**
     * Name.
     */
    private String name;

    /**
     * Reference Url.
     */
    private  String referenceUrl;

    /**
     * Values.
     */
    private List<Value> values;

    /**
     * Version.
     */
    private String version;

    /**
     * System.
     */
    private  SetSystem system;

    /**
     *Reference.
     */
    private  String reference;

    public ValueSet() { }

    public ValueSet(
            final String name,
            final String referenceUrl,
            final List<Value> values,
            final String version,
            final SetSystem system,
            final String reference) {
        this.name = name;
        this.referenceUrl = referenceUrl;
        this.values = values;
        this.version = version;
        this.system = system;
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "ValueSet{"
                +
                "name='"
                + name
                + '\''
                +
                ", referenceUrl='"
                + referenceUrl
                + '\''
                +
                ", values="
                + values
                +
                ", version='"
                + version
                + '\''
                +
                ", system="
                + system
                +
                ", reference='"
                + reference
                + '\''
                +
                '}';
    }

    /**
     * returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * returns system.
     * @return system.
     */
    public SetSystem getSystem() {
        return system;
    }

    /**
     * returns reference.
     * @return reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Reference url.
     * @return reference Url.
     */
    public String getReferenceUrl() {
        return referenceUrl;
    }

    /**
     * values getter
     * @return values.
     */
    public List<Value> getValues() {
        return values;
    }

    /**
     * getter for version.
     * @return version
     */
    public String getVersion() {
        return version;
    }


    public void setName(final String name) {
        this.name = name;
    }

    public void setReferenceUrl(final String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public void setValues(final List<Value> values) {
        this.values = values;
    }

    public void setVersion(final String version) {
        this.version = version;
    }


    public void setReference(final String reference) {
        this.reference = reference;
    }

    public void setSystem(final SetSystem system) {
        this.system = system;
    }
}
