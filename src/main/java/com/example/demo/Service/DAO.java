package com.example.demo.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Club;
import com.example.demo.Model.Complaint;
import com.example.demo.Model.Event;
import com.example.demo.Model.Registration;
import com.example.demo.Model.Student;
import com.example.demo.Repositories.ClubRepository;
import com.example.demo.Repositories.ComplaintRepository;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.RegistrationRepository;
import com.example.demo.Repositories.StudentRepository;

@Component
public class DAO {
	@Autowired
	StudentRepository repo1;
	
	@Autowired
	EventRepository repo2;
	
	@Autowired
	RegistrationRepository repo3;
	
	@Autowired
	ClubRepository repo4;

	 @Autowired
	    private ComplaintRepository complaintRepository;

	    // Save a new complaint
	    public Complaint saveComplaint(Complaint complaint) {
	        return complaintRepository.save(complaint);
	    }
	
	
	public List<Event> findByCategory(String category)
	{
		return repo2.findByCategory(category);
	}
	public List<Student> getStudentsByEventName(String eventName) {
        return repo3.findStudentsByEventName(eventName);
    }
	
	
	public List<Registration> getAllRegistrations()
	{
		return repo3.findAll();
	}
	
	 public Student saveStudent(String firstname, String lastname, String email, String password, MultipartFile image) {
	        Student student = new Student();
	        student.setFirstname(firstname);
	        student.setLastname(lastname);
	        student.setEmail(email);
	        student.setPassword(password);

	        try {
	            if (image != null && !image.isEmpty()) {
	                student.setImage(image.getBytes());
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return repo1.save(student);
	    }
	
	public void addStudent(Student student)
	{
		repo1.save(student);
	}
	
	public List<Student> getall()
	{
		return repo1.findAll();
	}
	
	public List<Student> studentpages(int page,int limit)
	{
		Sort sort = Sort.by(Sort.Direction.ASC,"email");
		org.springframework.data.domain.Pageable pageable=PageRequest.of(page, limit,sort);
		return  repo1.findAll(pageable).toList();
	}
	public long countStudents() {
	    return repo1.count();  // This will return the total number of students
	}
	
	public List<Event>eventpages(int page,int limit)
	{
		Sort sort = Sort.by(Sort.Direction.ASC,"eventName");
		org.springframework.data.domain.Pageable pageable=PageRequest.of(page, limit,sort);
		return  repo2.findAll(pageable).toList();
	}
	public Student findStudent(String email)
	{
		return repo1.findByEmail(email);
	}
	
	public void deleteStudent(String email)
	{
		repo1.delete(findStudent(email));
	}
	public void updateStudent(Student student) {
		deleteStudent(student.getEmail());
		repo1.save(student);
	}
	
	public void addEvent(Event event)
	{
		repo2.save(event);
	}

    public Event saveEvent(String eventName, String coordinatorName, String date, String time, int capacity, 
                           String venue, String description, String category, String clubName,MultipartFile image) {
        Event event = new Event();
        event.setEventName(eventName);
        event.setCoordinatorName(coordinatorName);
        event.setDate(date);
        event.setTime(time);
        event.setCapacity(capacity);
        event.setVenue(venue);
        event.setDescription(description);
        event.setCategory(category);
        Club club = repo4.findByClubName(clubName); 
        event.setClub(club);  // Associate the club with the event

        try {
            if (image != null && !image.isEmpty()) {
                event.setImage(image.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return repo2.save(event);
    }
    public Event updateEvent(String eventName, String coordinatorName, String date, String time, int capacity,
            String venue, String description, String category, String clubName, MultipartFile image) {
    		Event existingEvent = findEvent(eventName);

    		if (existingEvent == null) {
    			throw new IllegalArgumentException("Event not found with name: " + eventName);
    			}

			existingEvent.setCoordinatorName(coordinatorName);
			existingEvent.setDate(date);
			existingEvent.setTime(time);
			existingEvent.setCapacity(capacity);
			existingEvent.setVenue(venue);
			existingEvent.setDescription(description);
			existingEvent.setCategory(category);
			existingEvent.setClubName(clubName);

				if (image != null && !image.isEmpty()) {
				try {
				existingEvent.setImage(image.getBytes());
				} catch (IOException e) {
				throw new RuntimeException("Error updating image file", e);
				}
				}
				
				return repo2.save(existingEvent);
    			}
	public List<Event>getallevents()
	{
		return repo2.findAll();
	}
	
	public Event findEvent(String eventname) {
		return repo2.findByEventName(eventname);
	}
	
	public void deleteEvent(String eventname) {
		repo2.delete(findEvent(eventname));
	}
	
	public void registerEvent(String eventName, String email) {
	    Event event = findEvent(eventName);
	    if (event.getCapacity() <= 0) {
	        throw new IllegalArgumentException("Event capacity is full. Registration not allowed.");
	    }

	    Student student = findStudent(email);
	    if (checkregistration(email, eventName)) {
	        throw new IllegalArgumentException("Student is already registered for this event.");
	    }

	    Registration registration = new Registration(student, event, LocalDateTime.now());
	    repo3.save(registration);

	    // Decrease the capacity
	    event.setCapacity(event.getCapacity() - 1);
	    repo2.save(event); // Save the updated event (assuming `repo2` is for `Event` repository)
	}

	
	public boolean checkregistration(String email, String eventname) {
	    List<Registration> registrations = repo3.findByStudentEmailAndEventName(email, eventname);
	    return !registrations.isEmpty(); // true if there are any registrations
	}
	
	public Optional<Registration> findRegister(long id)
	{
		return repo3.findById(id);
	}

	public void unregister(String email, String eventName) {
	    List<Registration> registrations = repo3.findByStudentEmailAndEventName(email, eventName);
	    if (!registrations.isEmpty()) {
	        for (Registration registration : registrations) {
	            repo3.deleteById(registration.getId());
	        }
	        // Increase the capacity
	        Event event = findEvent(eventName);
	        event.setCapacity(event.getCapacity() + 1);
	        repo2.save(event); // Save the updated event
	    } else {
	        throw new IllegalArgumentException("No registration found for the specified event and email");
	    }
	}


	public List<Event> getEventsByStudentEmail(String email) {
	    Student student = findStudent(email);
	    List<Registration> registrations = repo3.findByStudentEmail(email);
	    return registrations.stream()
	            .map(Registration::getEvent)
	            .toList();
	}

	public Club saveclub(String clubname,String coordinatorName,String description,MultipartFile image)
	{
		Club club=new Club();
		club.setClubName(clubname);
		club.setCoordinatorName(coordinatorName);
		club.setDescription(description);
		try {
         if (image != null && !image.isEmpty()) {
              club.setImage(image.getBytes());
         }
    } catch (IOException e) {
    	e.printStackTrace();
     }
		return repo4.save(club);
		
	}

	public List<Club>viewclubs()
	{
		return repo4.findAll();
	}
	public List<String> getClubNames() {
        return repo4.findAll()
                .stream()
                .map(Club::getClubName)
                .collect(Collectors.toList());
    }
	
	public Club findClub(String clubName)
	{
		return repo4.findByClubName(clubName);
	}
	public Club updateClubDetails(String clubName, String coordinatorName, String description, MultipartFile image) {
	    Club club = findClub(clubName); // Assuming this retrieves the existing club.

	    if (club == null) {
	        throw new IllegalArgumentException("Club not found: " + clubName);
	    }

	    club.setCoordinatorName(coordinatorName);
	    club.setDescription(description);

	    if (image != null && !image.isEmpty()) {
	        try {
	            byte[] imageBytes = image.getBytes();
	            club.setImage(imageBytes);
	        } catch (IOException e) {
	            throw new RuntimeException("Error saving image", e);
	        }
	    }

	    return repo4.save(club); // Save updated club to the database.
	}

	
	
}
