package davidepan.capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CapstoneApplication {
	public static void main(String[] args) {

		System.out.println("=== AUTO-UPDATE TEST: nuova versione attiva ===");

		SpringApplication.run(CapstoneApplication.class, args);
	}
}
