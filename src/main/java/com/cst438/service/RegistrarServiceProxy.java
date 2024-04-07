package com.cst438.service;

import com.cst438.dto.*;
import com.cst438.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class RegistrarServiceProxy {

    Queue registrarServiceQueue = new Queue("registrar_service", true);
    EnrollmentRepository enrollmentRepository;

    @Bean
    public Queue createQueue() {
        return new Queue("gradebook_service", true);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "gradebook_service")
    public void receiveFromRegistrar(String message)  {
        //TODO implement this message
        try {
            System.out.println("receive from Registrar " + message);
            String[] parts = message.split(" ", 2);
            if (parts[0].equals("updateEnrollment")) {
                EnrollmentDTO dto = fromJsonString(parts[1], EnrollmentDTO.class);
                Enrollment e = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
                if (e == null) {
                    System.out.println("Error receiveFromRegistrar Enrollment not found " + dto.enrollmentId());
                } else {
                    e.setGrade(dto.grade());
                    enrollmentRepository.save(e);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in receiveFromRegistrar " + e.getMessage());
        }
    }


    private void sendMessage(String s) {
        rabbitTemplate.convertAndSend(registrarServiceQueue.getName(), s);
    }
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Add functions for Gradebook service here
    // Example:
    public void listSectionsForInstructor(int instructorId) {
        // Implement functionality to list sections for instructor
    }

    public void viewEnrollmentsForSection(int sectionId) {
        // Implement functionality to view enrollments for section
    }

    public void enterFinalGradesForEnrollment(int enrollmentId, String finalGrade) {
        // Implement functionality to enter final grades for enrollment
    }

    public void createAssignment(AssignmentDTO assignment) {
        sendMessage("createAssignment " +  fromJsonString(assignment));
    }

    public void modifyAssignment(int assignmentId, String newTitle, String newDueDate) {
        // Implement functionality to modify assignment
    }

    public void gradeAssignment(int assignmentId, int studentId, int score) {
        // Implement functionality to grade assignment
    }

    // Add more functions as needed for Gradebook service

    public void modifySection(int sectionId, String newBuilding, String newRoom, String newTimes) {
        // Implement functionality to modify section details
    }

    public void createCourse(String courseId, String title, String department, String instructorEmail) {
        // Implement functionality to create a new course
    }

    public void modifyCourse(String courseId, String newTitle, String newDepartment, String newInstructorEmail) {
        // Implement functionality to modify course details
    }

    public void deleteCourse(String courseId) {
        // Implement functionality to delete a course
    }

    public void createUser(String name, String email, String type) {
        // Implement functionality to create a new user
    }

    public void modifyUser(int userId, String newName, String newEmail, String newType) {
        // Implement functionality to modify user details
    }

    public void deleteUser(int userId) {
        // Implement functionality to delete a user
    }

    public void createEnrollment(int studentId, int sectionId) {
        // Implement functionality to create a new enrollment
    }

    public void deleteEnrollment(int enrollmentId) {
        // Implement functionality to delete an enrollment
    }

}