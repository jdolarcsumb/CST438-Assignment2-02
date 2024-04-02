package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    // Helper methods and additional tests as needed...
}
