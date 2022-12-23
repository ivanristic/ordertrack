package rs.biljnaapotekasvstefan.ordertrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OrderTrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderTrackApplication.class, args);
	}

}
