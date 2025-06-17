package com.deloitte.elrr.aggregator.util;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

public final class TestFileUtil {

    /**
     * @param filename
     * @return file
     * @throws IOException
     */
    public static File getJsonTestFile(String filename) throws IOException {
        return new ClassPathResource(filename).getFile();
    }

    private TestFileUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
