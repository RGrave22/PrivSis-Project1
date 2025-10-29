package pt.unl.fct.pds.proj1server.controller;

import org.springframework.beans.factory.annotation.Value;
import pt.unl.fct.pds.proj1server.components.PrivacyBudgetManager;
import pt.unl.fct.pds.proj1server.model.*;
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
            case "age":
                try {
                    int ageValue = Integer.parseInt(value);
                    count = medDataRepository.countByAge(ageValue);
                } catch (NumberFormatException e) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Age must be a valid number");
                }
                break;
            case "gender":
                count = medDataRepository.countByGender(value);
                break;
            case "diagnosis":
                count = medDataRepository.countByDiagnosis(value);
                break;
            case "postal_code":
                count = medDataRepository.countByPostalCode(value);
                break;
            case "address":
                count = medDataRepository.countByAddress(value);
                break;
            case "name":
                count = medDataRepository.countByName(value);
                break;
            case "email":
                count = medDataRepository.countByEmail(value);
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid attribute. Supported attributes: age, gender, diagnosis, postal_code, address, name, email");
        }

        double noiseCount = Utils.addNoise(count, s, epsilon);

        budgetManager.consumeBudget(epsilon);
        double remainingBudget = budgetManager.getRemainingBudget();

        return new CountResponse(attribute, (int) noiseCount, s, remainingBudget);
    }

    @GetMapping("/budget")
    public double getRemainingBudget() {
        
        return budgetManager.getRemainingBudget();
    }

    @PostMapping("/reset")
    public void resetBudget() {
        budgetManager.resetBudget(5.0);
    }

    //TODO ADICIONAR O AVERAGE DA AGE COM O RESTO DOS ATRIBUTOS TODOS :)
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
        double sensitivity;

        
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

            sensitivity = 125.0;

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
            sensitivity = 125.0;
        } else {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid attribute format. Use 'age', 'age:gender:<genderValue>', or 'age:diagnosis:<diagnosisValue>>'");
        }

        // Formula q tou a usar (sum + noise) / (count + noise) qualquer cena mudar
        
        double noisedSum = Utils.addNoise(sum, sensitivity, epsilonPerQuery);
        double noisedCount = Utils.addNoise(count, 1.0  , epsilonPerQuery);

        
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


    @PostMapping("/average-raw")
    public AverageResponse getAverageAgeRaw(@RequestBody AverageRequest averageRequest) {
        String attribute = averageRequest.getAttribute();
        if (attribute == null || attribute.isEmpty()) {
            attribute = "age";
        }

        long sum = 0;
        long count = 0;
        double sensitivity = 0;
        double remainingBudget = 0;

        String[] parts = attribute.split(":");
        String field = parts[0];

        if (!field.equalsIgnoreCase("age")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only 'age' attribute is supported for average queries"
            );
        }

        if (parts.length == 1) {
            Long s = medDataRepository.sumAge();
            sum = (s != null) ? s : 0L;
            count = medDataRepository.count();
            sensitivity = 125.0;
        } else if (parts.length == 3) {
            String filterType = parts[1];
            String filterValue = parts[2];

            if (filterType.equalsIgnoreCase("gender")) {
                Long s = medDataRepository.sumAgeByGender(filterValue);
                sum = (s != null) ? s : 0L;
                count = medDataRepository.countByGender(filterValue);
            } else if (filterType.equalsIgnoreCase("diagnosis")) {
                Long s = medDataRepository.sumAgeByDiagnosis(filterValue);
                sum = (s != null) ? s : 0L;
                count = medDataRepository.countByDiagnosis(filterValue);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid filter type. Supported: gender, diagnosis"
                );
            }


        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid attribute format. Use 'age', 'age:gender:Male', or 'age:diagnosis:Diabetes'"
            );
        }

        double average = (count > 0) ? ((double) sum) / ((double) count) : 0.0;



        return new AverageResponse(
                attribute,
                average,
                sensitivity,
                remainingBudget
        );
    }


    @PostMapping("/grouped-count")
    public GroupedCountResponse getMedDataCount(@RequestBody GroupedCountRequest countRequest) {
        String firstAtt = countRequest.getAttribute1();
        String secondAtt = countRequest.getAttribute2();
        String att1Value = countRequest.getValue1();
        String att2Value = countRequest.getValue2();

        if (!budgetManager.hasBudget(epsilon)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Privacy budget exhausted");
        }

        double s = 1.0;
        long count = 0;

        if (firstAtt == null || secondAtt == null || att1Value == null || att2Value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attributes and values are required");
        }

        if(firstAtt.equalsIgnoreCase(secondAtt)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You need to insert two different values"
            );
        }

        String[] values = Utils.reorder(firstAtt, secondAtt, att1Value, att2Value);
        String L  = values[0];
        String R  = values[1];
        String LV = values[2];
        String RV = values[3];

        switch (L) {
            case "age":
                int age = Integer.parseInt(LV);
                switch (R) {
                    case "gender": count = medDataRepository.countByAgeAndGender(age, RV); break;
                    case "diagnosis": count = medDataRepository.countByAgeAndDiagnosis(age, RV); break;
                    case "postal_code": count = medDataRepository.countByAgeAndPostalCode(age, RV); break;
                    case "address": count = medDataRepository.countByAgeAndAddress(age, RV); break;
                    case "name": count = medDataRepository.countByAgeAndName(age, RV); break;
                    case "email": count = medDataRepository.countByAgeAndEmail(age, RV); break;
                }
                break;
            case "gender":
                switch (R) {
                    case "diagnosis": count = medDataRepository.countByGenderAndDiagnosis(LV, RV); break;
                    case "postal_code": count = medDataRepository.countByGenderAndPostalCode(LV, RV); break;
                    case "address": count = medDataRepository.countByGenderAndAddress(LV, RV); break;
                    case "name": count = medDataRepository.countByGenderAndName(LV, RV); break;
                    case "email": count = medDataRepository.countByGenderAndEmail(LV, RV); break;
                }
                break;
            case "diagnosis":
                switch (R) {
                    case "postal_code": count = medDataRepository.countByDiagnosisAndPostalCode(LV, RV); break;
                    case "address": count = medDataRepository.countByDiagnosisAndAddress(LV, RV); break;
                    case "name": count = medDataRepository.countByDiagnosisAndName(LV, RV); break;
                    case "email": count = medDataRepository.countByDiagnosisAndEmail(LV, RV); break;
                }
                break;
            case "postal_code":
                switch (R) {
                    case "address": count = medDataRepository.countByPostalCodeAndAddress(LV, RV); break;
                    case "name": count = medDataRepository.countByPostalCodeAndName(LV, RV); break;
                    case "email": count = medDataRepository.countByPostalCodeAndEmail(LV, RV); break;
                }
                break;
            case "address":
                switch (R) {
                    case "name": count = medDataRepository.countByAddressAndName(LV, RV); break;
                    case "email": count = medDataRepository.countByAddressAndEmail(LV, RV); break;
                }
                break;
            case "name":
                if (R.equals("email")) {
                    count = medDataRepository.countByNameAndEmail(LV, RV);
                }
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid attribute. Supported attributes: age, gender, diagnosis, postal_code, address, name, email");
        }

        double noiseCount = Utils.addNoise(count, s, epsilon);

        budgetManager.consumeBudget(epsilon);
        double remainingBudget = budgetManager.getRemainingBudget();

        String attributesCounter = L + ":" + LV + " AND " + R + ":" + RV;

        return new GroupedCountResponse(attributesCounter, (int) noiseCount, s, remainingBudget);
    }



    @PostMapping("/grouped-count-raw")
    public GroupedCountResponse getMedDataCountRaw(@RequestBody GroupedCountRequest countRequest) {
        String firstAtt = countRequest.getAttribute1();
        String secondAtt = countRequest.getAttribute2();
        String att1Value = countRequest.getValue1();
        String att2Value = countRequest.getValue2();

        long count = 0;

        if (firstAtt == null || secondAtt == null || att1Value == null || att2Value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Attributes and values are required");
        }

        if(firstAtt.equalsIgnoreCase(secondAtt)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You need to insert two different values"
            );
        }

        String[] values = Utils.reorder(firstAtt, secondAtt, att1Value, att2Value);
        String L  = values[0];
        String R  = values[1];
        String LV = values[2];
        String RV = values[3];



        switch (L) {
            case "age":
                int age = Integer.parseInt(LV);
                switch (R) {
                    case "diagnosis" : count = medDataRepository.countByAgeAndDiagnosis(age, RV); break;
                    case "gender" : count = medDataRepository.countByAgeAndGender(age, RV); break;
                    case "postal_code" : count = medDataRepository.countByAgeAndPostalCode(age, RV); break;

                }
                break;
            case "gender":
                switch (R) {
                    case "diagnosis": count = medDataRepository.countByGenderAndDiagnosis(LV, RV); break;
                    case "postal_code": count = medDataRepository.countByGenderAndPostalCode(LV, RV); break;
                }
                break;
            case "diagnosis" :
                if(R.equals("postal_code")) {
                    count = medDataRepository.countByDiagnosisAndPostalCode(LV, RV);
                }
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid attribute. Supported attributes: gender, diagnosis, age, postalCode");
        }


        String attributesCounter = L + ":" + LV + " AND " + R + ":" + RV;

        return new GroupedCountResponse(attributesCounter, count, 0, 0);
    }




}







