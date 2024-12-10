package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Event;

import java.util.List;


public interface EventRepository extends JpaRepository<Event,String>{
	
	
	public Event findByEventName(String eventName);
	public List<Event> findByCategory(String category);
	
}
