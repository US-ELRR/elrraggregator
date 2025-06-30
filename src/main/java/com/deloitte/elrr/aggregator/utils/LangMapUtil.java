package com.deloitte.elrr.aggregator.utils;

import java.util.ArrayList;
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
    private ArrayList<String> prefLangCodes = new ArrayList<String>();

    /**
     * Constructor.
     */
    public LangMapUtil() {
        this.prefLangCodes.add("en-us");
    }

    /**
     * Given a set of LangTags, find the first one that matches one of the
     * prefLangCodes.
     *
     * @param tags
     * @return matching LangTag
     */
    public LangTag getMatchingKey(Set<LangTag> tags) {
        for (LangTag tag : new ArrayList<LangTag>(tags)) {
            if (prefLangCodes.contains(tag.toString().toLowerCase())) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Given a set of LangTags and a string prefix, return the first one that
     * matches the prefix.
     *
     * @param tags
     * @param prefix
     * @return Matching LangTag
     */
    public LangTag getCodeWithPrefix(Set<LangTag> tags, String prefix) {
        for (LangTag tag : new ArrayList<LangTag>(tags)) {
            if (tag.toString().startsWith("en")) {
                return tag;
            }
        }
        return null;
    }

    /**
     * @param langMap
     * @return langCode
     * @throws AggregatorException
     */
    public String getLangMapValue(LangMap langMap) {

        try {

            Set<LangTag> langCodes = langMap.getKeys();

            // Iterate and compare to lang.codes in .properties
            LangTag langCode = getMatchingKey(langMap.getKeys());

            // If langCode not found
            if (langCode == null) {

                LangTag enUS = new LangTag("en-us");

                // Check for en-us
                if (langCodes.contains(enUS)) {
                    langCode = enUS;
                } else {
                    langCode = getCodeWithPrefix(langMap.getKeys(), "en");
                }
            }

            // Get 1st element
            if (langCode == null) {
                langCode = langCodes.stream().findFirst().orElse(null);
            }

            return langMap.get(langCode);

        } catch (ClassCastException | NullPointerException e) {
            throw new AggregatorException("Error getting language codes", e);
        }
    }
}
