package com.piperitegames.finance.exception;

import java.nio.file.Path;

public class AnalyticsSaveException extends DataAccessException {

    public AnalyticsSaveException(Path path, Throwable cause) {
        super("Не удалось сохранить аналитику в файл " + path.toAbsolutePath(), cause);
    }
}