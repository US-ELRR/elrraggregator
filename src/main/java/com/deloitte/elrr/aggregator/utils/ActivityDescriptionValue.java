package com.deloitte.elrr.aggregator.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.yetanalytics.xapi.model.ActivityDefinition;
import com.yetanalytics.xapi.model.LangMap;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ActivityDescriptionValue {

  @Value("${lang.codes}")
  ArrayList<String> languageCodes = new ArrayList<String>();

  /**
   * @param map
   * @param activityDefinition
   * @param pred
   * @return activityDEscription
   * @throws AggregatorException
   */
  public String getActivityDescription(
      LangMap map, ActivityDefinition activityDefinition, String pref) {

    String langCode = null;
    Set<String> langCodes = map.getLanguageCodes();

    try {

      Iterator<String> langCodesIterator = langCodes.iterator();

      // Iterate and compare to lang.codes in .properties
      while (langCodesIterator.hasNext()) {

        String code = langCodesIterator.next();

        boolean found = Arrays.asList(languageCodes).contains(code);

        if (found) {
          langCode = code;
          break;
        }
      }

      // If langCode not found
      if (langCode == null || langCode.length() == 0) {
        // Check for en-us then en
        if (langCodes.contains("en-us")) {
          langCode = "en-us";
        } else if (langCodes.contains("en")) {
          langCode = "en";
        } else {
          String firstElement = langCodes.stream().findFirst().orElse(null);
          langCode = firstElement;
        }
      }

      // Get activity definition
      if (pref.equalsIgnoreCase("name")) {
        return activityDefinition.getName().get(langCode);
      } else {
        return activityDefinition.getDescription().get(langCode);
      }

    } catch (ClassCastException | NullPointerException e) {
      log.error("Error getting language codes - " + e.getMessage());
      e.printStackTrace();
      throw new AggregatorException("Error getting language codes - " + e.getMessage());
    }
  }
}
