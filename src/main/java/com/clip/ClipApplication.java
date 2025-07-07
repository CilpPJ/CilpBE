package com.clip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ClipApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClipApplication.class, args);
    }

}
