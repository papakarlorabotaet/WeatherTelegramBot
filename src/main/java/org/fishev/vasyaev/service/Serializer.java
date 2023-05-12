package org.fishev.vasyaev.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Import(ObjectMapper.class)
public class Serializer {

    ObjectMapper objectMapper;

    public Serializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void serialize(Map<Long, User> users) {
        try {
            objectMapper.writeValue(new File("Users.json"), users);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public Map<Long, User> deserialize() {
        Map<Long, User> users = null;
        TypeReference<HashMap<Long, User>> typeRef = new TypeReference<>() {};
        try {
            users = objectMapper.readValue(new File("Users.json"), typeRef);
        } catch (IOException e) {
            log.error(e.toString());
        }
        return users;
    }

}
