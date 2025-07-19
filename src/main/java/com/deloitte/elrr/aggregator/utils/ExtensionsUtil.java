package com.deloitte.elrr.aggregator.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

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

        Extensions extensions = null;
        Map<URI, Object> extensionsMap = new HashMap<>();

        if (context != null) {

            extensions = context.getExtensions();

            if (extensions != null) {
                extensionsMap = extensions.getMap();
            }

        }

        return extensionsMap;
    }

}
