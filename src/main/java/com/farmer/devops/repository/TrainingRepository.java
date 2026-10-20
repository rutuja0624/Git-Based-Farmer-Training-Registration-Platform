package com.farmer.devops.repository;

import com.farmer.devops.model.TrainingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<TrainingProgram, Long> {
    List<TrainingProgram> findByCategoryIgnoreCase(String category);
}
