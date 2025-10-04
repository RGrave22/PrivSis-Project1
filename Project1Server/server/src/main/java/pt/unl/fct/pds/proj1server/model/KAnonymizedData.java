package pt.unl.fct.pds.proj1server.model;

public class KAnonymizedData {
    private String ageRange;           // qi
    private String gender;             // qi
    private String postalCodePrefix;   // qi
    private String diagnosis;          // s
    private int equivalenceClassSize;  // k value - number of records in this group

    public KAnonymizedData() {}

    public KAnonymizedData(String ageRange, String gender, String postalCodePrefix, 
                           String diagnosis, int equivalenceClassSize) {
        this.ageRange = ageRange;
        this.gender = gender;
        this.postalCodePrefix = postalCodePrefix;
        this.diagnosis = diagnosis;
        this.equivalenceClassSize = equivalenceClassSize;
    }

    // Getters and Setters
    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPostalCodePrefix() { return postalCodePrefix; }
    public void setPostalCodePrefix(String postalCodePrefix) { 
        this.postalCodePrefix = postalCodePrefix; 
    }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public int getEquivalenceClassSize() { return equivalenceClassSize; }
    public void setEquivalenceClassSize(int equivalenceClassSize) { 
        this.equivalenceClassSize = equivalenceClassSize; 
    }
}
