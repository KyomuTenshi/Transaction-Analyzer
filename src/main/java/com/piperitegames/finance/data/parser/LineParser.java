package com.piperitegames.finance.data.parser;

/**
 * Превращает одну строку файла в объект модели.
 * При ошибке формата бросает {@link IllegalArgumentException} с описанием причины.
 */
@FunctionalInterface
public interface LineParser<T> {

    T parse(String line);
}