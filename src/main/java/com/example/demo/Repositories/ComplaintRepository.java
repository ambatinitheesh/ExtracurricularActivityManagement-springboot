package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint,Long> {

}
