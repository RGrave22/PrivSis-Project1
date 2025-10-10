package pt.unl.fct.pds.proj1server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.unl.fct.pds.proj1server.model.KAnonymizedData;
import pt.unl.fct.pds.proj1server.model.MedData;
import pt.unl.fct.pds.proj1server.repository.MedDataRepository;
import pt.unl.statistics.KAnonymityStats;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kanonymity")
public class KAnonymityController {

    @Autowired
    private MedDataRepository medDataRepository;

    @GetMapping
    public List<KAnonymizedData> getKAnonymizedData(@RequestParam(defaultValue = "2") int k) {
        if (k < 2) {
            throw new IllegalArgumentException("k must be at least 2");
        }

        List<MedData> originalData = medDataRepository.findAll();
        return applyMondrianKAnonymity(originalData, k);
    }

    @GetMapping("/stats")
    public KAnonymityStats getStatistics(@RequestParam(defaultValue = "2") int k) {
        List<MedData> originalData = medDataRepository.findAll();
        List<KAnonymizedData> anonymized = applyMondrianKAnonymity(originalData, k);

        long totalRecords = originalData.size();
        long anonymizedRecords = anonymized.stream()
            .mapToLong(KAnonymizedData::getEquivalenceClassSize)
            .sum();
        long suppressedRecords = totalRecords - anonymizedRecords;
        double retentionRate = totalRecords > 0 ? 
            (double) anonymizedRecords / totalRecords * 100 : 0;
        
        long equivalenceClasses = anonymized.size();
        double avgClassSize = equivalenceClasses > 0 ?
            (double) anonymizedRecords / equivalenceClasses : 0;

        return new KAnonymityStats(
            k,
            totalRecords,
            anonymizedRecords,
            suppressedRecords,
            retentionRate,
            equivalenceClasses,
            avgClassSize,
            "Mondrian (LeFevre et al., 2006)",
            "Age: Range generalization; PostalCode: Prefix generalization; Gender: Suppression if needed"
        );
    }

    @GetMapping("/test-attack")
    public LinkageAttackResult testLinkageAttack(@RequestParam(defaultValue = "2") int k) {
        List<KAnonymizedData> anonymized = getKAnonymizedData(k);
        
        long totalEquivalenceClasses = anonymized.size();
        long uniquelyIdentifiable = anonymized.stream()
            .filter(data -> data.getEquivalenceClassSize() == 1)
            .count();
        
        double vulnerabilityRate = totalEquivalenceClasses > 0 ?
            (double) uniquelyIdentifiable / totalEquivalenceClasses * 100 : 0;
        
        return new LinkageAttackResult(
            k,
            totalEquivalenceClasses,
            uniquelyIdentifiable,
            vulnerabilityRate,
            vulnerabilityRate < 10 ? "GOOD - Most records protected" :
            vulnerabilityRate < 30 ? "MODERATE - Some vulnerability" :
            "POOR - Many records at risk"
        );
    }


    private List<KAnonymizedData> applyMondrianKAnonymity(List<MedData> data, int k) {
        List<KAnonymizedData> result = new ArrayList<>();
        List<List<MedData>> partitions = new ArrayList<>();
        partitions.add(new ArrayList<>(data));
        
        partitionData(partitions, k);
        
        for (List<MedData> partition : partitions) {
            if (partition.size() >= k) {
                result.addAll(generalizePartition(partition));
            }
        }
        
        return result;
    }


    private void partitionData(List<List<MedData>> partitions, int k) {
        boolean changed = true;
        
        while (changed) {
            changed = false;
            List<List<MedData>> newPartitions = new ArrayList<>();
            
            for (List<MedData> partition : partitions) {
                if (partition.size() < 2 * k) {
                    newPartitions.add(partition);
                    continue;
                }
                
                String dimension = findBestSplitDimension(partition);
                List<List<MedData>> split = splitPartition(partition, dimension);
                
                if (split.get(0).size() >= k && split.get(1).size() >= k) {
                    newPartitions.addAll(split);
                    changed = true;
                } else {
                    newPartitions.add(partition);
                }
            }
            
            partitions.clear();
            partitions.addAll(newPartitions);
        }
    }


