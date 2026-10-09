package com.piperitegames.finance.controller;

import com.piperitegames.finance.controller.option.GroupMenuOption;

public class GroupMenuController extends AbstractMenuController<GroupMenuOption> {

    private final AnalyticsSession session;

    public GroupMenuController(ConsoleIO console, AnalyticsSession session) {
        super(console, "Выберите опцию группировки транзакции:", GroupMenuOption.class);
        this.session = session;
    }

    @Override
    protected boolean handle(GroupMenuOption option) {
        session.setGroupOption(option.getGroupOption());
        console.println("Выбрано: " + option.getGroupOption().getTitle());
        return false;
    }
}