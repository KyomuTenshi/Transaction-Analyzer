package com.piperitegames.finance.data.model;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString(callSuper = true)
public class CommentableTransaction extends Transaction implements Commentable {

    private final List<String> comments;

    public CommentableTransaction(long accountId, long id, LocalDateTime dateTime,
                                  String category, BigDecimal amount, List<String> comments) {
        super(accountId, id, dateTime, category, amount);
        this.comments = List.copyOf(comments);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.COMMENTABLE;
    }
}