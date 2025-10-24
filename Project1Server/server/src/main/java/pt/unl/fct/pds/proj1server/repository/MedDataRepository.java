package pt.unl.fct.pds.proj1server.repository;

import pt.unl.fct.pds.proj1server.model.MedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MedDataRepository extends JpaRepository<MedData, Long> {
    long countByGender(String gender);
    long countByDiagnosis(String diagnosis);
    long countByAge(int age);
    long countByPostalCode(String postalCode);
    long countByAgeAndGender(int age, String gender);
    long countByAgeAndDiagnosis(int age, String diagnosis);
    long countByAgeAndPostalCode(int age, String postalCode);
    long countByGenderAndDiagnosis(String gender, String diagnosis);
    long countByGenderAndPostalCode(String gender, String postalCode);
    long countByDiagnosisAndPostalCode(String diagnosis, String postalCode);

    @Query("SELECT SUM(m.age) FROM MedData m")
    Long sumAge();
    
    @Query("SELECT SUM(m.age) FROM MedData m WHERE m.gender = ?1")
    Long sumAgeByGender(String gender);
    
    @Query("SELECT SUM(m.age) FROM MedData m WHERE m.diagnosis = ?1")
    Long sumAgeByDiagnosis(String diagnosis);
}
