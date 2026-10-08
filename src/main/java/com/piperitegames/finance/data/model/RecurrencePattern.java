package com.piperitegames.finance.data.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public enum RecurrencePattern {
    HOURLY("hourly", LocalDateTime::plusHours),
    DAILY("daily", LocalDateTime::plusDays),
    WEEKLY("weekly", LocalDateTime::plusWeeks),
    BIWEEKLY("biweekly", (start, periods) -> start.plusWeeks(2 * periods)),
    MONTHLY("monthly", LocalDateTime::plusMonths),
    YEARLY("yearly", LocalDateTime::plusYears);

    @Getter
    private final String code;
    private final BiFunction<LocalDateTime, Long, LocalDateTime> shifter;

    public LocalDateTime shift(LocalDateTime start, long periods) {
        return shifter.apply(start, periods);
    }

    public static Optional<RecurrencePattern> fromCode(String code) {
        return Arrays.stream(values())
                .filter(pattern -> pattern.code.equalsIgnoreCase(code))
                .findFirst();
    }
}