    private String findBestSplitDimension(List<MedData> partition) {
        int ageRange = partition.stream()
            .mapToInt(MedData::getAge)
            .max().orElse(0) - partition.stream().mapToInt(MedData::getAge).min().orElse(0);
        
        long uniquePostalCodes = partition.stream()
            .map(MedData::getPostalCode)
            .distinct()
            .count();
        
        long uniqueGenders = partition.stream()
            .map(MedData::getGender)
            .distinct()
            .count();
        
        if (ageRange >= uniquePostalCodes && ageRange >= uniqueGenders) {
            return "age";
        } else if (uniquePostalCodes >= uniqueGenders) {
            return "postalCode";
        } else {
            return "gender";
        }
    }

    private List<List<MedData>> splitPartition(List<MedData> partition, String dimension) {
        List<MedData> sorted = new ArrayList<>(partition);
        
        switch (dimension) {
            case "age":
                sorted.sort(Comparator.comparingInt(MedData::getAge));
                break;
            case "postalCode":
                sorted.sort(Comparator.comparing(MedData::getPostalCode));
                break;
            case "gender":
                sorted.sort(Comparator.comparing(MedData::getGender));
                break;
        }
        
        int medianIndex = sorted.size() / 2;
        List<List<MedData>> result = new ArrayList<>();
        result.add(new ArrayList<>(sorted.subList(0, medianIndex)));
        result.add(new ArrayList<>(sorted.subList(medianIndex, sorted.size())));
        
        return result;
    }


    private List<KAnonymizedData> generalizePartition(List<MedData> partition) {
        String ageRange = generalizeAge(partition);
        String postalCodePrefix = generalizePostalCode(partition);
        String gender = generalizeGender(partition);
        int classSize = partition.size();
        
        return partition.stream()
            .collect(Collectors.groupingBy(MedData::getDiagnosis))
            .entrySet()
            .stream()
            .map(entry -> new KAnonymizedData(
                ageRange,
                gender,
                postalCodePrefix,
                entry.getKey(),
                classSize
            ))
            .collect(Collectors.toList());
    }


    private String generalizeAge(List<MedData> partition) {
        int minAge = partition.stream().mapToInt(MedData::getAge).min().orElse(0);
        int maxAge = partition.stream().mapToInt(MedData::getAge).max().orElse(0);
        
        if (minAge == maxAge) {
            return String.valueOf(minAge);
        }
        
        int rangeSize = 10;
        int lowerBound = (minAge / rangeSize) * rangeSize;
        int upperBound = ((maxAge / rangeSize) + 1) * rangeSize - 1;
        
        return lowerBound + "-" + upperBound;
    }


    private String generalizePostalCode(List<MedData> partition) {
        List<String> postalCodes = partition.stream()
            .map(MedData::getPostalCode)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        if (postalCodes.size() == 1) {
            return postalCodes.get(0);
        }
        
        String first = postalCodes.get(0);
        String last = postalCodes.get(postalCodes.size() - 1);
        
        int commonLength = 0;
        for (int i = 0; i < Math.min(first.length(), last.length()); i++) {
            if (first.charAt(i) == last.charAt(i)) {
                commonLength++;
            } else {
                break;
            }
        }
        
        if (commonLength == 0) {
            return "****";
        }
        
        String prefix = first.substring(0, commonLength);
        return prefix + "****".substring(0, Math.min(4, first.length() - commonLength));
    }

    private String generalizeGender(List<MedData> partition) {
        long uniqueGenders = partition.stream()
            .map(MedData::getGender)
            .distinct()
            .count();
        
        if (uniqueGenders == 1) {
            return partition.get(0).getGender();
        }
        
        return "*";
    }

    // Maybe create a new file for this class //Yes
    static class LinkageAttackResult {
        private int kValue;
        private long totalEquivalenceClasses;
        private long uniquelyIdentifiable;
        private double vulnerabilityRate;
        private String assessment;

        public LinkageAttackResult(int kValue, long totalEquivalenceClasses,
                                  long uniquelyIdentifiable, double vulnerabilityRate,
                                  String assessment) {
            this.kValue = kValue;
            this.totalEquivalenceClasses = totalEquivalenceClasses;
            this.uniquelyIdentifiable = uniquelyIdentifiable;
            this.vulnerabilityRate = vulnerabilityRate;
            this.assessment = assessment;
        }

        // Getters
        public int getKValue() { return kValue; }
        public long getTotalEquivalenceClasses() { return totalEquivalenceClasses; }
        public long getUniquelyIdentifiable() { return uniquelyIdentifiable; }
        public double getVulnerabilityRate() { return vulnerabilityRate; }
        public String getAssessment() { return assessment; }
    }
}
