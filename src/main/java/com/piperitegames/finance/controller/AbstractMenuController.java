package com.piperitegames.finance.controller;

import com.piperitegames.finance.controller.option.MenuOption;
import com.piperitegames.finance.exception.FinanceAnalyzerException;

import java.util.Arrays;
import java.util.Optional;

public abstract class AbstractMenuController<O extends Enum<O> & MenuOption> {

    protected final ConsoleIO console;
    private final String title;
    private final O[] options;

    protected AbstractMenuController(ConsoleIO console, String title, Class<O> optionType) {
        this.console = console;
        this.title = title;
        this.options = optionType.getEnumConstants();
    }

    public final void run() {
        onStart();
        boolean keepRunning = true;
        while (keepRunning) {
            printMenu();
            Optional<O> option = parseOption(console.readLine("> "));
            if (option.isEmpty()) {
                console.error("Нет такой опции. Введите номер пункта из списка.");
                continue;
            }
            try {
                keepRunning = handle(option.get());
            } catch (FinanceAnalyzerException e) {
                console.error(e.getMessage());
            }
        }
    }

    /**
     * Обрабатывает выбранный пункт.
     *
     * @return true — остаться в этом меню, false — выйти из него
     */
    protected abstract boolean handle(O option);

    protected void onStart() {
    }

    protected Optional<String> header() {
        return Optional.empty();
    }

    private void printMenu() {
        console.println();
        console.println(title);
        header().ifPresent(console::println);
        for (O option : options) {
            console.println(option.getCode() + " — " + option.getText());
        }
        console.println("Введите нужную опцию и нажмите Enter.");
    }

    private Optional<O> parseOption(String input) {
        try {
            int code = Integer.parseInt(input.strip());
            return Arrays.stream(options)
                    .filter(option -> option.getCode() == code)
                    .findFirst();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}