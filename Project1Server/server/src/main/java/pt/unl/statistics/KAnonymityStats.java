 package pt.unl.statistics;
 
 public class KAnonymityStats {
        private int kValue;
        private long totalRecords;
        private long anonymizedRecords;
        private long suppressedRecords;
        private double retentionRate;
        private long equivalenceClasses;
        private double avgClassSize;
        private String algorithm;
        private String transformations;

        public KAnonymityStats(int kValue, long totalRecords, long anonymizedRecords,
                              long suppressedRecords, double retentionRate,
                              long equivalenceClasses, double avgClassSize,
                              String algorithm, String transformations) {
            this.kValue = kValue;
            this.totalRecords = totalRecords;
            this.anonymizedRecords = anonymizedRecords;
            this.suppressedRecords = suppressedRecords;
            this.retentionRate = retentionRate;
            this.equivalenceClasses = equivalenceClasses;
            this.avgClassSize = avgClassSize;
            this.algorithm = algorithm;
            this.transformations = transformations;
        }

        // Getters
        public int getKValue() { return kValue; }
        public long getTotalRecords() { return totalRecords; }
        public long getAnonymizedRecords() { return anonymizedRecords; }
        public long getSuppressedRecords() { return suppressedRecords; }
        public double getRetentionRate() { return retentionRate; }
        public long getEquivalenceClasses() { return equivalenceClasses; }
        public double getAvgClassSize() { return avgClassSize; }
        public String getAlgorithm() { return algorithm; }
        public String getTransformations() { return transformations; }
}
