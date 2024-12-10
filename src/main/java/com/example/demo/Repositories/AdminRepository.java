package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Admin;

public interface AdminRepository extends JpaRepository<Admin,String> {
	
	public Admin findByEmail(String email);
	

}
