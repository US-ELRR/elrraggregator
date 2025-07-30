package com.deloitte.elrr.aggregator.utils;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.deloitte.elrr.aggregator.rules.ExtensionsConstants;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanalytics.xapi.model.AbstractActor;
import com.yetanalytics.xapi.model.Context;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExtensionsUtil {

  /**
   * @param context
   * @param returnObject
   * @return extensionsMap or LocalDateTime
   * @throws AggregatorException
   */
  public Object getExtensions(Context context, String returnObject) {

    Map<URI, Object> extensionsMap = new HashMap<>();

    try {

      // Map
      if (context != null && context.getExtensions() != null
          && returnObject.equalsIgnoreCase("Map")) {

        extensionsMap = context.getExtensions().getMap();
        return extensionsMap;

        // LocalDatewTime
      } else if (context != null && context.getExtensions() != null
          && returnObject.equalsIgnoreCase("LocalDateTime")) {

        String strLocalDateTime = (String) context.getExtensions().get(
            ExtensionsConstants.CONTEXT_EXTENSIONS_EXPIRES);

        if (strLocalDateTime != null) {
          return LocalDateTime.parse(strLocalDateTime,
              DateTimeFormatter.ISO_DATE_TIME);
        }

        // Actor
      } else if (context != null && context.getExtensions() != null
          && returnObject.equalsIgnoreCase("Actor")) {

        ObjectMapper mapper = Mapper.getMapper();

        // Get extensions
        extensionsMap = context.getExtensions().getMap();

        for (Map.Entry<URI, Object> entry : extensionsMap.entrySet()) {

          String json = mapper.writeValueAsString(entry.getValue());
          AbstractActor actor = mapper.readValue(json,
              AbstractActor.class);
          if (actor != null) {
            return actor;
          }

        }

      }

    } catch (IOException e) {
      throw new AggregatorException("Error ", e);
    }

    return null;
  }

}
