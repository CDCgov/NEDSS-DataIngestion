package gov.cdc.dataingestion.report.model;

import java.util.Objects;

/**
 * Value.
 */
public class Value {

    /**
     * Code.
     */
    private String code;
    /**
     * Display.
     */
    private String display;

    /**
     *
     */
    private String version;

    /**
     * replaces is used in the case of an altValue that needs to be used instead
     *  of what is normally used in the valueSet. for example, a DOH might want
     *  to use 'U' instead of 'UNK' for Y/N/UNK values
     */
    private String replaces;

    /**
     * System.
     */
    private SetSystem system;

    public Value() { }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(final String display) {
        this.display = display;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getReplaces() {
        return replaces;
    }

    public void setReplaces(final String replaces) {
        this.replaces = replaces;
    }

    public SetSystem getSystem() {
        return system;
    }
    public void setSystem(final SetSystem system) {
        this.system = system;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Value value)) {
            return false;
        }
        return Objects.equals(
                getCode(), value.getCode())
                && Objects.equals(getDisplay(), value.getDisplay())
                && Objects.equals(getVersion(), value.getVersion())
                && Objects.equals(getReplaces(), value.getReplaces())
                && getSystem() == value.getSystem();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getDisplay(), getVersion(), getReplaces(), getSystem());
    }
}
