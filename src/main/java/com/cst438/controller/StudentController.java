package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.CourseDTO;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

  @Autowired
  UserRepository userRepository;
  @Autowired
  EnrollmentRepository enrollmentRepository;
  @Autowired
  SectionRepository sectionRepository;
  @Autowired
  CourseRepository courseRepository;

  // student gets transcript showing list of all enrollments
  // studentId will be temporary until Login security is implemented
  //example URL  /transcript?studentId=19803
  @GetMapping("/transcripts")
  public List<EnrollmentDTO> getTranscript(@RequestParam("studentId") int studentId) {
    // list course_id, sec_id, title, credit, grade in chronological order
    // user must be a student
    // hint: use enrollment repository method findEnrollmentByStudentIdOrderByTermId
    // remove the following line when done
    List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);
    User student = userRepository.findStudentByStudentId(studentId);
    if (student == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Student not found for id: " + studentId);
    }
    return getEnrollmentDTOS(enrollments);
  }

  static List<EnrollmentDTO> getEnrollmentDTOS(List<Enrollment> enrollments) {
    List<EnrollmentDTO> dtoEnrollments = new ArrayList<>();
    for (Enrollment e : enrollments) {
      dtoEnrollments.add(new EnrollmentDTO(
          e.getEnrollmentId(),
          e.getGrade(),
          e.getStudent().getId(),
          e.getStudent().getName(),
          e.getStudent().getEmail(),
          e.getSection().getCourse().getCourseId(),
          e.getSection().getSecId(),
          e.getSection().getSectionNo(),
          e.getSection().getBuilding(),
          e.getSection().getRoom(),
          e.getSection().getTimes(),
          e.getSection().getCourse().getCredits(),
          e.getSection().getTerm().getYear(),
          e.getSection().getTerm().getSemester()
      ));
    }
    return dtoEnrollments;
  }

  // student gets a list of their enrollments for the given year, semester
  // user must be student
  // studentId will be temporary until Login security is implemented
  @GetMapping("/enrollments")
  public List<EnrollmentDTO> getSchedule(
      @RequestParam("year") int year,
      @RequestParam("semester") String semester,
      @RequestParam("studentId") int studentId) {

    User student = userRepository.findStudentByStudentId(studentId);
    if (student == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Student not found for id: " + studentId);
    }
    List<Enrollment> enrollments = enrollmentRepository.findByYearAndSemesterOrderByCourseId(year, semester, studentId);
    return getEnrollmentDTOS(enrollments);
  }


  // student adds enrollment into a section
  // user must be student
  // return EnrollmentDTO with enrollmentId generated by database
  @PostMapping("/enrollments/sections/{sectionNo}")
  public EnrollmentDTO addCourse(
      @PathVariable int sectionNo,
      @RequestParam("studentId") int studentId ) {

    Section section = sectionRepository.findSectionBySectionNo(sectionNo);
    if (section.getCourse().getTitle().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "section not found: " + sectionNo);
    }

    long time_ms = System.currentTimeMillis();
    java.sql.Date today = new java.sql.Date(time_ms);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
    Date addDate = section.getTerm().getAddDate();
    Date addDeadline = section.getTerm().getAddDeadline();
    if(today.before(addDate) || today.after(addDeadline)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Date Invalid");
    }

    List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);
    List<EnrollmentDTO> dtoEnrollments = new ArrayList<>();
    if(!enrollments.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student is already enrolled");
    } else {
      Enrollment e = new Enrollment();
      e.setSection(section);
      e.setGrade(null);
      enrollmentRepository.save(e);
      return new EnrollmentDTO(
          e.getEnrollmentId(),
          e.getGrade(),
          e.getStudent().getId(),
          e.getStudent().getName(),
          e.getStudent().getEmail(),
          e.getSection().getCourse().getCourseId(),
          e.getSection().getSecId(),
          e.getSection().getSectionNo(),
          e.getSection().getBuilding(),
          e.getSection().getRoom(),
          e.getSection().getTimes(),
          e.getSection().getCourse().getCredits(),
          e.getSection().getTerm().getYear(),
          e.getSection().getTerm().getSemester()
      );
    }

    // check that the Section entity with primary key sectionNo exists
    // check that today is between addDate and addDeadline for the section
    // check that student is not already enrolled into this section
    // create a new enrollment entity and save.  The enrollment grade will
    // be NULL until instructor enters final grades for the course.

    // remove the following line when done.

  }

  // student drops a course
  // user must be student
  @DeleteMapping("/enrollments/{enrollmentId}")
  public void dropCourse(@PathVariable("enrollmentId") int enrollmentId) {

    Enrollment e = enrollmentRepository.findById(enrollmentId).orElse(null);
    if (e == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment could not be found");
    }
    Section s = sectionRepository.findSectionBySectionNo(e.getSection().getSectionNo());
    long time_ms = System.currentTimeMillis();
    java.sql.Date today = new java.sql.Date(time_ms);
    Date dropDeadline = s.getTerm().getDropDeadline();
    // check that today is not after the dropDeadline for section
    if (today.after(dropDeadline)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Drop Deadline Expired");
    }
    enrollmentRepository.delete(e);
  }
}