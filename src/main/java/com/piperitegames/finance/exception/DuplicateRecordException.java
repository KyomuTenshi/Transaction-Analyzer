package com.piperitegames.finance.exception;

import java.nio.file.Path;

public class DuplicateRecordException extends FinanceAnalyzerException {

    public DuplicateRecordException(Path path, String fieldName, long id) {
        super("В файле %s повторяется %s: %d".formatted(path.getFileName(), fieldName, id));
    }
}