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

import static com.cst438.test.utils.TestUtils.asJsonString;
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

    EnrollmentDTO enrollmentDTO = new EnrollmentDTO(
            1, "A", 3, "John Smith", "jsmith@csumb.edu", "cst499",
            2, 6, "024", "101", "T W 10:00-11:50", 3, 2023, "Spring"
    );

    EnrollmentDTO enrollmentDT0 = new EnrollmentDTO(
            2, "B", 2, "John Doe", "jdoe@csumb.edu", "cst101",
            1, 3, "030", "102", "T W 10:00-11:50", 3, 2024, "Fall"
    );

    int secN0 = enrollmentDTO.sectionNo();
    int studentId = enrollmentDTO.studentId();
    MockHttpServletResponse http;
    String esString = "/enrollments/sections/"+secN0+"?studentId="+studentId;

    @Test
    public void enrollStudent() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(esString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(enrollmentDTO)))
                .andReturn().getResponse();

        assertEquals(200, http.getStatus());
    }

    //Do this test by itself; it works.
    @Test
    public void enrollStudentAlreadyEnrolled() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(esString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(enrollmentDT0)))
                .andReturn().getResponse();

        assertEquals(200, http.getStatus());

        http = mockMvc.perform(MockMvcRequestBuilders.post(esString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(enrollmentDT0)))
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
