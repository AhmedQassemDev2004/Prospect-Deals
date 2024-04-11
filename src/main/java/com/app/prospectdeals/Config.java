package com.app.prospectdeals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class Config {
    public String OUTPUT_PATH = ""; // Add your configuration variables here

    Config() {
        File configFile = new File("./config/config.json");

        if (configFile.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode rootNode = objectMapper.readTree(configFile);

                OUTPUT_PATH = rootNode.get("OUTPUT_PATH").asText();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public void save() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode rootNode = objectMapper.createObjectNode();

            rootNode.put("OUTPUT_PATH", OUTPUT_PATH);

            File configFile = new File("./config/config.json");

            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }

            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            objectMapper.writeValue(configFile, rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
