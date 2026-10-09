package com.piperitegames.finance.exception;

/**
 * Поток ввода закрыт (например, нажато Ctrl+D). Это не бизнес-ошибка,
 * а сигнал завершить работу, поэтому наследуется напрямую от RuntimeException.
 */
public class InputClosedException extends RuntimeException {

    public InputClosedException() {
        super("Поток ввода закрыт");
    }

    public InputClosedException(Throwable cause) {
        super("Ошибка чтения с консоли", cause);
    }
}