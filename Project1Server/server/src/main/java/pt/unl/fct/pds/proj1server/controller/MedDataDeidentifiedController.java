package pt.unl.fct.pds.proj1server.controller;

import pt.unl.fct.pds.proj1server.model.MedDataDeidentified;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import pt.unl.fct.pds.proj1server.model.CountResponse;
import pt.unl.fct.pds.proj1server.repository.MedDataDeidentifiedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/api/deidentified")
public class MedDataDeidentifiedController {

    @Autowired
    private MedDataDeidentifiedRepository medDataDeidentifiedRepository;


    @GetMapping
    public List<MedDataDeidentified> getAllDeidentifiedData() {
        return medDataDeidentifiedRepository.findAll();
    }

    @GetMapping("/{id}")
    public MedDataDeidentified getDeidentifiedDataById(@PathVariable Long id) {
        return medDataDeidentifiedRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "De-identified data not found with id: " + id));
    }

    @PostMapping("/count")
    public CountResponse getDeidentifiedCount(@RequestBody CountRequest countRequest) {
        String attribute = countRequest.getAttribute();
        String value = countRequest.getValue();
        long count = 0;

        switch (attribute.toLowerCase()) {
            case "gender":
                count = medDataDeidentifiedRepository.countByGender(value);
                break;
            case "diagnosis":
                count = medDataDeidentifiedRepository.countByDiagnosis(value);
                break;
            case "age":
                try {
                    int ageValue = Integer.parseInt(value);
                    count = medDataDeidentifiedRepository.countByAge(ageValue);
                } catch (NumberFormatException e) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Age must be a valid number");
                }
                break;
            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, 
                        "Invalid attribute. Supported attributes: gender, diagnosis, age");
        }

        return new CountResponse(attribute, (int) count, 0,0);
    }

}
