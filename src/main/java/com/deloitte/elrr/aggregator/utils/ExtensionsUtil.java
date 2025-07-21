package com.deloitte.elrr.aggregator.utils;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.deloitte.elrr.aggregator.rules.ExtensionsConstants;
import com.yetanalytics.xapi.model.Context;
import com.yetanalytics.xapi.model.Extensions;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExtensionsUtil {

    /**
     * @param context
     * @param returnObject
     * @return extensionsMap or LocalDateTime
     */
    public Object getExtensions(Context context, String returnObject) {

        Map<URI, Object> extensionsMap = new HashMap<>();

        if (context != null) {

            Extensions extensions = context.getExtensions();

            if (extensions != null) {

                if (returnObject.equalsIgnoreCase("Map")) {

                    extensionsMap = extensions.getMap();
                    return extensionsMap;

                } else if (returnObject.equalsIgnoreCase("LocalDateTime")) {

                    String strLocalDateTime = (String) extensions.get(
                            ExtensionsConstants.CONTEXT_EXTENSIONS_EXPIRES);

                    if (strLocalDateTime != null) {
                        return LocalDateTime.parse(strLocalDateTime,
                                DateTimeFormatter.ISO_DATE_TIME);
                    }

                }

            }

        }

        return null;
    }

}
