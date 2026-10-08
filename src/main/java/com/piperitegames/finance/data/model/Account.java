package com.piperitegames.finance.data.model;

import lombok.NonNull;
import lombok.Value;

@Value
public class Account {
    long id;
    @NonNull AccountType type;
    long userId;
}