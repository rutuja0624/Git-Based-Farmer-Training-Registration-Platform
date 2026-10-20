package com.farmer.devops.repository;

import com.farmer.devops.model.Registration;
import com.farmer.devops.model.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findByRegistrationCode(String registrationCode);

    List<Registration> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumberAndTrainingProgramId(String mobileNumber, Long trainingId);

    List<Registration> findByStatus(RegistrationStatus status);

    List<Registration> findByTrainingProgramId(Long trainingId);

    List<Registration> findByStatusAndTrainingProgramId(RegistrationStatus status, Long trainingId);

    long countByStatus(RegistrationStatus status);

    long countByTrainingProgramId(Long trainingId);
}
