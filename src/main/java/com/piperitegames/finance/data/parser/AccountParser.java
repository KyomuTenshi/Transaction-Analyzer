package com.piperitegames.finance.data.parser;

import com.piperitegames.finance.data.model.Account;
import com.piperitegames.finance.data.model.AccountType;

public class AccountParser implements LineParser<Account> {

    private static final String FIELD_SEPARATOR = ",";
    private static final int FIELD_COUNT = 3;

    @Override
    public Account parse(String line) {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "ожидается %d поля, получено %d".formatted(FIELD_COUNT, fields.length));
        }

        long id = ParsingSupport.parseLong(fields[0], "ID счёта");
        int typeCode = ParsingSupport.parseInt(fields[1], "тип счёта");
        AccountType type = AccountType.fromCode(typeCode)
                .orElseThrow(() -> new IllegalArgumentException("неизвестный тип счёта: " + typeCode));
        long userId = ParsingSupport.parseLong(fields[2], "ID пользователя");

        return new Account(id, type, userId);
    }
}