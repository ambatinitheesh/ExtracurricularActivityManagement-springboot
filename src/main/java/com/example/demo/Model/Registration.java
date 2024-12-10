package com.example.demo.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_email", referencedColumnName = "email")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "event_name", referencedColumnName = "event_name")
    private Event event;

    
    private LocalDateTime registrationDate;

    // Constructors, getters, and setters
    public Registration() {}

    public Registration(Student student, Event event, LocalDateTime registrationDate) {
        this.student = student;
        this.event = event;
        this.registrationDate = registrationDate;
    }

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
