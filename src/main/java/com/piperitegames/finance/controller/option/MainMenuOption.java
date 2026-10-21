package com.piperitegames.finance.controller.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MainMenuOption implements MenuOption {
    EXIT(0, "выход из приложения"),
    SEARCH(1, "задать критерии поиска транзакций"),
    GROUP(2, "выбрать поле для группировки"),
    AGGREGATE(3, "выбрать функцию агрегации"),
    CALCULATE(4, "рассчитать и вывести аналитику"),
    SAVE(5, "сохранить аналитику");

    private final int code;
    private final String text;
}