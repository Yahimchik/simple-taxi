package org.simpletaxi.internalauthstarter.service;

import java.util.Map;

public interface InternalJwtService {

    String generateInternalToken(Map<String, Object> claims);

    Map<String, Object> validateInternalToken(String token);
}
