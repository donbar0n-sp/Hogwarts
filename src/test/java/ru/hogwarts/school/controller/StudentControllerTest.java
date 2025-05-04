package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/student";
    }

    private String getFacultyUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @Test
    void testCreateStudent() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(11);

        ResponseEntity<Student> response = restTemplate.postForEntity(getBaseUrl(), student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isGreaterThan(0);
        assertThat(response.getBody().getName()).isEqualTo("Harry Potter");
    }

    @Test
    void testGetStudentInfo() {
        Student student = new Student();
        student.setName("Hermione Granger");
        student.setAge(12);

        student = restTemplate.postForEntity(getBaseUrl(), student, Student.class).getBody();
        Long id = student.getId();

        ResponseEntity<Student> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Hermione Granger");
    }

    @Test
    void testEditStudent() {
        Student student = new Student();
        student.setName("Ron Weasley");
        student.setAge(12);

        student = restTemplate.postForEntity(getBaseUrl(), student, Student.class).getBody();
        Long id = student.getId();

        student.setName("Ronald Weasley");

        HttpEntity<Student> requestEntity = new HttpEntity<>(student);

        ResponseEntity<Student> response = restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.PUT, requestEntity, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Ronald Weasley");
    }

    @Test
    void testDeleteStudent() {
        Student student = new Student();
        student.setName("Neville Longbottom");
        student.setAge(13);

        student = restTemplate.postForEntity(getBaseUrl(), student, Student.class).getBody();
        Long id = student.getId();

        restTemplate.delete(getBaseUrl() + "/" + id);

        ResponseEntity<Student> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetStudentsByAge() {
        Student student = new Student();
        student.setName("Luna Lovegood");
        student.setAge(14);

        restTemplate.postForEntity(getBaseUrl(), student, Student.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl() + "/filter-by-age?age=14", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetStudentsByAgeRange() {
        Student student = new Student();
        student.setName("Cho Chang");
        student.setAge(15);

        restTemplate.postForEntity(getBaseUrl(), student, Student.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl() + "/find-by-age-between?min=14&max=16", Student[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetFacultyOfStudent() {
        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("Blue");

        faculty = restTemplate.postForEntity(getFacultyUrl(), faculty, Faculty.class).getBody();

        // Create JSON manually instead of using a Java object
        String studentJson = String.format("""
        {
            "name": "Padma Patil",
            "age": 16,
            "faculty": { "id": %d }
        }
        """, faculty.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(studentJson, headers);

        ResponseEntity<Student> studentResponse = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                request,
                Student.class
        );

        Student student = studentResponse.getBody();

        // Now test the GET /{id}/showFaculty endpoint
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + student.getId() + "/showFaculty",
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Ravenclaw");
    }


}
