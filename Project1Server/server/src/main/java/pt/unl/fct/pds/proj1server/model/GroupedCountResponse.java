package pt.unl.fct.pds.proj1server.model;

public class GroupedCountResponse {

    private String attribute;
    private String condition;
    private double noisedValue;
    private double sensitivity;
    private double remainingBudget;

    public GroupedCountResponse() {
    }

    public GroupedCountResponse(String attribute, String condition,
                                double noisedValue, double sensitivity, double remainingBudget) {
        this.attribute = attribute;
        this.condition = condition;
        this.noisedValue = noisedValue;
        this.sensitivity = sensitivity;
        this.remainingBudget = remainingBudget;
    }


    public String getAttribute() { return attribute; }
    public String getCondition() { return condition; }
    public double getNoisedValue() { return noisedValue; }
    public double getSensitivity() { return sensitivity; }
    public double getRemainingBudget() { return remainingBudget; }


    public void setAttribute(String attribute) { this.attribute = attribute; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setNoisedValue(double noisedValue) { this.noisedValue = noisedValue; }
    public void setSensitivity(double sensitivity) { this.sensitivity = sensitivity; }
    public void setRemainingBudget(double remainingBudget) { this.remainingBudget = remainingBudget; }
}
