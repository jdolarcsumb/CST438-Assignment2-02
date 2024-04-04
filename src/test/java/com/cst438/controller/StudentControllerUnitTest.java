package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
public class StudentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

//    private ObjectMapper objectMapper = new ObjectMapper();

    EnrollmentDTO enrollmentDTO = new EnrollmentDTO(0, "A", 3, "John Doe", "jdoe@example.com", "cst101", 1, 8, "Library", "101", "T Th 9:00-10:15", 3, 2023, "Fall");
    int secN0 = enrollmentDTO.sectionNo();
    int studentId = enrollmentDTO.studentId();
    MockHttpServletResponse http;
    String esString = "/enrollments/sections/"+secN0+"?studentId="+studentId;

    @Test
    public void enrollStudent() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(esString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(enrollmentDTO)))
                .andReturn().getResponse();

        assertEquals(200, http.getStatus());
    }

    @Test
    public void enrollStudentAlreadyEnrolled() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(esString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(enrollmentDTO)))
                .andReturn().getResponse();

        assertEquals(200, http.getStatus());

        http = mockMvc.perform(MockMvcRequestBuilders.post(esString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(enrollmentDTO)))
                .andReturn().getResponse();

        assertNotEquals(200, http.getStatus());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
