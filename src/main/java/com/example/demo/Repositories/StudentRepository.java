package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Student;

import java.util.List;


public interface StudentRepository extends JpaRepository<Student, String> {
	
	public Student findByEmail(String email);

}
