package com.piperitegames.finance.service.analytics;

import java.util.Comparator;

/**
 * Ключ группы: подпись для вывода и числовой порядок для сортировки.
 */
public record GroupKey(long order, String label) implements Comparable<GroupKey> {

    public static final GroupKey UNKNOWN_ACCOUNT = new GroupKey(Long.MAX_VALUE, "счёт не найден");

    private static final Comparator<GroupKey> COMPARATOR =
            Comparator.comparingLong(GroupKey::order).thenComparing(GroupKey::label);

    @Override
    public int compareTo(GroupKey other) {
        return COMPARATOR.compare(this, other);
    }
}