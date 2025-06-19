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
    private ArrayList<String> languageCodes = new ArrayList<String>();

    /**
     * Constructor.
     */
    public LangMapUtil() {
        this.languageCodes.add("en-us");
    }

    /**
     * @param langMap
     * @return langCode
     * @throws AggregatorException
     */
    public String getLangMapValue(LangMap langMap) {

        LangTag langCode = null;
        Set<LangTag> langCodes = langMap.getKeys();

        try {

            Iterator<LangTag> langCodesIterator = langCodes.iterator();

            // Iterate and compare to lang.codes in .properties
            while (langCodesIterator.hasNext()) {

                LangTag code = langCodesIterator.next();

                if (languageCodes.contains(code.toString().toLowerCase())) {
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

                LangTag firstElement = langCodes.stream().findFirst().orElse(
                        null);
                langCode = firstElement;
            }

            return langMap.get(langCode);

        } catch (ClassCastException | NullPointerException e) {
            throw new AggregatorException("Error getting language codes", e);
        }
    }
}
