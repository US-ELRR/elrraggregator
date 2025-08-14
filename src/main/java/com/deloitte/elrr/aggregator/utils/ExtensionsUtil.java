package com.deloitte.elrr.aggregator.utils;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.deloitte.elrr.aggregator.rules.ExtensionsConstants;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.types.GoalType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanalytics.xapi.model.AbstractActor;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Context;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExtensionsUtil {

    /**
     * @param <T>
     * @param context
     * @param key
     * @param returnObject
     * @return T
     * @throws AggregatorException
     */
    public <T> T getExtensionValue(Context context, String key,
            Class<T> returnObject) throws AggregatorException {

        Map<URI, Object> extensionsMap = new HashMap<>();

        try {

            if (context == null || context.getExtensions() == null) {
                return null;
            }

            // LocalDateTime
            if (key.equalsIgnoreCase(ExtensionsConstants.EXT_EXPIRES) || (key
                    .equalsIgnoreCase(
                            ExtensionsConstants.ACTIVITY_EXT_EXPIRES))) {

                String strLocalDateTime = (String) context.getExtensions().get(
                        key);

                if (strLocalDateTime != null) {
                    return (T) LocalDateTime.parse(strLocalDateTime,
                            DateTimeFormatter.ISO_DATE_TIME);
                }

                // Actor
            } else if (key.equalsIgnoreCase(ExtensionsConstants.EXT_BY) || (key
                    .equalsIgnoreCase(ExtensionsConstants.EXT_TO))) {

                ObjectMapper mapper = Mapper.getMapper();

                // Get extensions
                extensionsMap = context.getExtensions().getMap();

                for (Map.Entry<URI, Object> entry : extensionsMap.entrySet()) {

                    String json = mapper.writeValueAsString(entry.getValue());
                    AbstractActor actor = mapper.readValue(json,
                            AbstractActor.class);
                    if (actor != null) {
                        return (T) actor;
                    }

                }

            } else {

                return null;

            }

        } catch (IOException e) {
            throw new AggregatorException("Error ", e);
        }

        return null;
    }

    /**
     * @param activity
     * @param extensionsConstant
     * @return LocatDateTime
     * @throws IllegalArgumentException
     * @throws DateTimeParseException
     */
    public LocalDateTime getExtensionsDate(Activity activity,
            String extensionsConstant) throws IllegalArgumentException,
            DateTimeParseException {

        String strDate = (String) activity.getDefinition().getExtensions().get(
                extensionsConstant);

        if (strDate != null) {
            return LocalDateTime.parse(strDate,
                    DateTimeFormatter.ISO_DATE_TIME);
        }

        return null;

    }

    /**
     * @param type
     * @return GoalType
     */
    public GoalType getGoalType(String type) {

        if (type.toString().equalsIgnoreCase("ASSIGNED")) {
            return GoalType.ASSIGNED;
        } else if (type.toString().equalsIgnoreCase("SELF-ASSIGNED")) {
            return GoalType.SELF;
        } else if (type.toString().equalsIgnoreCase("RECOMMENDED")) {
            return GoalType.RECOMMENDED;
        } else {
            return GoalType.ASSIGNED;
        }

    }

}
