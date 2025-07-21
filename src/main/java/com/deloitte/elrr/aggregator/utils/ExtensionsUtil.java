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
     * @return Map<URI, Object>
     */
    public Map<URI, Object> getExtensions(Context context) {

        Map<URI, Object> extensionsMap = new HashMap<>();

        if (context != null) {

            Extensions extensions = context.getExtensions();

            if (extensions != null) {
                extensionsMap = extensions.getMap();
            }

        }

        return extensionsMap;
    }

    /**
     * @param context
     * @return expires
     */
    public LocalDateTime getExpired(Context context) {

        LocalDateTime expires = null;

        if (context != null) {

            Extensions extensions = context.getExtensions();

            if (extensions != null) {

                String strExpires = (String) extensions.get(
                        ExtensionsConstants.CONTEXT_EXTENSIONS_EXPIRES);

                if (strExpires != null) {
                    expires = LocalDateTime.parse(strExpires,
                            DateTimeFormatter.ISO_DATE_TIME);
                }

            }

        }

        return expires;

    }
}
