package com.telegram.reporting;

import org.apache.commons.lang3.StringUtils;

public class ExceptionHelper {
    public static String cannotFindExpectedErrorMessage(String expectedMessage) {
        var errorMessage = "Can't find expected error message: ";
        if (StringUtils.isBlank(expectedMessage)) {
            return errorMessage + "EXPECTED MESSAGE DIDN'T DEFINE!";
        }
        return errorMessage + expectedMessage;
    }
}
