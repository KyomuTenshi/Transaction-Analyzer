package com.piperitegames.finance.data.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    REGULAR("Regular"),
    TAXABLE("Taxable"),
    RECURRENT("Recurrent"),
    FOREIGN_CURRENCY("ForeignCurrency"),
    COMMENTABLE("Commentable");

    private final String code;

    public static Optional<TransactionType> fromCode(String code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equals(code))
                .findFirst();
    }
}