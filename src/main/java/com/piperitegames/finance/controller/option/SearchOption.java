package com.piperitegames.finance.controller.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchOption implements MenuOption {
    SAVE_AND_BACK(0, "сохранить поиск и вернуться назад"),
    RESET(1, "выбрать все транзакции (сбросит все заданные ранее фильтры)"),
    BY_CATEGORY(2, "искать по категориям"),
    BY_DATE_RANGE(3, "искать по диапазону дат"),
    BY_AMOUNT_RANGE(4, "искать по диапазону суммы транзакций"),
    BY_COMMENT(5, "искать по комментарию (для транзакций, поддерживающих комментарии)");

    private final int code;
    private final String text;
}