package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrollmentController.class)
public class EnrollmentControllerUnitTest {

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentController enrollmentController;

    @Mock
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void enterFinalClassGradesForAll() throws Exception {
        EnrollmentDTO enrollmentDTO = new EnrollmentDTO(
                1, "B", 3, "John Smith", "jsmith@csumb.edu", "cst363",
                9, 9, "052", "104", "T W 10:00-11:50", 4, 2024, "Spring"
        );

        given(enrollmentRepository.saveAll(any())).willReturn(Arrays.asList(new Enrollment()));

        mockMvc.perform(put("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(enrollmentDTO))))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateEnrollmentGrade() throws Exception {
        List<EnrollmentDTO> enrollments = List.of(new EnrollmentDTO(1, "A", 1, "John Doe", "john@example.com", "CST101", 101, 1, "Building 1", "Room 100", "MW 10-12", 3, 2023, "Fall"));

        // Mock the GET request to retrieve enrollments
        mockMvc.perform(get("/sections/{sectionNo}/enrollments", 101)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Update grades in the DTOs as needed here

        // Mock the PUT request to update enrollments
        mockMvc.perform(put("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollments)))
                .andExpect(status().isOk());
    }

    @Test
    public void testEnrollPastDeadline() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/enrollments/sections/101?studentId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEnrollBadSectionNumber() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/enrollments/sections/999?studentId=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEnrollIntoSection() throws Exception {
        EnrollmentDTO enrollmentDTO = new EnrollmentDTO(1, "A", 1, "John Doe", "jdoe@example.com", "CST101", 101, 1, "Building 1", "101", "MWF 10-11 AM", 3, 2023, "Fall");

        mockMvc.perform(MockMvcRequestBuilders.post("/enrollments/sections/101?studentId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(enrollmentDTO)))
                .andExpect(status().isOk());
    }

}
