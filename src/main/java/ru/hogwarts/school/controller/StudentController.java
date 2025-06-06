package ru.hogwarts.school.controller;

import ru.hogwarts.school.model.Faculty;
import  ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    private int count = 1;
    private final Object lock = new Object();

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping("{id}")
    public ResponseEntity<Student> editStudent(@RequestBody Student student, @PathVariable Long id) {
        Student foundStudent = studentService.editStudent(id, student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("filter-by-age")
    public ResponseEntity<List<Student>> getStudentsByAge(@RequestParam int age) {
        return ResponseEntity.ok(studentService.getStudentsByAge(age));
    }

    @GetMapping("find-by-age-between")
    public ResponseEntity<Collection<Student>> getStudentsByAge(@RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(studentService.getStudentsByAgeRange(min, max));
    }

    @GetMapping("{id}/showFaculty")
    public ResponseEntity<Faculty> getFacultyOfStudent(@PathVariable long id) {
        Faculty faculty = studentService.getFacultyOfStudent(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("count")
    public ResponseEntity<Long> getStudentCount() {
        return ResponseEntity.ok(studentService.getStudentCount());
    }

    @GetMapping("average-age")
    public ResponseEntity<Double> getAverageStudentAge() {
        return ResponseEntity.ok(studentService.getAverageStudentAge());
    }

    @GetMapping("last-five-students")
    public ResponseEntity<List<Student>> getLastFiveStudents() {
        return ResponseEntity.ok(studentService.getLastFiveStudents());
    }

    @GetMapping("/students/by-a")
    public List<String> getStudentsStartingWithA() {
        return studentService.getStudentNamesStartingWithA();
    }

    @GetMapping("/students/average-age")
    public double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/students/print-parallel")
    public void printStudentsInParallel() {
        List<Student> students = studentService.getFirstSixStudents();

        if (students.size() < 6) {
            System.out.println("The minimum amount of students is 6");
            return;
        }

        System.out.println("Main thread:");
        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1:");
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2:");
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        });

        thread1.start();
        thread2.start();
    }

    @GetMapping("/students/print-synchronized")
    public void printStudentsSynchronized() {
        List<Student> students = studentService.getFirstSixStudents();

        if (students.size() < 6) {
            System.out.println("The minimum amount of students is 6");
        }

        System.out.println("Main thread:");
        synchronizedPrint(students.get(0).getName());
        synchronizedPrint(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            System.out.println("Thread 1:");
            synchronizedPrint(students.get(2).getName());
            synchronizedPrint(students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("Thread 2:");
            synchronizedPrint(students.get(4).getName());
            synchronizedPrint(students.get(5).getName());
        });

        thread1.start();
        thread2.start();
    }

    private void synchronizedPrint(String name) {
        synchronized (lock) {
            System.out.println(count + ": " + name);
            count++;
        }
    }
}
