package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static com.cst438.test.utils.TestUtils.asJsonString;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
public class EnrollmentControllerUnitTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    MockHttpServletResponse http;

    EnrollmentDTO enrollmentDTO = new EnrollmentDTO(
            1, "A", 5, "John Smith", "jsmith@csumb.edu", "cst499",
            2, 6, "024", "101", "T W 10:00-11:50", 3, 2023, "Spring"
    );

    EnrollmentDTO enrollmentDT0 = new EnrollmentDTO(
            1, "B", 5, "John Smith", "jsmith@csumb.edu", "cst499",
            999, 999, "024", "101", "T W 10:00-11:50", 3, 2023, "Spring"
    );

    List<EnrollmentDTO>enrollments = new ArrayList<>();

    int secN0 = enrollmentDTO.sectionNo();
    int studentId = enrollmentDTO.studentId();
    String enrollString = "/enrollments";

    String enSecString = "/enrollments/sections/"+secN0+"?studentId="+studentId;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void enterStudentGrade() throws Exception {
        enrollments.add(enrollmentDTO);

        http = mockMvc.perform(MockMvcRequestBuilders.put(enrollString).accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(enrollments)))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, http.getStatus());
    }

    @Test
    public void enrollBadSectionNumber() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(enrollString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(enrollmentDT0)))
                .andReturn().getResponse();

        // Check the response code for 200 meaning OK
        assertNotEquals(200, http.getStatus());
    }

    @Test
    public void enrollIntoSection() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(enSecString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(enrollmentDTO)))
                .andReturn().getResponse();

        System.out.println(http);

        assertEquals(200, http.getStatus());
    }

}
