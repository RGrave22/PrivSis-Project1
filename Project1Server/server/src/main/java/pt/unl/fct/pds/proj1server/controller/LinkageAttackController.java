package pt.unl.fct.pds.proj1server.controller;

import pt.unl.fct.pds.proj1server.model.MedData;
import pt.unl.fct.pds.proj1server.model.WorkData;
import pt.unl.fct.pds.proj1server.model.LinkageResult;
import pt.unl.fct.pds.proj1server.repository.MedDataRepository;
import pt.unl.fct.pds.proj1server.repository.WorkDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.unl.statistics.LinkageStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/api/linkage")
public class LinkageAttackController {

    @Autowired
    private MedDataRepository medDataRepository;

    @Autowired
    private WorkDataRepository workDataRepository;


    @GetMapping("/attack")
    public List<LinkageResult> performLinkageAttack() {
        List<LinkageResult> results = new ArrayList<>();
        
        List<MedData> medDataList = medDataRepository.findAll();
        List<WorkData> workDataList = workDataRepository.findAll();

        for (MedData med : medDataList) {
            for (WorkData work : workDataList) {
                // Match on postal_code AND gender (common quasi-identifiers)
                if (med.getPostalCode().equals(work.getPostalCode()) && 
                    med.getGender().equalsIgnoreCase(work.getGender())) {
                    
                    LinkageResult result = new LinkageResult(
                        med.getId(),
                        med.getAge(),
                        med.getGender(),
                        med.getDiagnosis(),
                        work.getId(),
                        work.getFName(),
                        work.getLName(),
                        work.getPostalCode(),
                        work.getEducation(),
                        work.getWorkplace(),
                        work.getDepartment(),
                        "postal_code + gender"
                    );
                    
                    results.add(result);
                }
            }
        }

        return results;
    }


   //I dont know if you to keep this endpoint to check the stats of the attack could be useful 
   //for the report idk   
    @GetMapping("/stats")
    public LinkageStats getLinkageStats() {
        List<LinkageResult> results = performLinkageAttack();
        long totalMedRecords = medDataRepository.count();
        long totalWorkRecords = workDataRepository.count();
        long successfulMatches = results.size();
        
        double successRate = totalMedRecords > 0 ? 
            (double) successfulMatches / totalMedRecords * 100 : 0;

        Map<String, Integer> quasiIdentifierCounts = new HashMap<>();
        for (LinkageResult result : results) {
            String key = result.getPostalCode() + "|" + result.getGender();
            quasiIdentifierCounts.put(key, quasiIdentifierCounts.getOrDefault(key, 0) + 1);
        }
        
        long uniqueMatches = quasiIdentifierCounts.values().stream().filter(count -> count == 1).count();
        double uniquenessRate = successfulMatches > 0 ? 
            (double) uniqueMatches / successfulMatches * 100 : 0;

        return new LinkageStats(
            totalMedRecords,
            totalWorkRecords,
            successfulMatches,
            successRate,
            uniqueMatches,
            uniquenessRate,
            "postal_code + gender",
            "De-identification FAILED - records can be re-identified!"
        );
    }

}
