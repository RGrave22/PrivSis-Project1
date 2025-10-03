package pt.unl.fct.pds.proj1server.repository;

import pt.unl.fct.pds.proj1server.model.MedDataDeidentified;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedDataDeidentifiedRepository extends JpaRepository<MedDataDeidentified, Long> {
    // Query methods for de-identified data
    long countByGender(String gender);
    long countByDiagnosis(String diagnosis);
    long countByAge(int age);
}
