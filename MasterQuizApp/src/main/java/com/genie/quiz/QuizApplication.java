package com.genie.quiz;

// ADDED: Necessary imports for the new functionality
import com.genie.quiz.entity.User;
import com.genie.quiz.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
@SpringBootApplication
public class QuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizApplication.class, args);
	}

	// ADDED: This entire @Bean method
	/**
	 * This CommandLineRunner bean will run once when the application starts.
	 * It checks if an "admin" user exists. If not, it creates one with a
	 * default password and the "ADMIN" role.
	 */




	@Bean
	public CommandLineRunner createAdminUser(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			@Value("${admin.username}") String adminUsername,
			@Value("${admin.password}") String adminPassword) {

		return args -> {
			if (userRepository.findByUsername(adminUsername).isEmpty()) {
				User admin = new User();
				admin.setUsername(adminUsername);
				admin.setEmail("admin@quizapp.com");
				admin.setPassword(passwordEncoder.encode(adminPassword));
				admin.setRole("ADMIN");
				admin.setProfileImageUrl("/images/default-avatar.png");

				userRepository.save(admin);
				System.out.println(">>> Default ADMIN user ensured <<<");
			}
		};
	}



}