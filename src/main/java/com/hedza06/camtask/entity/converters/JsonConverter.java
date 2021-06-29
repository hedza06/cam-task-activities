package com.hedza06.camtask.entity.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JsonConverter implements AttributeConverter<Map<String, Object>, String> {

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToEntityAttribute(String attribute)
    {
        if (attribute == null) {
            return new HashMap<>();
        }
        try
        {
            byte[] attributeBytes = attribute.getBytes(StandardCharsets.ISO_8859_1);
            String encodedAttribute = new String(attributeBytes);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(encodedAttribute, HashMap.class);
        }
        catch (IOException e) {
            log.info("IO Error: {}", e.getMessage());
        }
        return new HashMap<>();
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> dbData)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(dbData);
        }
        catch (JsonProcessingException e) {
            return null;
        }
    }

}
