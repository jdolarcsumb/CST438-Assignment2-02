package com.cst438.service;

import com.cst438.dto.*;
import com.cst438.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;

@Service
public class RegistrarServiceProxy {

    Queue registrarServiceQueue = new Queue("registrar_service", true);
    EnrollmentRepository enrollmentRepository;
    SectionRepository sectionRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    TermRepository termRepository;



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
            String[] parts = message.split(" ", 3);
            String call = parts[0];
            if (call.equals("addCourse")) {
                CourseDTO course = fromJsonString(parts[1], CourseDTO.class);
                Course c = new Course();
                c.setCredits(course.credits());
                c.setTitle(course.title());
                c.setCourseId(course.courseId());
                courseRepository.save(c);
            } else if (call.equals("deleteCourse")) {
                courseRepository.deleteById(parts[1]);
            } else if (call.equals("updateCourse")) {
                CourseDTO dto = fromJsonString(parts[1], CourseDTO.class);
                Course c = courseRepository.findById(dto.courseId()).orElse(null);
                if (c == null) {
                    System.out.println("Error receiveFromRegistrar: Course not found " + dto.courseId());
                } else {
                    c.setTitle(dto.title());
                    c.setCredits(dto.credits());
                    courseRepository.save(c);
                }
            } else if (call.equals("enrollCourse")) {
                int studentId = Integer.valueOf(parts[1]);
                int sectionNo = Integer.valueOf(parts[2]);
                Enrollment e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(studentId, sectionNo);
                if (e!=null) {
                    System.out.println("Error receiveFromRegistrar: already enrolled in this section");
                }
                e = new Enrollment();
                User student = userRepository.findById(studentId).orElse(null);
                if (student==null) {
                    System.out.println("Error receiveFromRegistrar: student id not found");
                }
                e.setStudent(student);
                Section section = sectionRepository.findById(sectionNo).orElse(null);
                if (section == null) {
                    System.out.println("Error receiveFromRegistrar: section number not found");
                }
                Date now = new Date();
                if (now.before(section.getTerm().getAddDate()) || now.after(section.getTerm().getAddDeadline())) {
                    System.out.println("Error receiveFromRegistrar: cannot enroll in this section due to date");
                }
                e.setSection(section);
                enrollmentRepository.save(e);
            } else if (call.equals("dropCourse")) {
                Enrollment e = enrollmentRepository.findById(Integer.valueOf(parts[1])).orElse(null);
                if (e==null) {
                    System.out.println("Error receiveFromRegistrar: enrollment not found");
                }
                Date now = new Date();
                if (now.after(e.getSection().getTerm().getDropDeadline()) ) {
                    System.out.println("Error receiveFromRegistrar: enrollment can not be deleted due to the drop deadline date");
                }
                enrollmentRepository.delete(e);
            } else if (call.equals("createUser")) {
                UserDTO userDTO = fromJsonString(parts[1], UserDTO.class);
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                User user = new User();
                user.setName(userDTO.name());
                user.setEmail(userDTO.email());

                // create password and encrypt it
                String password = userDTO.name() + "2024";
                String enc_password = encoder.encode(password);
                user.setPassword(enc_password);

                user.setType(userDTO.type());
                if (!userDTO.type().equals("STUDENT") &&
                        !userDTO.type().equals("INSTRUCTOR") &&
                        !userDTO.type().equals("ADMIN")) {
                    // invalid type
                    System.out.println("Error receiveFromRegistrar: invalid user type");
                }
                userRepository.save(user);
            } else if (call.equals("updateUser")) {
                UserDTO userDTO = fromJsonString(parts[1], UserDTO.class);
                User user = userRepository.findById(userDTO.id()).orElse(null);
                if (user==null) {
                    System.out.println("Error receiveFromRegistrar: User not found " + userDTO.id());
                }
                user.setName(userDTO.name());
                user.setEmail(userDTO.email());
                user.setType(userDTO.type());
                if (!userDTO.type().equals("STUDENT") &&
                        !userDTO.type().equals("INSTRUCTOR") &&
                        !userDTO.type().equals("ADMIN")) {
                    // invalid type
                    System.out.println("Error receiveFromRegistrar: invalid user type");
                }
                userRepository.save(user);
            } else if (call.equals("deleteUser")) {
                userRepository.deleteById(Integer.valueOf(parts[1]));
            } else if (call.equals("addSection")) {
                SectionDTO section = fromJsonString(parts[1], SectionDTO.class);
                Course course = courseRepository.findById(section.courseId()).orElse(null);
                if (course == null ){
                    System.out.println("Error receiveFromRegistrar: Course not found "+section.courseId());
                }
                Section s = new Section();
                s.setCourse(course);

                Term term = termRepository.findByYearAndSemester(section.year(), section.semester());
                if (term == null) {
                    System.out.println("Error receiveFromRegistrar: year, semester invalid ");
                }
                s.setTerm(term);

                s.setSecId(section.secId());
                s.setBuilding(section.building());
                s.setRoom(section.room());
                s.setTimes(section.times());

                User instructor = null;
                if (section.instructorEmail()==null || section.instructorEmail().equals("")) {
                    s.setInstructor_email("");
                } else {
                    instructor = userRepository.findByEmail(section.instructorEmail());
                    if (instructor == null || !instructor.getType().equals("INSTRUCTOR")) {
                        System.out.println("Error receiveFromRegistrar: email not found or not an instructor " + section.instructorEmail());
                    }
                    s.setInstructor_email(section.instructorEmail());
                }

                sectionRepository.save(s);

            } else if (call.equals("updateSection")) {
                SectionDTO section = fromJsonString(parts[1], SectionDTO.class);
                Section s = sectionRepository.findById(section.secNo()).orElse(null);
                if (s==null) {
                    System.out.println("Error receiveFromRegistrar: section not found "+section.secNo());
                }
                s.setSecId(section.secId());
                s.setBuilding(section.building());
                s.setRoom(section.room());
                s.setTimes(section.times());

                User instructor = null;
                if (section.instructorEmail()==null || section.instructorEmail().equals("")) {
                    s.setInstructor_email("");
                } else {
                    instructor = userRepository.findByEmail(section.instructorEmail());
                    if (instructor == null || !instructor.getType().equals("INSTRUCTOR")) {
                        System.out.println("Error receiveFromRegistrar: email not found or not an instructor " + section.instructorEmail());
                    }
                    s.setInstructor_email(section.instructorEmail());
                }
                sectionRepository.save(s);
            } else if (call.equals("deleteSection")) {
                sectionRepository.deleteById(Integer.valueOf(parts[1]));
            } else {
                System.out.println("Error receiveFromRegistrar: Action not recognized");
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
    public void getSectionsForInstructor(String instructorEmail, int year, String semester) {
        sendMessage("getSectionsForInstructor " +  asJsonString(instructorEmail) + " " + year + " " + asJsonString(semester));
    }

    public void getEnrollments(int sectionNo) {
        sendMessage("getSectionsForInstructor " +  sectionNo);
    }

    public void enterFinalGradesForEnrollment(List<EnrollmentDTO> dlist) {
        sendMessage("enterFinalGradesForEnrollment " +  asJsonString(dlist));
    }

    public void createAssignment(AssignmentDTO a) {
        sendMessage("createAssignment " +  asJsonString(a));
    }

    public void updateAssignment(AssignmentDTO a) {
        sendMessage("updateAssignment " +  asJsonString(a));
    }

    public void deleteAssignment(int assignmentId) {
        sendMessage("deleteAssignment " +  assignmentId);
    }

    public void updateGrades(List<GradeDTO> dlist) {
        sendMessage("updateGrades " +  asJsonString(dlist));
    }

    public void getAssignmentGrades(int assignmentId) {
        sendMessage("updateGrades " +  assignmentId);
    }

    public void getStudentAssignments(int studentId, int year, String semester) {
        sendMessage("getSectionsForInstructor " +  studentId + " " + year + " " + asJsonString(semester));
    }

    public void getAssignments(int secNo) {
        sendMessage("getAssignments " +  secNo);
    }


}