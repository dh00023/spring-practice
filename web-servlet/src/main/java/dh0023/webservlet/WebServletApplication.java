package dh0023.webservlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan // servlet 자동 등록
@SpringBootApplication
public class WebServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebServletApplication.class, args);
	}

}
