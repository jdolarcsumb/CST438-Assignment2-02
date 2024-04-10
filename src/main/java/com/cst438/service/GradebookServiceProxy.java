package com.cst438.service;

import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;
import com.cst438.domain.*;
import com.cst438.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@Service
public class GradebookServiceProxy {
    /*
     * create or use existing message queue
     */
    Queue gradebookServiceQueue = new Queue("gradebook_service", true);
    @Bean
    public Queue createQueue() { return new Queue("registrar_service", true); }
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    SectionRepository sectionRepository;

    public void addCourse(CourseDTO course) {
        sendMessage( "addCourse_" +asJsonString(course) );
    }
    public void updateCourse(CourseDTO course) {
        sendMessage("updateCourse_" +asJsonString(course) );
    }
    public void deleteCourse(String courseId) {sendMessage("deleteCourse_" +courseId);}
    public void addSection(SectionDTO s) {sendMessage("addSection_" +asJsonString(s));}
    public void updateSection(SectionDTO s) {sendMessage("updateSection_" +asJsonString(s));}
    public void deleteSection(int sectionNo) {sendMessage("deleteSection_" +sectionNo);}
    public void addUser(UserDTO user) {sendMessage("createUser_" + asJsonString(user));}
    public void updateUser (UserDTO user) { sendMessage("updateUser_" +asJsonString(user));}
    public void deleteUser (int userId) { sendMessage( "deleteUser_" +userId);}
    public void enrollCourse(EnrollmentDTO e) { sendMessage("enrollCourse_" +asJsonString(e));}
    public void dropCourse(int enrollmentId) {
        sendMessage("dropCourse_" +enrollmentId);
    }

    @RabbitListener(queues = "registrar_service")
    public void receiveFromGradebook(String message)  {
        //TODO implement this message
        // receive message from Gradebook service

        try {
            System.out.println("receive from Gradebook " + message);
            String[] parts = message.split("_", 2);
            if (parts[0].equals("updateEnrollment")){
                EnrollmentDTO dto = fromJsonString(parts[1], EnrollmentDTO.class);
                Enrollment e = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
                if (e == null) {
                    System.out.println("Error receive from GradeBook Enrollment not found " + dto.enrollmentId());
                } else {
                    e.setGrade(dto.grade());
                    enrollmentRepository.save(e);
                }
            } else if (parts[0].equals("enterFinalGradesForEnrollment")) {
                EnrollmentDTO dto = fromJsonString(parts[1], EnrollmentDTO.class);
                Enrollment e = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
                if (e == null) {
                    System.out.println("Error receive from GradeBook Enrollment not found " + dto.enrollmentId());
                } else {
                    e.setGrade(dto.grade());
                    enrollmentRepository.save(e);
                }
            } else {
                System.out.println("Command " + parts[0] + " not recognized");
            }
        } catch (Exception e) {
            System.out.println("Exception in receivedFromGradebook " + e.getMessage());
        }
    }

    private void sendMessage(String s) {
        System.out.println("Registrar to Gradebook " +s);
        rabbitTemplate.convertAndSend(gradebookServiceQueue.getName(), s);
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
}