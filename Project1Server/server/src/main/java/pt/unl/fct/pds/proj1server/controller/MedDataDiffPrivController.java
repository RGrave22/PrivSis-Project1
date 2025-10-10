package pt.unl.fct.pds.proj1server.controller;

import org.springframework.beans.factory.annotation.Value;
import pt.unl.fct.pds.proj1server.components.PrivacyBudgetManager;
import pt.unl.fct.pds.proj1server.model.MedData;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import pt.unl.fct.pds.proj1server.model.CountResponse;
import pt.unl.fct.pds.proj1server.repository.MedDataRepository;
import pt.unl.fct.pds.proj1server.model.CountRequest;
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
}

