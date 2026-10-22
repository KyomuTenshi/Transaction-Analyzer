package com.piperitegames.finance.controller;

import com.piperitegames.finance.controller.option.SearchOption;
import com.piperitegames.finance.service.filter.TransactionFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class SearchMenuController extends AbstractMenuController<SearchOption> {

    private static final String DATE_ERROR = "Неверный формат даты. Используйте ГГГГ-ММ-ДД, например 2024-09-27.";
    private static final String AMOUNT_ERROR = "Неверный формат суммы. Пример: -50 или 150.75.";

    private final AnalyticsSession session;
    private TransactionFilter draft;

    public SearchMenuController(ConsoleIO console, AnalyticsSession session) {
        super(console, "Выберите способ поиска транзакции:", SearchOption.class);
        this.session = session;
    }

    @Override
    protected void onStart() {
        draft = session.getFilter();
    }

    @Override
    protected Optional<String> header() {
        return Optional.of("Текущий фильтр: " + draft.describe());
    }

    @Override
    protected boolean handle(SearchOption option) {
        switch (option) {
            case SAVE_AND_BACK -> {
                session.setFilter(draft);
                console.println("Фильтр сохранён: " + draft.describe());
                return false;
            }
            case RESET -> {
                session.setFilter(TransactionFilter.empty());
                console.println("Все фильтры сброшены.");
                return false;
            }
            case BY_CATEGORY -> readCategory();
            case BY_DATE_RANGE -> readDateRange();
            case BY_AMOUNT_RANGE -> readAmountRange();
            case BY_COMMENT -> readComment();
        }
        return true;
    }

    private void readCategory() {
        String category = console.readLine("Введите категорию (Enter — поиск по всем категориям): ");
        draft = draft.withCategory(category);
    }

    private void readDateRange() {
        console.println("Будут найдены транзакции, которые находятся в диапазоне дат, а также");
        console.println("повторяющиеся транзакции, которые выполнятся в указанном диапазоне.");
        console.println("Формат даты: ГГГГ-ММ-ДД, например 2024-09-27.");

        LocalDate from = console.readOptional(
                "Введите начальную дату транзакции (Enter — не ограничивать начальную дату): ",
                LocalDate::parse, DATE_ERROR).orElse(null);
        LocalDate to = console.readOptional(
                "Введите конечную дату транзакции (Enter — не ограничивать конечную дату): ",
                LocalDate::parse, DATE_ERROR).orElse(null);

        draft = draft.withDateRange(from, to);
    }

    private void readAmountRange() {
        BigDecimal min = console.readOptional(
                "Введите минимальную сумму транзакции (Enter — не ограничивать минимальную сумму): ",
                SearchMenuController::parseAmount, AMOUNT_ERROR).orElse(null);
        BigDecimal max = console.readOptional(
                "Введите максимальную сумму транзакции (Enter — не ограничивать максимальную сумму): ",
                SearchMenuController::parseAmount, AMOUNT_ERROR).orElse(null);

        draft = draft.withAmountRange(min, max);
    }

    private void readComment() {
        String comment = console.readLine("Введите комментарий или его часть (Enter — для поиска всех комментариев): ");
        draft = draft.withComment(comment);
    }

    private static BigDecimal parseAmount(String input) {
        String normalized = input
                .replace('\u2212', '-')
                .replace(',', '.');
        return new BigDecimal(normalized);
    }
}