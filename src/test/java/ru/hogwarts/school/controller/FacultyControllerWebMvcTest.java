package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetFacultyById() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(facultyService.findFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"));
    }

    @Test
    void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Hufflepuff", "Yellow");
        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Yellow"));
    }

    @Test
    void testEditFaculty() throws Exception {
        Faculty updated = new Faculty(1L, "Slytherin", "Green");
        when(facultyService.editFaculty(Mockito.eq(1L), any(Faculty.class))).thenReturn(updated);

        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Slytherin"));
    }

    @Test
    void testDeleteFaculty() throws Exception {
        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindByColor() throws Exception {
        when(facultyService.findByColor("Red")).thenReturn(List.of(new Faculty(2L, "Gryffindor", "Red")));

        mockMvc.perform(get("/faculty/findFaculty").param("color", "Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"));
    }

    @Test
    void testFindByName() throws Exception {
        when(facultyService.findByName("Hufflepuff")).thenReturn(List.of(new Faculty(3L, "Hufflepuff", "Yellow")));

        mockMvc.perform(get("/faculty/findFaculty").param("name", "Hufflepuff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value("Yellow"));
    }

    @Test
    void testGetStudentsOfFaculty() throws Exception {
        Student student1 = new Student(1L, "Harry Potter", 15);
        Student student2 = new Student(2L, "Hermione Granger", 15);

        when(facultyService.getStudentsOfFaculty(1L)).thenReturn(Set.of(student1, student2));

        mockMvc.perform(get("/faculty/1/showStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Harry Potter", "Hermione Granger")));
    }
}
//commit
