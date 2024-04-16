package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.GradeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    UserRepository userRepository;

    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    @GetMapping("/sections/{secNo}/assignments")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public List<AssignmentDTO> getAssignments(
            @PathVariable("secNo") int secNo) {


        List<Assignment> alist = assignmentRepository.findBySectionNoOrderByDueDate(secNo);

        List<AssignmentDTO> dlist = new ArrayList<>();

        for (Assignment a : alist) {
            dlist.add(new AssignmentDTO(
                    a.getAssignmentId(),
                    a.getTitle(),
                    a.getDueDateAsString(),
                    a.getSection().getCourse().getCourseId(),
                    a.getSection().getSecId(),
                    a.getSection().getSectionNo()));
        }

        return dlist;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    @PostMapping("/assignments")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public AssignmentDTO createAssignment(
            @RequestBody AssignmentDTO dto) {
        Assignment a = new Assignment();
        a.setTitle(dto.title());
        a.setDueDate(dto.dueDate());
        Section s = sectionRepository.findById(dto.secNo()).orElse(null);
        if (s==null) {
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "section not found");
        }
        a.setSection(s);
        assignmentRepository.save(a);

        return new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDateAsString(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo());
    }

    // update assignment for a section.  Only title and dueDate may be changed.
    // user must be instructor of the section
    // return updated AssignmentDTO
    @PutMapping("/assignments")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto) {
        Assignment a = assignmentRepository.findById(dto.id()).orElse(null);
        if (a == null) {
            throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "assignment not found");
        }
        a.setTitle(dto.title());
        a.setDueDate(dto.dueDate());
        assignmentRepository.save(a);

        return new AssignmentDTO(
                a.getAssignmentId(),
                a.getTitle(),
                a.getDueDateAsString(),
                a.getSection().getCourse().getCourseId(),
                a.getSection().getSecId(),
                a.getSection().getSectionNo());
    }

    // delete assignment for a section
    // logged in user must be instructor of the section
    @DeleteMapping("/assignments/{assignmentId}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId) {
        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
        if (a != null) {
            assignmentRepository.delete(a);
        }
    }


    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @GetMapping("/assignment/{assignmentId}/grades")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public List<GradeDTO> getAssignmentGrades(@PathVariable("assignmentId") int assignmentId) {
        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
        if (a==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment not found");
        }
        List<GradeDTO> dlist = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(a.getSection().getSectionNo());
        for (Enrollment e : enrollments) {
            Grade g = gradeRepository.findByEnrollmentIdAndAssignmentId(e.getEnrollmentId(), a.getAssignmentId());
            if (g==null) {
                // create a grade with null score.
                g = new Grade();
                g.setAssignment(a);
                g.setEnrollment(e);
                gradeRepository.save(g);
            }
            dlist.add(new GradeDTO(
                    g.getGradeId(),
                    e.getStudent().getName(),
                    e.getStudent().getEmail(),
                    a.getTitle(),
                    a.getSection().getCourse().getCourseId(),
                    a.getSection().getSecId(),
                    g.getScore()));
        }
        return dlist;
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    @PutMapping("/grades")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_INSTRUCTOR')")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {
        for (GradeDTO g: dlist) {
            Grade grade = gradeRepository.findById(g.gradeId()).orElse(null);
            if (grade!=null) {
                grade.setScore(g.score());
                gradeRepository.save(grade);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "grade not found "+ g.gradeId());
            }
        }
    }



    // student lists their assignments/grades for an enrollment ordered by due date
    // student must be enrolled in the section
    @GetMapping("/assignments")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_STUDENT')")
    public List<AssignmentStudentDTO> getStudentAssignments(
            Principal principal,
            @RequestParam("year") int year,
            @RequestParam("semester") String semester) {

        // check that this enrollment is for the logged in user student.
        User student = userRepository.findByEmail(principal.getName());

        List<AssignmentStudentDTO> dlist = new ArrayList<>();
        List<Assignment> alist = assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(student.getId(), year, semester);
        for (Assignment a : alist) {

            Enrollment e = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(a.getSection().getSectionNo(), student.getId());
            if (e==null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "enrollment not found studentId:"+student.getId()+" sectionNo:"+a.getSection().getSectionNo());
            }

            // if assignment has been graded, include the score
            Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId( e.getEnrollmentId(), a.getAssignmentId());
            dlist.add(new AssignmentStudentDTO(
                    a.getAssignmentId(),
                    a.getTitle(),
                    a.getDueDate(),
                    a.getSection().getCourse().getCourseId(),
                    a.getSection().getSecId(),
                    (grade!=null)? grade.getScore(): null ));

        }
        return dlist;
    }
}

