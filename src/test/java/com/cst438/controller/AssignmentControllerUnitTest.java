package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.GradeDTO;
import static org.mockito.ArgumentMatchers.anyInt;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssignmentController.class)
public class AssignmentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignmentRepository assignmentRepository;

    @MockBean
    private SectionRepository sectionRepository;

    @MockBean
    private GradeRepository gradeRepository;

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateAssignmentSuccess() throws Exception {
        Term term = new Term();
        term.setStartDate(Date.valueOf("2024-01-01"));
        term.setEndDate(Date.valueOf("2024-05-01"));

        Section section = new Section();
        section.setSectionNo(101);
        section.setCourse(new Course());
        section.setTerm(term);

        Assignment assignment = new Assignment();
        assignment.setAssignmentId(1);
        assignment.setTitle("New Assignment");
        assignment.setDueDate(Date.valueOf("2024-02-15"));
        assignment.setSection(section);

        given(sectionRepository.findSectionBySectionNoAndCourseIdAndSecId(any(Integer.class), any(String.class), any(Integer.class))).willReturn(section);
        given(assignmentRepository.save(any(Assignment.class))).willReturn(assignment);

        AssignmentDTO newAssignmentDTO = new AssignmentDTO(0, "New Assignment", "2024-02-15", "CST101", 1, 101);

        mockMvc.perform(post("/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAssignmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Assignment"));
    }

    @Test
    public void testAddAssignment() throws Exception {
        AssignmentDTO newAssignmentDTO = new AssignmentDTO(0, "Project 1", "2023-10-15", "CST101", 1, 101);
        Assignment newAssignment = new Assignment();
        newAssignment.setAssignmentId(1);
        newAssignment.setTitle(newAssignmentDTO.title());
        newAssignment.setDueDate(Date.valueOf(newAssignmentDTO.dueDate()));

        given(assignmentRepository.save(any(Assignment.class))).willReturn(newAssignment);

        mockMvc.perform(post("/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAssignmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Project 1"));

        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    @Test
    public void gradeInvalidAssignment() throws Exception {
        int invalidAssignmentId = 999; // Assuming 999 is an ID that doesn't exist
        given(assignmentRepository.findById(invalidAssignmentId)).willReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/assignments/{assignmentId}/grades", invalidAssignmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Assuming the controller returns 404 for non-existent resources
    }

    @Test
    public void gradeAssignment() throws Exception {
        int assignmentId = 1; // Assuming this is a valid assignment ID
        List<GradeDTO> gradesToUpdate = List.of(new GradeDTO(1, "Student Name", "student@example.com", "Assignment Title", "CST101", 101, 90));

        given(gradeRepository.findByEnrollmentIdAndAssignmentId(anyInt(), anyInt())).willReturn(new Grade());

        // Mock GET request to fetch assignment grades
        mockMvc.perform(MockMvcRequestBuilders.get("/assignments/{assignmentId}/grades", assignmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Mock PUT request to update grades
        mockMvc.perform(MockMvcRequestBuilders.put("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(gradesToUpdate)))
                .andExpect(status().isOk());
    }

    @Test
    public void addAssignmentInvalidSectionNumber() throws Exception {
        AssignmentDTO newAssignment = new AssignmentDTO(0, "New Assignment", "2023-12-15", "CST999", 999, 101); // Invalid section number

        mockMvc.perform(MockMvcRequestBuilders.post("/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newAssignment)))
                .andExpect(status().isBadRequest()) // Assuming the controller checks for section validity and returns BadRequest on failure
                .andReturn().getResponse().getContentAsString().contains("Invalid section number");
    }

    @Test
    public void addAssignment() throws Exception {
        AssignmentDTO newAssignment = new AssignmentDTO(0, "Project Introduction", "2024-01-20", "CST101", 1, 101);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newAssignment)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // Verify some fields in the response, assuming the response includes the created assignment details
        assertTrue(response.getContentAsString().contains("Project Introduction"));
    }


}