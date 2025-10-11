package pt.unl.fct.pds.proj1server.model;

public class AverageResponse {
    private String attribute;
    private double noisedAverage;
    private double sensitivity;
    private double remainingBudget;

    public AverageResponse() {
    }

    public AverageResponse(String attribute, double noisedAverage, double sensitivity, double remainingBudget) {
        this.attribute = attribute;
        this.noisedAverage = noisedAverage;
        this.sensitivity = sensitivity;
        this.remainingBudget = remainingBudget;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public double getNoisedAverage() {
        return noisedAverage;
    }

    public void setNoisedAverage(double noisedAverage) {
        this.noisedAverage = noisedAverage;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }
}
