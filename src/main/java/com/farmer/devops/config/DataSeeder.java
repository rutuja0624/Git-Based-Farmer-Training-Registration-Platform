package com.farmer.devops.config;

import com.farmer.devops.model.Registration;
import com.farmer.devops.model.RegistrationStatus;
import com.farmer.devops.model.TrainingProgram;
import com.farmer.devops.repository.RegistrationRepository;
import com.farmer.devops.repository.TrainingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TrainingRepository trainingRepo;
    private final RegistrationRepository registrationRepo;

    public DataSeeder(TrainingRepository trainingRepo, RegistrationRepository registrationRepo) {
        this.trainingRepo = trainingRepo;
        this.registrationRepo = registrationRepo;
    }

    @Override
    public void run(String... args) {
        if (trainingRepo.count() == 0) {
            TrainingProgram t1 = trainingRepo.save(new TrainingProgram(
                    null,
                    "Organic Farming & Soil Health Management",
                    "Sustainable Agriculture",
                    "Dr. Ramesh Deshmukh (Soil Scientist)",
                    "KVK Agricultural Training Hall, Pune",
                    "2026-11-15 (09:30 AM - 04:30 PM)",
                    35,
                    "Hands-on workshop on natural composting, bio-fertilizers, green manuring, and zero-chemical soil rejuvenation techniques."
            ));

            TrainingProgram t2 = trainingRepo.save(new TrainingProgram(
                    null,
                    "Micro-Irrigation & Drip Automation Techniques",
                    "Water Management",
                    "Er. Priya Kulkarni (Irrigation Specialist)",
                    "District Agri-Tech Demonstration Farm, Nashik",
                    "2026-11-20 (10:00 AM - 03:00 PM)",
                    40,
                    "Practical training on sensor-based drip lines, fertigation systems, water pump maintenance, and government subsidy schemes."
            ));

            TrainingProgram t3 = trainingRepo.save(new TrainingProgram(
                    null,
                    "Integrated Pest & Crop Disease Management",
                    "Plant Protection",
                    "Dr. Anil Verma (Entomologist)",
                    "Regional Extension Center, Baramati",
                    "2026-11-28 (10:00 AM - 04:00 PM)",
                    30,
                    "Early detection of fungal and insect infestations, pheromone trap deployment, biological pest control, and safe spraying practices."
            ));

            TrainingProgram t4 = trainingRepo.save(new TrainingProgram(
                    null,
                    "Agricultural Drone Spraying & Precision Farming",
                    "Smart AgTech",
                    "Capt. Vikram Shinde (Drone Operations Lead)",
                    "Central Demonstration Ground, Nagpur",
                    "2026-12-05 (09:00 AM - 05:00 PM)",
                    25,
                    "DGCA guidelines, pesticide payload calculations, autonomous flight mapping, battery care, and multi-spectral field analysis."
            ));

            // Seed initial test registrations for immediate testing of Tracker and Admin modules
            Registration r1 = new Registration(
                    "REG-100201",
                    "Suresh Patil",
                    "9876543210",
                    "suresh.patil@example.com",
                    "Haveli",
                    "Pune",
                    4.5,
                    t1
            );
            r1.setStatus(RegistrationStatus.APPROVED);
            r1.setRemarks("Application verified; allocated seat #12");
            registrationRepo.save(r1);

            Registration r2 = new Registration(
                    "REG-100202",
                    "Sunita Shinde",
                    "9823012345",
                    "sunita.shinde@example.com",
                    "Dindori",
                    "Nashik",
                    2.0,
                    t2
            );
            r2.setStatus(RegistrationStatus.PENDING);
            r2.setRemarks("Awaiting batch quota confirmation");
            registrationRepo.save(r2);

            Registration r3 = new Registration(
                    "REG-100203",
                    "Manoj Jadhav",
                    "9811223344",
                    "manoj.jadhav@example.com",
                    "Indapur",
                    "Pune",
                    7.2,
                    t3
            );
            r3.setStatus(RegistrationStatus.COMPLETED);
            r3.setRemarks("Workshop completed; certificate issued");
            registrationRepo.save(r3);
        }
    }
}
