package org.simpletaxi.internalauthstarter.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class PathProperties {
    private List<String> publicPaths = new ArrayList<>();

    @PostConstruct
    public void logPaths() {
        System.out.println("Loaded public paths from YAML: " + publicPaths);
    }
}
