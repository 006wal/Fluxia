package Fluxia.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// On dit à Spring de regarder PARTOUT (dans com.fluxia et Fluxia.demo)
@ComponentScan(basePackages = {"com.fluxia", "Fluxia.demo"})
@EntityScan(basePackages = {"com.fluxia.model"})
@EnableJpaRepositories(basePackages = {"com.fluxia.repository"})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}