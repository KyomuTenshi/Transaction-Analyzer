package com.piperitegames.finance.exception;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class InvalidDataFormatException extends FinanceAnalyzerException {

    private final transient Path path;
    private final int lineNumber;

    public InvalidDataFormatException(Path path, int lineNumber, String reason, Throwable cause) {
        super("Ошибка в файле %s, строка %d: %s".formatted(path.getFileName(), lineNumber, reason), cause);
        this.path = path;
        this.lineNumber = lineNumber;
    }
}