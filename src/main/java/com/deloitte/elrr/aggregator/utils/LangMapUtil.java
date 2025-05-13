package com.deloitte.elrr.aggregator.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.yetanalytics.xapi.model.LangMap;
import com.yetanalytics.xapi.model.LangTag;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LangMapUtil {

    @Value("${lang.codes}")
    ArrayList<String> languageCodes = new ArrayList<String>();

    public LangMapUtil() {
        this.languageCodes.add("en-us");
    }

    /**
     * @param map
     * @return langCode
     * @throws AggregatorException
     */
    public String getLangMapValue(LangMap map) {

        LangTag langCode = null;
        Set<LangTag> langCodes = map.getKeys();

        try {

            Iterator<LangTag> langCodesIterator = langCodes.iterator();

            // Iterate and compare to lang.codes in .properties
            while (langCodesIterator.hasNext()) {

                LangTag code = langCodesIterator.next();

                if (languageCodes.contains(code)) {
                    langCode = code;
                    break;
                }
            }

            // If langCode not found
            if (langCode == null) {

                LangTag enUS = new LangTag("en-us");
                LangTag en = new LangTag("en");

                // Check for en-us
                if (langCodes.contains(enUS)) {

                    langCode = enUS;

                    // Check for begins with en
                } else if (langCodes.contains(en)) {

                    // Reset pointer
                    langCodesIterator = langCodes.iterator();

                    // Iterate and get 1st en*
                    while (langCodesIterator.hasNext()) {

                        LangTag code = langCodesIterator.next();

                        if (code.toString().startsWith("en")) {
                            langCode = code;
                            break;
                        }
                    }
                }
            }

            // Get 1st element
            if (langCode == null) {

                LangTag firstElement = langCodes.stream().findFirst().orElse(null);
                langCode = firstElement;
            }

            return map.get(langCode);

        } catch (ClassCastException | NullPointerException e) {
            log.error("Error getting language codes", e);
            e.printStackTrace();
            throw new AggregatorException("Error getting language codes", e);
        }
    }
}
