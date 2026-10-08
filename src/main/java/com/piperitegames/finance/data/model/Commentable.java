package com.piperitegames.finance.data.model;

import java.util.List;
import java.util.Locale;

public interface Commentable {

    List<String> getComments();

    default boolean hasCommentContaining(String part) {
        String needle = part.toLowerCase(Locale.ROOT);
        return getComments().stream()
                .map(comment -> comment.toLowerCase(Locale.ROOT))
                .anyMatch(comment -> comment.contains(needle));
    }
}