package com.farmer.devops.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "training_programs")
public class TrainingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, length = 100)
    private String instructor;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(name = "training_date", nullable = false, length = 60)
    private String trainingDate;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity = 40;

    @Column(length = 500)
    private String description;

    public TrainingProgram() {
    }

    public TrainingProgram(Long id, String title, String category, String instructor, String location, String trainingDate, int maxCapacity, String description) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.instructor = instructor;
        this.location = location;
        this.trainingDate = trainingDate;
        this.maxCapacity = maxCapacity;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingProgram that = (TrainingProgram) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
