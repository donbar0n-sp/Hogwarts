package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.hogwarts.school.model.Faculty;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Invoked method to add faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.info("Invoked method to find faculty with id = {}", id);
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(long id, Faculty faculty) {
        logger.info("Invoked method to edit faculty with id = {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("Trying to edit non-existent faculty with id = {}", id);
            return null;
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Invoked method to delete faculty with id = {}", id);
        if (!facultyRepository.existsById(id)) {
            logger.warn("Trying to delete non-existent faculty with id = {}", id);
            return;
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        logger.debug("Invoked method to get all faculties");
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findByName(String name) {
        logger.debug("Invoked method to find faculties by name = {}", name);
        return facultyRepository.findByNameIgnoreCase(name);
    }

    public Collection<Faculty> findByColor(String color) {
        logger.debug("Invoked method to find faculties by color = {}", color);
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public Collection<Student> getStudentsOfFaculty(long facultyId) {
        logger.info("Invoked method to get students of faculty with id = {}", facultyId);
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);
        if (faculty == null) {
            logger.warn("No faculty found with id = {} when trying to get students", facultyId);
            return List.of();
        }
        return faculty.getStudents();
    }

    public String getLongestFacultyName() {
        logger.info("Invoked method to get the longest faculty name");
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("No faculties found");
    }
}
