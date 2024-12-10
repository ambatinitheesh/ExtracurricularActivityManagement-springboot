package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.Model.Admin;
import com.example.demo.Repositories.AdminRepository;

@Component
public class AdminDAO {

	@Autowired
	AdminRepository repo;
	
	public Admin findAdmin(String email)
	{
		return repo.findByEmail(email);
	}
	
}
