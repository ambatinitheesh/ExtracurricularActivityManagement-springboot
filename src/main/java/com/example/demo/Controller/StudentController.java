package com.example.demo.Controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Model.Admin;
import com.example.demo.Model.Club;
import com.example.demo.Model.Complaint;
import com.example.demo.Model.Event;
import com.example.demo.Model.Registration;
import com.example.demo.Model.Student;
import com.example.demo.Service.AdminDAO;
import com.example.demo.Service.DAO;
import com.example.demo.Service.EmailService;

@RestController
@CrossOrigin
public class StudentController {
	
	@Autowired
	DAO dao;
	
	@Autowired
	AdminDAO admindao;
	@Autowired
    private EmailService emailService;

    private Map<String, String> otpStore = new HashMap<>();
    
    // Endpoint to save a new complaint
    @PostMapping("/addcomplaint")
    public ResponseEntity<Complaint> saveComplaint(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String complaint,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Complaint newComplaint = new Complaint();
            newComplaint.setEmail(email);
            newComplaint.setName(name);
            newComplaint.setComplaint(complaint);
            if (file != null) {
                newComplaint.setFile(file.getBytes());
            }
            Complaint savedComplaint = dao.saveComplaint(newComplaint);
            return ResponseEntity.ok(savedComplaint);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Event>> getEventsByCategory(@PathVariable String category) {
        List<Event> events = dao.findByCategory(category);
        return ResponseEntity.ok(events);
    }
    
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // Check if the student already exists
        Student existingStudent = dao.findStudent(email);
        if (existingStudent != null) {
            // If the student exists, return an error message
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email already exists"));
        }

        // Generate a six-digit OTP
        int otp = 100000 + new Random().nextInt(900000); // Ensures a 6-digit OTP between 100000 and 999999

        otpStore.put(email, String.valueOf(otp)); // Store OTP as a string
        emailService.sendEmail(email, "OTP Verification", "Your OTP is: " + otp);
        return ResponseEntity.ok(Collections.singletonMap("otp", String.valueOf(otp)));
    }


	//api to add Students
    @PostMapping("/add")
    public Student addstud(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return dao.saveStudent(firstname, lastname, email, password, image);
    }
    
