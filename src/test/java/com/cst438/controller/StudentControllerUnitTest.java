package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EnrollmentController.class)
public class StudentControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SectionRepository sectionRepository;

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private EnrollmentDTO testEnrollmentDTO;

    @BeforeEach
    public void setup() {
        testEnrollmentDTO = new EnrollmentDTO(0, "A", 3, "John Doe", "jdoe@example.com", "cst101", 1, 8, "Library", "101", "T Th 9:00-10:15", 3, 2023, "Fall");
    }

    @Test
    public void enrollStudentInSectionSuccess() throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(1);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        mvc.perform(post("/enrollments/sections/" + testEnrollmentDTO.sectionNo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEnrollmentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(1))
                .andExpect(jsonPath("$.courseId").value("cst101"));

        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    public void enrollStudentInSectionAlreadyEnrolled() throws Exception {
        // Simulate already enrolled scenario
        when(enrollmentRepository.findEnrollmentBySectionNoAndStudentId(testEnrollmentDTO.sectionNo(), testEnrollmentDTO.studentId())).thenReturn(new Enrollment());

        mvc.perform(post("/enrollments/sections/" + testEnrollmentDTO.sectionNo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEnrollmentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("already enrolled")));

        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
