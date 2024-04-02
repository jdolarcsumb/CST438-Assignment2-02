package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;

import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateAssignmentSuccess() throws Exception {
        Section section = new Section();
        section.setSectionNo(101);
        section.setCourse(new Course());
        section.getTerm().setStartDate(Date.valueOf("2024-01-01"));
        section.getTerm().setEndDate(Date.valueOf("2024-05-01"));

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

}