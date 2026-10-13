package com.piperitegames.finance.data.repository.file;

import com.piperitegames.finance.data.parser.LineParser;
import com.piperitegames.finance.exception.DataAccessException;
import com.piperitegames.finance.exception.DataFileNotFoundException;
import com.piperitegames.finance.exception.InvalidDataFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

final class TextFileReader {

    private static final Pattern INLINE_COMMENT = Pattern.compile("(^|\\s)#.*$");
    private static final String BYTE_ORDER_MARK = "\uFEFF";

    private TextFileReader() {
    }

    static <T> List<T> readAll(Path path, LineParser<T> parser) {
        if (!Files.isRegularFile(path)) {
            throw new DataFileNotFoundException(path);
        }

        List<T> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.startsWith(BYTE_ORDER_MARK)) {
                    line = line.substring(1);
                }
                String content = INLINE_COMMENT.matcher(line).replaceFirst("").strip();
                if (content.isEmpty()) {
                    continue;
                }
                try {
                    result.add(parser.parse(content));
                } catch (IllegalArgumentException e) {
                    throw new InvalidDataFormatException(path, lineNumber, e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Не удалось прочитать файл " + path.toAbsolutePath(), e);
        }
        return result;
    }
}