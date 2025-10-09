package com.simple.taxi.auth.config.argument_resolver.impl;

import com.simple.taxi.auth.config.argument_resolver.DeviceInfo;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class DeviceInfoResolver implements HandlerMethodArgumentResolver {

    public static final String USER_AGENT = "User-Agent";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(DeviceInfo.class) != null
                && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String userAgent = request.getHeader(USER_AGENT);
        if (userAgent == null || userAgent.isBlank()) {
            userAgent = "Unknown Device";
        }

        return userAgent;
    }
}
