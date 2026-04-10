package com.example.erroranalysisemail.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ControllerHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(ControllerHelper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String generateJsonFromObj(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("Error converting object to JSON", e);
            return "{\"error\":\"Failed to generate JSON response\"}";
        }
    }
}
