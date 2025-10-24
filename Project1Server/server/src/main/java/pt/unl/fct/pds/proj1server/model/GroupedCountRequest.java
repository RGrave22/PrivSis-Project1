package pt.unl.fct.pds.proj1server.model;

public class GroupedCountRequest {

    private String attribute1;
    private String value1;
    private String attribute2;
    private String value2;

    public GroupedCountRequest() {
    }

    public GroupedCountRequest(String attribute1, String value1,
                               String attribute2, String value2) {
        this.attribute1 = attribute1;
        this.value1 = value1;
        this.attribute2 = attribute2;
        this.value2 = value2;
    }

    public String getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

}
