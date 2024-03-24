package com.cst438.controller;

import static com.cst438.controller.StudentController.getEnrollmentDTOS;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.User;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {
    @Autowired
    EnrollmentRepository enrollmentRepository;

    // instructor downloads student enrollments for a section, ordered by student name
    // user must be instructor for the section
    @GetMapping("/sections/{sectionNo}/enrollments")
    public List<EnrollmentDTO> getEnrollments(
            @PathVariable("sectionNo") int sectionNo ) {
        //  hint: use enrollment repository findEnrollmentsBySectionNoOrderByStudentName method
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(sectionNo);
        return getEnrollmentDTOS(enrollments);
    }

    // instructor uploads enrollments with the final grades for the section
    // user must be instructor for the section
    @PutMapping("/enrollments")
    public void updateEnrollmentGrade(@RequestBody List<EnrollmentDTO> dlist) {
        // For each EnrollmentDTO in the list
        for (EnrollmentDTO dto : dlist) {
            //  find the Enrollment entity using enrollmentId
            Enrollment e = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
            if (e == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "enrollment not found " + dto.enrollmentId());
            }
            //  update the grade and save back to database
            e.setGrade(dto.grade());
            enrollmentRepository.save(e);
        }
    }

}
