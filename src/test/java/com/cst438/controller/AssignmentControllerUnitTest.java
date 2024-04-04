package com.cst438.controller;

import com.cst438.dto.AssignmentDTO;;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.ArrayList;
import java.util.List;
import static com.cst438.test.utils.TestUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@AutoConfigureMockMvc
@SpringBootTest
public class AssignmentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    AssignmentDTO assignmentDTO = new AssignmentDTO(2, "Project Introduction", "2024-01-20", "CST101", 1, 1);
    AssignmentDTO assignmentDTO1 = new AssignmentDTO(1, "An Assignment", "2024-02-10", "CST202", 2, 2);
    AssignmentDTO assignmentDT0 = new AssignmentDTO(99, "The Projectz", "2024-02-20", "CST102", 2, 999);
    int id = assignmentDTO.id();
    int id0 = assignmentDTO1.id();
    int id1 = assignmentDT0.id();

    MockHttpServletResponse http;
    MockHttpServletResponse http0;

    String grade_httpString0 = "/assignments/"+id0+"/grades";
    String grade_httpString1 = "/assignments/"+id1+"/grades";
    String assignmentString = "/assignments";
    String gradeString = "/grades";


    @Test
    public void gradeInvalidAssignment() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(grade_httpString1).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(assignmentDT0)))
                .andReturn().getResponse();

        assertNotEquals(200, http.getStatus());
    }

    @Test
    public void gradeAssignment() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.get(grade_httpString0)).andReturn().getResponse();
        assertEquals(200, http.getStatus());

        http0 = mockMvc.perform(MockMvcRequestBuilders.post(gradeString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(assignmentDTO)))
                .andReturn().getResponse();

        assertEquals(200, http.getStatus());
    }

    @Test
    public void addAssignmentInvalidSectionNumber() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(assignmentString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(assignmentDTO1)))
                .andReturn().getResponse();

        // Check the response code for 200 meaning OK
        assertEquals(404, http.getStatus());
    }

    @Test
    public void addAssignment() throws Exception {
        http = mockMvc.perform(MockMvcRequestBuilders.post(assignmentString).accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).content(asJsonString(assignmentDTO)))
                .andReturn().getResponse();

        // Check the response code for 200 meaning OK
        assertEquals(200, http.getStatus());
    }


}