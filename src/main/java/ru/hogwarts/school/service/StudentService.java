package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.debug("Invoked method to create student");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Invoked method to find student with id = {}", id);
        return studentRepository.findById(id).orElse(null);
    }

    public Student editStudent(long id, Student student) {
        logger.info("Invoked method to edit student with id = {}", id);
        if (!studentRepository.existsById(id)) {
            logger.warn("Trying to edit non-existent student with id = {}", id);
            return null;
        }
        student.setId(id);
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.info("Invoked method to delete student with id = {}",id);
        if (!studentRepository.existsById(id)){
        logger.warn("Trying to delete non-existent student with id = {}", id);
        return;}
        studentRepository.deleteById(id);
    }

    public List<Student> getStudentsByAge(int age) {
        logger.debug("Invoked method to list students by age = {}", age);
        return studentRepository.findAll()
                .stream()
                .filter(student -> student.getAge() == age)
                .toList();
    }

    public Collection<Student> getStudentsByAgeRange(int min, int max) {
        logger.debug("Invoked method to list students aging between {} and {}", min,max);
        if (min > max) {
            logger.warn("Invalid age range: min {} is greater than max {}",min,max);
        }
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getFacultyOfStudent(long studentId) {
        logger.info("Invoked method to get faculty of student with id = {}",studentId);
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            logger.warn("No student found with id = {}", studentId);
            return null;
        }
        return student.getFaculty();
    }

    public long getStudentCount() {
        logger.info("Invoked method to get total student count");
        return studentRepository.getStudentCount();
    }

    public double getAverageStudentAge() {
        logger.info("Invoked method to get average student age");
        return studentRepository.getAverageStudentAge();
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Invoked method to get last five students");
        return studentRepository.getLastFiveStudents();
    }

    public List<String> getStudentNamesStartingWithA() {
        logger.info("Invoked method to get student names starting with 'А'");
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .map(String::toUpperCase)
                .filter(name -> name.startsWith("А"))
                .sorted()
                .toList();
    }

    public double getAverageAge() {
        logger.info("Invoked method to calculate average student age using Stream API");
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    public List<Student> getFirstSixStudents() {
        logger.info("Invoked method to get first six students");
        return studentRepository.findAll().stream().limit(6).toList();
    }
}
