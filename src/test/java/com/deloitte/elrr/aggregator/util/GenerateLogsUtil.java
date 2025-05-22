package com.deloitte.elrr.aggregator.util;

import org.slf4j.LoggerFactory;

public class GenerateLogsUtil {
    /**
     * @author phleven
     */
    private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GenerateLogsUtil.class);

    public void generateLogs(String msg) {
        LOGGER.trace(msg);
        LOGGER.debug(msg);
        LOGGER.info(msg);
        LOGGER.warn(msg);
        LOGGER.error(msg);
    }
}