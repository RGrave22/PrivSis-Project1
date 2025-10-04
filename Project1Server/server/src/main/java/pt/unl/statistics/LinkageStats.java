package pt.unl.statistics;


public class LinkageStats {
    private long totalMedicalRecords;
    private long totalWorkRecords;
    private long successfulMatches;
    private double successRate;
    private long uniqueMatches;
    private double uniquenessRate;
    private String quasiIdentifiersUsed;
    private String conclusion;

    public LinkageStats(long totalMedicalRecords, long totalWorkRecords, 
                       long successfulMatches, double successRate,
                       long uniqueMatches, double uniquenessRate,
                       String quasiIdentifiersUsed, String conclusion) {
        this.totalMedicalRecords = totalMedicalRecords;
        this.totalWorkRecords = totalWorkRecords;
        this.successfulMatches = successfulMatches;
        this.successRate = successRate;
        this.uniqueMatches = uniqueMatches;
        this.uniquenessRate = uniquenessRate;
        this.quasiIdentifiersUsed = quasiIdentifiersUsed;
        this.conclusion = conclusion;
    }

    // Getters
    public long getTotalMedicalRecords() { return totalMedicalRecords; }
    public long getTotalWorkRecords() { return totalWorkRecords; }
    public long getSuccessfulMatches() { return successfulMatches; }
    public double getSuccessRate() { return successRate; }
    public long getUniqueMatches() { return uniqueMatches; }
    public double getUniquenessRate() { return uniquenessRate; }
    public String getQuasiIdentifiersUsed() { return quasiIdentifiersUsed; }
    public String getConclusion() { return conclusion; }
}