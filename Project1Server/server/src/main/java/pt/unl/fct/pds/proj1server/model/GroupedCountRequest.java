package pt.unl.fct.pds.proj1server.model;

public class GroupedCountRequest {

    private String attribute;
    private String aValue;
    private String condition;
    private String cValue;

    public GroupedCountRequest() {
    }

    public GroupedCountRequest(
            String attribute,
            String aValue, String condition, String cValue) {
        this.attribute = attribute;
        this.aValue = aValue;
        this.condition = condition;
        this.cValue = cValue;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getaValue() {
        return aValue;
    }

    public void setaValue(String aValue) {
        this.aValue = aValue;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getcValue() {
        return cValue;
    }

    public void setcValue(String cValue) {
        this.cValue = cValue;
    }






}
