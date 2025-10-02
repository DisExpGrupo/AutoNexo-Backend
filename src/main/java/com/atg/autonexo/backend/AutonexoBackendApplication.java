package com.atg.autonexo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AutonexoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutonexoBackendApplication.class, args);
    }

}
