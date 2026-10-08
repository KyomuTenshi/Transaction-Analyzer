package com.piperitegames.finance.exception;

import java.nio.file.Path;

public class DataFileNotFoundException extends DataAccessException {

    public DataFileNotFoundException(Path path) {
        super("Файл не найден: " + path.toAbsolutePath());
    }
}