    @PostMapping("/addstudent")
    public ResponseEntity<?> addStuden(@RequestBody Student student)
    {
    	dao.addStudent(student);
    	 return ResponseEntity.ok("Student created successfully");
    }
    @PostMapping("/addevent")
    public Event addEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam("coordinatorName") String coordinatorName,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("capacity") int capacity,
            @RequestParam("venue") String venue,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("clubName")String clubName,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return dao.saveEvent(eventName, coordinatorName, date, time, capacity, venue, description, category,clubName, image);
    }
    @PostMapping("/update-event")
    public Event updateEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam("coordinatorName") String coordinatorName,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("capacity") int capacity,
            @RequestParam("venue") String venue,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("clubName") String clubName,
            @RequestParam(value = "image", required = false) MultipartFile image) {
    	return dao.updateEvent(eventName, coordinatorName, date, time, capacity, venue, description, category, clubName, image);
    }
    
	@DeleteMapping("/delete-event")
	public String deleteEvent(@RequestParam("eventName")String eventName)
	{
		dao.deleteEvent(eventName);
		return "Deleted Successfully";
	}
	
    @PostMapping("/addclub")
    public Club addClub(@RequestParam("clubname")String clubname,@RequestParam("coordinatorname")String coordinatorname,
    					@RequestParam("description")String description,@RequestParam(value = "image", required = false) MultipartFile image) {
    	return dao.saveclub(clubname, coordinatorname, description, image);
    }
    
    @GetMapping("/viewclubs")
    public List<Club> viewclubs()
    {
    	return dao.viewclubs();
    }
    
    @PostMapping("/editclub")
    public Club editClub(
        @RequestParam("clubname") String clubName,
        @RequestParam("coordinatorname") String coordinatorName,
        @RequestParam("description") String description,
        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        return dao.updateClubDetails(clubName, coordinatorName, description, image);
    }

    
    
    @GetMapping("/names")
    public List<String>getClubNames()
    {
    	return dao.getClubNames();
    }
    
    @GetMapping("view-club")
    public Club viewClub(@RequestParam("clubName")String clubName)
    {
    	return dao.findClub(clubName);
    }
	@GetMapping("/viewall")
	public List<Student> viewall()
	{
		return dao.getall();
	}
	
	
	@GetMapping("/studentpage")
	public Map<String, Object> getStudentsPage(@RequestParam("page") int page, @RequestParam("limit") int limit) {
	    List<Student> students = dao.studentpages(page, limit);
	    long total = dao.countStudents();  // Assuming you have a method to count the total students
	    Map<String, Object> response = new HashMap<>();
	    response.put("students", students);
	    response.put("total", total);
	    return response;
	}

	@GetMapping("/eventspage")
	public List<Event> fun8(@RequestParam("page")int page,@RequestParam("limit")int limit)
	{
		
		return dao.eventpages(page, limit);
	}
	
		
	@GetMapping("/profile")
	public ResponseEntity<Map<String, Object>> findUser(@RequestParam("email") String email) {
	    Map<String, Object> response = new HashMap<>();
	
	    // Try to find a student first
	    Student student = dao.findStudent(email);
	    if (student != null) {
	        response.put("role", student.getRole());
	        response.put("firstname", student.getFirstname());
	        response.put("lastname", student.getLastname());
	        response.put("email", student.getEmail());
	        
	        // Convert image byte array to base64 string
	        if (student.getImage() != null) {
	            String base64Image = Base64.getEncoder().encodeToString(student.getImage());
	            response.put("image", base64Image);
	        } else {
	            response.put("image", null); // No image available
	        }
	        
	        return ResponseEntity.ok(response);
	    }
	    

    // Try to find an admin if student is not found
    Admin admin = admindao.findAdmin(email);
    if (admin != null) {
        response.put("role", admin.getRole());
        response.put("name", admin.getName());
        response.put("email", admin.getEmail());
        
        // Convert image byte array to base64 string if Admin has an image
        if (admin.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(admin.getImage());
            response.put("image", base64Image);
        } else {
            response.put("image", null); // No image available
        }
        
        return ResponseEntity.ok(response);
    }

    // Return a 404 Not Found if neither student nor admin is found
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
}

	@GetMapping("/studentprofile")
	public ResponseEntity<Map<String, Object>> getStudentProfile(@RequestParam String email) {
	    Student student = dao.findStudent(email); // Adjust this to match your repository method
	    if (student != null) {
	        Map<String, Object> response = new HashMap<>();
	        response.put("firstname", student.getFirstname());
	        response.put("lastname", student.getLastname());
	        response.put("email", student.getEmail());
	        response.put("role", student.getRole());

	        // Convert image byte array to base64 and send it as a string
	        if (student.getImage() != null) {
	            String base64Image = Base64.getEncoder().encodeToString(student.getImage());
	            response.put("image", base64Image);
	        } else {
	            response.put("image", null); // No image available
	        }

	        return ResponseEntity.ok(response);
	    }
	    return ResponseEntity.notFound().build();
	}

	@PutMapping("/update")
	public Student update(@RequestBody Student student)
	{
		dao.updateStudent(student);
		return student;
	}
	
	@DeleteMapping("/delete")
	public String delete(@RequestParam("email") String email) {
		dao.deleteStudent(email);
		return "Deleted Sucessfully";
	}
	
	@PostMapping("/login")
	public Object login(@RequestBody Map<String, String> credentials) {
	    String email = credentials.get("email");
	    String password = credentials.get("password");

	    Student st = dao.findStudent(email);
	    Admin at = admindao.findAdmin(email);

	    if (st != null && st.getPassword().equals(password)) {
	        return st;
	    } else if (at != null && at.getPassword().equals(password)) {
	        return at;
	    } else {
	        return null; // or handle invalid credentials appropriately
	    }
	}
	
	@GetMapping("/events")
	public List<Event>viewallEvents()
	{
		return dao.getallevents();
		
	}
	
	@GetMapping("/viewevent")
	public Event findEvent(@RequestParam("eventname") String eventname)
	{
		return dao.findEvent(eventname);
	}
	

	
	
	@PostMapping("/register")
	public String registration(@RequestParam("email")String email,@RequestParam("eventname")String eventname)
	{
		dao.registerEvent(eventname, email);
		return "Registered Successfully";
	}
	
	@GetMapping("/check-registration")
	public boolean checkregistration(@RequestParam("email")String email,@RequestParam("eventname")String eventname) {
		boolean b=dao.checkregistration(email, eventname);
		return b;
	}
	
		@DeleteMapping("/unregister")
		public String Unregister(@RequestParam("email")String email,@RequestParam("eventname")String eventname)
		{
			dao.unregister(email, eventname);
			return "Cancelled Registration Successfully";
		}
	
	@GetMapping("/registered-events")
	public List<Event> getRegisteredEvents(@RequestParam("email") String email) {
	    return dao.getEventsByStudentEmail(email);
	}

	@GetMapping("/all-registrations")
	public List<Registration> allregistrations()
	{
		return dao.getAllRegistrations();
	}
	

	 @GetMapping("/students-by-event")
	    public List<Student> getStudentsByEventName(@RequestParam("eventName") String eventName) {
	        return dao.getStudentsByEventName(eventName);
	    }
	 
	@GetMapping("/find-student")
	public Student findStudent(@RequestParam("email") String email)
	{
		return dao.findStudent(email);
	}
}
