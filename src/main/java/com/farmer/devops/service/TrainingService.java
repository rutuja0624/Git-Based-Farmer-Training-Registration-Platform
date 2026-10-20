package com.farmer.devops.service;

import com.farmer.devops.exception.ResourceNotFoundException;
import com.farmer.devops.model.TrainingProgram;
import com.farmer.devops.repository.RegistrationRepository;
import com.farmer.devops.repository.TrainingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@SuppressWarnings("null")
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final RegistrationRepository registrationRepository;

    public TrainingService(TrainingRepository trainingRepository, RegistrationRepository registrationRepository) {
        this.trainingRepository = trainingRepository;
        this.registrationRepository = registrationRepository;
    }

    public List<TrainingProgram> getAllTrainings() {
        return trainingRepository.findAll();
    }

    public TrainingProgram getTrainingById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training program not found with ID: " + id));
    }

    @Transactional
    public TrainingProgram saveTraining(TrainingProgram trainingProgram) {
        return trainingRepository.save(trainingProgram);
    }

    public Map<String, Object> getTrainingStats(Long trainingId) {
        TrainingProgram training = getTrainingById(trainingId);
        long registeredCount = registrationRepository.countByTrainingProgramId(trainingId);
        int remainingCapacity = Math.max(0, training.getMaxCapacity() - (int) registeredCount);

        Map<String, Object> stats = new HashMap<>();
        stats.put("training", training);
        stats.put("registeredCount", registeredCount);
        stats.put("maxCapacity", training.getMaxCapacity());
        stats.put("remainingCapacity", remainingCapacity);
        return stats;
    }
}
