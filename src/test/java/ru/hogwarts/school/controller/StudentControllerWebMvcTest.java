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
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetStudentById() throws Exception {
        Student student = new Student(1L, "Harry", 17);
        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student(1L, "Hermione", 17);
        when(studentService.addStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hermione"));
    }

    @Test
    void testEditStudent() throws Exception {
        Student updated = new Student(1L, "Ron", 18);
        when(studentService.editStudent(Mockito.eq(1L), any(Student.class))).thenReturn(updated);

        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ron"))
                .andExpect(jsonPath("$.age").value(18));
    }

    @Test
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStudentsByAge() throws Exception {
        when(studentService.getStudentsByAge(16)).thenReturn(List.of(new Student(2L, "Ginny", 16)));

        mockMvc.perform(get("/student/filter-by-age").param("age", "16"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ginny"));
    }

    @Test
    void testGetStudentsByAgeRange() throws Exception {
        when(studentService.getStudentsByAgeRange(15, 17)).thenReturn(List.of(new Student(3L, "Luna", 16)));

        mockMvc.perform(get("/student/find-by-age-between").param("min", "15").param("max", "17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Luna"));
    }

    @Test
    void testGetFacultyOfStudent() throws Exception {
        Faculty faculty = new Faculty(1L, "Ravenclaw", "Blue");
        when(studentService.getFacultyOfStudent(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/1/showFaculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ravenclaw"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }
}
