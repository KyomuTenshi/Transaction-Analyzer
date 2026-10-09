package com.piperitegames.finance.controller.option;

import com.piperitegames.finance.service.analytics.GroupOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupMenuOption implements MenuOption {
    BACK(0, "вернуться назад (без группировки)", GroupOption.NONE),
    BY_MONTH(1, "группировать по месяцам", GroupOption.MONTH),
    BY_YEAR(2, "группировать по годам", GroupOption.YEAR),
    BY_DAY_OF_WEEK(3, "группировать по дню недели", GroupOption.DAY_OF_WEEK),
    BY_CATEGORY(4, "группировать по категории", GroupOption.CATEGORY),
    INCOME_EXPENSE(5, "считать доходы и расходы", GroupOption.INCOME_EXPENSE),
    BY_ACCOUNT_TYPE(6, "группировать по типу счёта", GroupOption.ACCOUNT_TYPE),
    BY_USER_ID(7, "группировать по ID пользователя", GroupOption.USER_ID);

    private final int code;
    private final String text;
    private final GroupOption groupOption;
}