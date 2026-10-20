package com.farmer.devops.controller;

import com.farmer.devops.dto.ApiResponse;
import com.farmer.devops.model.TrainingProgram;
import com.farmer.devops.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trainings")
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TrainingProgram>>> getAllTrainings() {
        List<TrainingProgram> trainings = trainingService.getAllTrainings();
        return ResponseEntity.ok(ApiResponse.ok("Trainings retrieved successfully", trainings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainingProgram>> getTrainingById(@PathVariable Long id) {
        TrainingProgram training = trainingService.getTrainingById(id);
        return ResponseEntity.ok(ApiResponse.ok(training));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrainingStats(@PathVariable Long id) {
        Map<String, Object> stats = trainingService.getTrainingStats(id);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
