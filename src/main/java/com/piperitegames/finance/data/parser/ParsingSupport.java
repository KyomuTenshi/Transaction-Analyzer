package com.piperitegames.finance.data.parser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

final class ParsingSupport {

    private ParsingSupport() {
    }

    static long parseLong(String value, String fieldName) {
        try {
            return Long.parseLong(value.strip());
        } catch (NumberFormatException e) {
            throw invalidValue(value, fieldName, e);
        }
    }

    static int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.strip());
        } catch (NumberFormatException e) {
            throw invalidValue(value, fieldName, e);
        }
    }

    static BigDecimal parseDecimal(String value, String fieldName) {
        try {
            return new BigDecimal(value.strip());
        } catch (NumberFormatException e) {
            throw invalidValue(value, fieldName, e);
        }
    }

    static LocalDateTime parseDateTime(String value, String fieldName) {
        try {
            return LocalDateTime.parse(value.strip(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw invalidValue(value, fieldName, e);
        }
    }

    static String requireNotBlank(String value, String fieldName) {
        String stripped = value.strip();
        if (stripped.isEmpty()) {
            throw new IllegalArgumentException("поле «%s» не может быть пустым".formatted(fieldName));
        }
        return stripped;
    }

    private static IllegalArgumentException invalidValue(String value, String fieldName, Exception cause) {
        return new IllegalArgumentException(
                "некорректное значение поля «%s»: '%s'".formatted(fieldName, value.strip()), cause);
    }
}