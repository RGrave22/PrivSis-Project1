package pt.unl.fct.pds.proj1server.model;

public class AverageRequest {
    private String attribute;

    public AverageRequest() {
    }

    public AverageRequest(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
