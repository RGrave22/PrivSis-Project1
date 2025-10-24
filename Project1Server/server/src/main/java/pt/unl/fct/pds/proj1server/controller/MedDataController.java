package pt.unl.fct.pds.proj1server.controller;

import pt.unl.fct.pds.proj1server.model.MedData;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import pt.unl.fct.pds.proj1server.model.CountResponse;
import pt.unl.fct.pds.proj1server.repository.MedDataRepository;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/meddata")
public class MedDataController {

    @Autowired
    private MedDataRepository medDataRepository;

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
        String attribute = countRequest.getAttribute();
        String value = countRequest.getValue();
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

        return new CountResponse(attribute, (int) count, 0,0);
    }
}
