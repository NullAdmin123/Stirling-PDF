package stirling.software.SPDF.controller.web;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import stirling.software.SPDF.config.security.UserService;
import stirling.software.SPDF.model.User;
import stirling.software.SPDF.repository.UserRepository;
@Controller
@Tag(name = "Account Security", description = "Account Security APIs")
public class AccountWebController {
	

	@GetMapping("/login")
	public String login(HttpServletRequest request, Model model, Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
		
		if (request.getParameter("error") != null) {

	        model.addAttribute("error", request.getParameter("error"));
	    }
	    if (request.getParameter("logout") != null) {

	        model.addAttribute("logoutMessage", "You have been logged out.");
	    }
	    
	    return "login";
	}
	@Autowired
	private UserRepository userRepository;  // Assuming you have a repository for user operations

	@Autowired
	private UserService userService;  // Assuming you have a repository for user operations

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/addUsers")
	public String showAddUserForm(Model model) {
	    List<User> allUsers = userRepository.findAll();
	    model.addAttribute("users", allUsers);
	    return "addUsers";
	}

	
	
	@GetMapping("/account")
	public String account(HttpServletRequest request, Model model, Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }
		if (authentication != null && authentication.isAuthenticated()) {
	        Object principal = authentication.getPrincipal();

	        if (principal instanceof UserDetails) {
	            // Cast the principal object to UserDetails
	            UserDetails userDetails = (UserDetails) principal;

	            // Retrieve username and other attributes
	            String username = userDetails.getUsername();

	            // Fetch user details from the database
	            Optional<User> user = userRepository.findByUsername(username);  // Assuming findByUsername method exists
	            if (!user.isPresent()) {
	                // Handle error appropriately
	                return "redirect:/error";  // Example redirection in case of error
	            }

	            // Convert settings map to JSON string
	            ObjectMapper objectMapper = new ObjectMapper();
	            String settingsJson;
	            try {
	                settingsJson = objectMapper.writeValueAsString(user.get().getSettings());
	            } catch (JsonProcessingException e) {
	                // Handle JSON conversion error
	                e.printStackTrace();
	                return "redirect:/error";  // Example redirection in case of error
	            }

	            // Add attributes to the model
	            model.addAttribute("username", username);
	            model.addAttribute("role", user.get().getRolesAsString());
	            model.addAttribute("settings", settingsJson);
	        }
		} else {
	        	return "redirect:/";
	        }
	    return "account";
	}
	 
	
	
	
}
