package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
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
        //  remove the following line when done
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(sectionNo);
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

    // instructor uploads enrollments with the final grades for the section
    // user must be instructor for the section
    @PutMapping("/enrollments")
    public void updateEnrollmentGrade(@RequestBody List<EnrollmentDTO> dlist) {
        // For each EnrollmentDTO in the list
        //  find the Enrollment entity using enrollmentId
        //  update the grade and save back to database
        for (EnrollmentDTO dto : dlist) {
            Enrollment e = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
            if (e == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "enrollment not found " + dto.enrollmentId());
            } else {
                e.setGrade(dto.grade());
                enrollmentRepository.save(e);
            }
        }
    }

}
