package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @Test
    void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getId()).isGreaterThan(0);
        assertThat(response.getBody().getName()).isEqualTo("Gryffindor");
    }

    @Test
    void testGetFacultyInfo() {
        Faculty faculty = new Faculty();
        faculty.setName("Hufflepuff");
        faculty.setColor("Yellow");

        faculty = restTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class).getBody();
        Long id = faculty.getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hufflepuff");
    }

    @Test
    void testEditFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("Blue");

        faculty = restTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class).getBody();
        Long id = faculty.getId();

        faculty.setName("Updated Ravenclaw");
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(faculty);

        ResponseEntity<Faculty> response = restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.PUT, requestEntity, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Updated Ravenclaw");
    }

    @Test
    void testDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Slytherin");
        faculty.setColor("Green");

        faculty = restTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class).getBody();
        Long id = faculty.getId();

        restTemplate.delete(getBaseUrl() + "/" + id);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testFindFacultyByName() {
        Faculty faculty = new Faculty();
        faculty.setName("TestName");
        faculty.setColor("Gray");

        restTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(getBaseUrl() + "/findFaculty?name=TestName", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testFindFacultyByColor() {
        Faculty faculty = new Faculty();
        faculty.setName("TestColorFaculty");
        faculty.setColor("Purple");

        restTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(getBaseUrl() + "/findFaculty?color=Purple", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetAllFaculties() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(getBaseUrl() + "/findFaculty", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
