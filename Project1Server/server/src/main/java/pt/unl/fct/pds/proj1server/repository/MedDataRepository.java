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
    long countByAddress(String address);
    long countByName(String name);
    long countByEmail(String email);

    //Age
    long countByAgeAndGender(int age, String gender);
    long countByAgeAndDiagnosis(int age, String diagnosis);
    long countByAgeAndPostalCode(int age, String postalCode);
    long countByAgeAndAddress(int age, String address);
    long countByAgeAndName(int age, String name);
    long countByAgeAndEmail(int age, String email);

    //Gender
    long countByGenderAndDiagnosis(String gender, String diagnosis);
    long countByGenderAndPostalCode(String gender, String postalCode);
    long countByGenderAndAddress(String gender, String address);
    long countByGenderAndName(String gender, String name);
    long countByGenderAndEmail(String gender, String email);

    //Diagnosis
    long countByDiagnosisAndPostalCode(String diagnosis, String postalCode);
    long countByDiagnosisAndAddress(String diagnosis, String address);
    long countByDiagnosisAndName(String diagnosis, String name);
    long countByDiagnosisAndEmail(String diagnosis, String email);

    //PostalCode
    long countByPostalCodeAndAddress(String postalCode, String address);
    long countByPostalCodeAndName(String postalCode, String name);
    long countByPostalCodeAndEmail(String postalCode, String email);

    //Address
    long countByAddressAndName(String address, String name);
    long countByAddressAndEmail(String address, String email);

    //Name
    long countByNameAndEmail(String name, String email);

    @Query("SELECT SUM(m.age) FROM MedData m")
    Long sumAge();
    
    @Query("SELECT SUM(m.age) FROM MedData m WHERE m.gender = ?1")
    Long sumAgeByGender(String gender);
    
    @Query("SELECT SUM(m.age) FROM MedData m WHERE m.diagnosis = ?1")
    Long sumAgeByDiagnosis(String diagnosis);

    @Query("SELECT SUM(m.age) FROM MedData m WHERE m.postalCode = ?1")
    Long sumAgeByPostalCode(String postalCode);

    @Query("SELECT SUM(m.age) FROM MedData m WHERE m.address = ?1")
    Long sumAgeByAddress(String address);

    @Query("SELECT SUM(m.age) FROM MedData m WHERE m.name = ?1")
    Long sumAgeByName(String name);

    @Query("SELECT SUM(m.age) FROM MedData m WHERE m.email = ?1")
    Long sumAgeByEmail(String email);
}
