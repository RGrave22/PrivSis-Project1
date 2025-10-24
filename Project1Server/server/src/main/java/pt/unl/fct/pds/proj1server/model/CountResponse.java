package pt.unl.fct.pds.proj1server.model;

public class CountResponse {
    private String attribute;
    private double noisedValue;
    private double sensitivity;
    private double remainingBudget;

    public CountResponse() {
    }

    public CountResponse(String attribute, double noisedValue, double sensitivity, double remainingBudget) {
        this.attribute = attribute;
        this.noisedValue = noisedValue;
        this.sensitivity = sensitivity;
        this.remainingBudget = remainingBudget;
    }

    public String getAttribute() { return attribute; }
    public double getNoisedValue() { return noisedValue; }
    public double getSensitivity() { return sensitivity; }
    public double getRemainingBudget() { return remainingBudget; }

    public void setAttribute(String attribute) { this.attribute = attribute; }
    public void setNoisedValue(double noisedValue) { this.noisedValue = noisedValue; }
    public void setSensitivity(double sensitivity) { this.sensitivity = sensitivity; }
    public void setRemainingBudget(double remainingBudget) { this.remainingBudget = remainingBudget; }
}
