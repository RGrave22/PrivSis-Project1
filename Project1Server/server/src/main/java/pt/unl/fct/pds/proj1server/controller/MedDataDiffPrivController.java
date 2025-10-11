package pt.unl.fct.pds.proj1server.controller;

import org.springframework.beans.factory.annotation.Value;
import pt.unl.fct.pds.proj1server.components.PrivacyBudgetManager;
import pt.unl.fct.pds.proj1server.model.MedData;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import pt.unl.fct.pds.proj1server.model.CountResponse;
import pt.unl.fct.pds.proj1server.model.AverageRequest;
import pt.unl.fct.pds.proj1server.model.AverageResponse;
import pt.unl.fct.pds.proj1server.repository.MedDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.unl.fct.pds.proj1server.utils.Utils;

import java.util.List;

@RestController
@RequestMapping("/api/dp")
public class MedDataDiffPrivController {

    @Autowired
    private MedDataRepository medDataRepository;

    @Autowired
    private PrivacyBudgetManager budgetManager;

    @Value("${privacy.epsilon}")
    private double epsilon;

    @GetMapping
    public List<MedData> getAllMedDatas() {
        return medDataRepository.findAll();
    }

    @GetMapping("/{id}")
    public MedData getMedDataById(@PathVariable Long id) {
        return medDataRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "MedData not found with id: " + id));
    }

    @PostMapping("/count")
    public CountResponse getMedDataCount(@RequestBody CountRequest countRequest) {
        if (!budgetManager.hasBudget(epsilon)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Privacy budget exhausted");
        }

        String attribute = countRequest.getAttribute();
        String value = countRequest.getValue();
        double s = 1.0;
        long count = 0;

        switch (attribute.toLowerCase()) {
            case "gender":
                count = medDataRepository.countByGender(value);
                break;
            case "diagnosis":
                count = medDataRepository.countByDiagnosis(value);
                break;
            case "age":
                try {
                    int ageValue = Integer.parseInt(value);
                    count = medDataRepository.countByAge(ageValue);
                } catch (NumberFormatException e) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Age must be a valid number");
                }
                break;
            case "postal_code":
                count = medDataRepository.countByPostalCode(value);
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid attribute. Supported attributes: gender, diagnosis, age, postalCode");
        }

        double noiseCount = Utils.addNoise(count, s, epsilon);

        budgetManager.consumeBudget(epsilon);

        return new CountResponse(attribute, (int) noiseCount);
    }
    
    @GetMapping("/budget")
    public double getRemainingBudget() {
        
        return budgetManager.getRemainingBudget();
    }

    @PostMapping("/reset")
    public void resetBudget() {
        budgetManager.resetBudget(10.0);

    }

    @PostMapping("/average")
    public AverageResponse getAverageAge(@RequestBody AverageRequest averageRequest) {
        
        double epsilonPerQuery = epsilon / 2.0;
        
        if (!budgetManager.hasBudget(epsilon)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Privacy budget exhausted");
        }

        String attribute = averageRequest.getAttribute();
        if (attribute == null || attribute.isEmpty()) {
            attribute = "age";
        }
        long sum = 0;
        long count = 0;
        double sensitivity; //maybe criar var de classe se formos usar mais vezes

        
        String[] parts = attribute.split(":");
        String field = parts[0];
        
        if (!field.equalsIgnoreCase("age")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only 'age' attribute is supported for average queries");
        }

        if (parts.length == 1) {
            
            sum = medDataRepository.sumAge() != null ? medDataRepository.sumAge() : 0;
            count = medDataRepository.count();
            sensitivity = 100.0; //talvez mudar esse valor pq o gajo fala nos slides dos valores se forem reais pode dar merda 

        } else if (parts.length == 3) {
            String filterType = parts[1];
            String filterValue = parts[2];
            
            if (filterType.equalsIgnoreCase("gender")) {
                sum = medDataRepository.sumAgeByGender(filterValue) != null ? 
                      medDataRepository.sumAgeByGender(filterValue) : 0;
                count = medDataRepository.countByGender(filterValue);
            } else if (filterType.equalsIgnoreCase("diagnosis")) {
                sum = medDataRepository.sumAgeByDiagnosis(filterValue) != null ? 
                      medDataRepository.sumAgeByDiagnosis(filterValue) : 0;
                count = medDataRepository.countByDiagnosis(filterValue);
            } else {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid filter type. Supported: gender, diagnosis");
            }
            sensitivity = 100.0; //talvez mudar este valor tou a assumir q o valor max é 100 mais q 100 ja e estica
        } else {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid attribute format. Use 'age', 'age:gender:Male', or 'age:diagnosis:Diabetes'");
        }

        // Formula q tou a usar (sum + noise) / (count + noise) qualquer cena mudar
        
        double noisedSum = Utils.addNoise(sum, sensitivity, epsilonPerQuery);
        double noisedCount = Utils.addNoise(count, 1.0, epsilonPerQuery);

        
        double noisedAverage = noisedCount > 0 ? noisedSum / noisedCount : 0;

        
        budgetManager.consumeBudget(epsilon);
        double remainingBudget = budgetManager.getRemainingBudget();

        return new AverageResponse(
            attribute,
            noisedAverage,
            sensitivity,
            remainingBudget
        );
    }
}

