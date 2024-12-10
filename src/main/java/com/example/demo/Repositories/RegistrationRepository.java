package com.example.demo.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Model.Registration;
import com.example.demo.Model.Student;

public interface RegistrationRepository extends JpaRepository<Registration, Long>{
	@Query("SELECT r FROM Registration r WHERE r.student.email = :email AND r.event.eventName = :eventname")
	List<Registration> findByStudentEmailAndEventName(@Param("email") String email, @Param("eventname") String eventname);

	public Optional<Registration> findById(Long id);
	
	@Query("SELECT r FROM Registration r WHERE r.student.email = :email")
	List<Registration> findByStudentEmail(@Param("email") String email);
	
	@Query("SELECT r.student FROM Registration r WHERE r.event.eventName = :eventName")
    List<Student> findStudentsByEventName(@Param("eventName") String eventName);

}
