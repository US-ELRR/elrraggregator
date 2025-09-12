package com.deloitte.elrr.aggregator.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({"PMD", "checkstyle:hideutilityclassconstructor"})
public final class PrettyJson {

    /**
     * @param json
     * @return prettyJson
     */
    public static String prettyJson(String json) {

        String prettyJson = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonObject = objectMapper.readValue(json, Object.class);
            prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            return json;
        }

        return prettyJson;

    }

}
