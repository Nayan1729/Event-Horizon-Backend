package org.springboot.security.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springboot.security.entities.Student;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {

    List<Student> students = new ArrayList<>(
            List.of(
                        new Student(101,"Nayan",12),
                        new Student(102,"Vasi",13)
            )
    );

    @GetMapping("/students")
    public List<Student> getStudents() {

        return students;
    }
    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        students.add(student);
        return student;
    }
    @GetMapping("/csrf")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }
}
