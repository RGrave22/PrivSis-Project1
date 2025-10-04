package pt.unl.fct.pds.proj1server.model;


public class LinkageResult {
    private Long medDataId;
    private int age;
    private String gender;
    private String diagnosis;
    
    private Long workDataId;
    private String firstName;
    private String lastName;
    private String postalCode;
    private String education;
    private String workplace;
    private String department;
    
    private String matchedOn;
    private boolean successfulReidentification;

    public LinkageResult() {}

    public LinkageResult(Long medDataId, int age, String gender, String diagnosis,
                        Long workDataId, String firstName, String lastName, 
                        String postalCode, String education, String workplace, 
                        String department, String matchedOn) {
        this.medDataId = medDataId;
        this.age = age;
        this.gender = gender;
        this.diagnosis = diagnosis;
        this.workDataId = workDataId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalCode = postalCode;
        this.education = education;
        this.workplace = workplace;
        this.department = department;
        this.matchedOn = matchedOn;
        this.successfulReidentification = true;
    }

    public Long getMedDataId() { return medDataId; }
    public void setMedDataId(Long medDataId) { this.medDataId = medDataId; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public Long getWorkDataId() { return workDataId; }
    public void setWorkDataId(Long workDataId) { this.workDataId = workDataId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getWorkplace() { return workplace; }
    public void setWorkplace(String workplace) { this.workplace = workplace; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getMatchedOn() { return matchedOn; }
    public void setMatchedOn(String matchedOn) { this.matchedOn = matchedOn; }

    public boolean isSuccessfulReidentification() { return successfulReidentification; }
    public void setSuccessfulReidentification(boolean successfulReidentification) { 
        this.successfulReidentification = successfulReidentification; 
    }
}
