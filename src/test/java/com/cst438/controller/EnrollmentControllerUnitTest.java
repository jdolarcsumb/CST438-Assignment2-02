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

    @Autowired
    private EnrollmentController enrollmentController;

    MockHttpServletResponse http;

    EnrollmentDTO enrollmentDTO = new EnrollmentDTO(
            1, "A", 5, "John Smith", "jsmith@csumb.edu", "cst499",
            6, 6, "024", "101", "T W 10:00-11:50", 3, 2024, "Spring"
    );

    EnrollmentDTO enrollmentDT0 = new EnrollmentDTO(
            1, "B", 5, "John Smith", "jsmith@csumb.edu", "cst499",
            999, 999, "024", "101", "T W 10:00-11:50", 3, 2024, "Spring"
    );

    String enSecString = "/enrollments/sections/"+enrollmentDTO.sectionNo()+"?studentId="+enrollmentDTO.enrollmentId();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void enterFinalGrades() throws Exception {
        List<EnrollmentDTO>enrollments = new ArrayList<>();
        enrollments.add(enrollmentDTO);



        // issue a http POST request to SpringTestServer
        // specify MediaType for request and response data
        // convert section to String data and set as request content
        http = mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollments)))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, http.getStatus());
    }

    @Test
    public void enrollmentBadSectionNumber() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(enSecString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(enrollmentDT0)))
                .andReturn().getResponse();

        // Check the response code for 200 meaning OK
        assertNotEquals(200, http.getStatus());
    }

    @Test
    public void testEnrollIntoSection() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(enSecString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(enrollmentDTO)))
                .andReturn().getResponse();

        assertEquals(200, http.getStatus());
    }

}
