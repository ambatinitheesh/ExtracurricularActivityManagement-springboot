package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Club;

import java.util.List;


public interface ClubRepository extends JpaRepository<Club, String> {
	public Club findByClubName(String clubName);

}
