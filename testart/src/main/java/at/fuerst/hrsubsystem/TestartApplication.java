package at.fuerst.hrsubsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = EmployeeRepository.class)
public class TestartApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestartApplication.class, args);
    }

}
