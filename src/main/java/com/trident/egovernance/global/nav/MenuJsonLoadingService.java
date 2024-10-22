package com.trident.egovernance.global.nav;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class MenuJsonLoadingService {
    private final ObjectMapper objectMapper;

    public MenuJsonLoadingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Cacheable("jsonData")
    public MenuNode loadJson() throws IOException {
        File file = new File("src/main/resources/main.json"); // Replace with the path to your JSON file
        return objectMapper.readValue(file, MenuNode.class);
    }
}
