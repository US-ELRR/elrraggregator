package com.deloitte.elrr.aggregator.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.yetanalytics.xapi.model.LangMap;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LangMapUtil {

  @Value("${lang.codes}")
  ArrayList<String> languageCodes = new ArrayList<String>();

  /**
   * @param map
   * @return langCode
   * @throws AggregatorException
   */
  public String getLangMapValue(LangMap map) {

    String langCode = null;
    Set<String> langCodes = map.getLanguageCodes();

    try {

      Iterator<String> langCodesIterator = langCodes.iterator();

      // Iterate and compare to lang.codes in .properties
      while (langCodesIterator.hasNext()) {

        String code = langCodesIterator.next();
        log.info("===> langCode = " + code);

        if (languageCodes.contains(code)) {
          langCode = code;
          break;
        }
      }

      // If langCode not found
      if (langCode == null || langCode.length() == 0) {
        // Check for en-us
        if (langCodes.contains("en-us")) {
          langCode = "en-us";
          // Check for en
        } else if (langCodes.contains("en")) {
          langCode = "en";
          // Get 1st element
        } else {
          String firstElement = langCodes.stream().findFirst().orElse(null);
          langCode = firstElement;
        }
      }

      return langCode;

    } catch (ClassCastException | NullPointerException e) {
      log.error("Error getting language codes - " + e.getMessage());
      e.printStackTrace();
      throw new AggregatorException("Error getting language code - " + e.getMessage());
    }
  }
}
