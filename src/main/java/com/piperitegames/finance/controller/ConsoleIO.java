package com.piperitegames.finance.controller;

import com.piperitegames.finance.exception.InputClosedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.util.Optional;
import java.util.function.Function;

public class ConsoleIO {

    private final BufferedReader in;
    private final PrintStream out;
    private final PrintStream err;

    public ConsoleIO() {
        this(new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)), System.out, System.err);
    }

    public ConsoleIO(BufferedReader in, PrintStream out, PrintStream err) {
        this.in = in;
        this.out = out;
        this.err = err;
    }

    public void println() {
        out.println();
    }

    public void println(String text) {
        out.println(text);
    }

    public void error(String text) {
        out.flush();
        err.println(text);
        err.flush();
    }

    public String readLine(String prompt) {
        out.print(prompt);
        out.flush();
        try {
            String line = in.readLine();
            if (line == null) {
                throw new InputClosedException();
            }
            return line;
        } catch (IOException e) {
            throw new InputClosedException(e);
        }
    }

    /**
     * Читает необязательное значение: пустой ввод — Optional.empty(),
     * некорректный ввод — сообщение об ошибке и повторный запрос.
     */
    public <T> Optional<T> readOptional(String prompt, Function<String, T> parser, String errorMessage) {
        while (true) {
            String input = readLine(prompt).strip();
            if (input.isEmpty()) {
                return Optional.empty();
            }
            try {
                return Optional.of(parser.apply(input));
            } catch (IllegalArgumentException | DateTimeException e) {
                error(errorMessage);
            }
        }
    }
}