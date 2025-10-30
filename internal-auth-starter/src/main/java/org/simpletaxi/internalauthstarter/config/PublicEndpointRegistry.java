package org.simpletaxi.internalauthstarter.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.simpletaxi.internalauthstarter.security.PublicEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.result.method.RequestMappingInfoHandlerMapping;

import java.util.HashSet;
import java.util.Set;

@Component
@Getter
public class PublicEndpointRegistry {

    private final ApplicationContext applicationContext;
    private final AntPathMatcher matcher;
    private final Set<String> publicPaths = new HashSet<>();

    public PublicEndpointRegistry(ApplicationContext applicationContext,
                                  PathProperties pathProperties, AntPathMatcher matcher) {
        this.applicationContext = applicationContext;
        this.matcher = matcher;
        this.publicPaths.addAll(pathProperties.getPublicPaths());
    }

    @PostConstruct
    public void init() {
        String[] beans = applicationContext.getBeanNamesForType(RequestMappingInfoHandlerMapping.class);
        for (String beanName : beans) {
            RequestMappingInfoHandlerMapping mapping =
                    applicationContext.getBean(beanName, RequestMappingInfoHandlerMapping.class);

            mapping.getHandlerMethods().forEach((info, method) -> {
                boolean isPublic = AnnotatedElementUtils.hasAnnotation(method.getMethod(), PublicEndpoint.class)
                        || AnnotatedElementUtils.hasAnnotation(method.getBeanType(), PublicEndpoint.class);
                if (isPublic) {
                    info.getPatternsCondition().getPatterns()
                            .forEach(pattern -> publicPaths.add(pattern.getPatternString()));
                }
            });
        }
    }

    public boolean isPublic(String path) {
        return publicPaths.stream().anyMatch(p -> matcher.match(p, path));
    }
}
