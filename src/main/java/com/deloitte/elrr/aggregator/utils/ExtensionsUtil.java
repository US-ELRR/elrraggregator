package com.deloitte.elrr.aggregator.utils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

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

        try {

            if (context == null || context.getExtensions() == null) {
                return null;
            }

            // ZonedDateTime
            if (ZonedDateTime.class.isAssignableFrom(returnObject)) {

                String strLocalDateTime = (String) context.getExtensions().get(
                        key);

                if (strLocalDateTime != null) {
                    return (T) ZonedDateTime.parse(strLocalDateTime,
                            DateTimeFormatter.ISO_DATE_TIME);
                }

                // Actor
            } else if (AbstractActor.class.isAssignableFrom(returnObject)) {

                ObjectMapper mapper = Mapper.getMapper();

                // Get extensions
                String json = mapper.writeValueAsString(context.getExtensions()
                        .get(key));

                return (T) mapper.readValue(json, AbstractActor.class);

            } else {

                throw new UnsupportedOperationException();

            }

        } catch (IOException e) {
            throw new AggregatorException("Error ", e);
        }

        return null;
    }

    /**
     * @param activity
     * @param extensionsConstant
     * @return ZonedDateTime
     * @throws IllegalArgumentException
     * @throws DateTimeParseException
     */
    public ZonedDateTime getExtensionsDate(Activity activity,
            String extensionsConstant) throws IllegalArgumentException,
            DateTimeParseException {

        String strDate = (String) activity.getDefinition().getExtensions().get(
                extensionsConstant);

        if (strDate != null) {
            return ZonedDateTime.parse(strDate,
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
