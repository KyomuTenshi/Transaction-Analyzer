package com.piperitegames.finance.data.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    CURRENT(0, "текущий"),
    SAVINGS(1, "сберегательный"),
    CREDIT(2, "кредитный");

    private final int code;
    private final String title;

    public static Optional<AccountType> fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst();
    }
}