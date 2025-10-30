package org.simpletaxi.internalauthstarter;

import org.simpletaxi.internalauthstarter.config.PathProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class InternalAuthStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternalAuthStarterApplication.class, args);
    }

}
