package pt.unl.fct.pds.proj1server.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PrivacyBudgetManager {

    private double budget;

    public PrivacyBudgetManager(@Value("${privacy.budget}") double initialBudget) {
        this.budget = initialBudget;
    }

    public synchronized boolean hasBudget(double val) {
        return budget >= val;
    }

    public synchronized void consumeBudget(double val) {
        budget -= val;
    }

    public synchronized double getRemainingBudget() {
        return budget;
    }

    public synchronized void resetBudget(double val) {
        budget = val;
    }

}
