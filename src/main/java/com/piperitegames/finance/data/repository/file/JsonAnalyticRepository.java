package com.piperitegames.finance.data.repository.file;

import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.piperitegames.finance.data.model.AnalyticsReport;
import com.piperitegames.finance.data.repository.AnalyticRepository;
import com.piperitegames.finance.exception.AnalyticsSaveException;

import java.io.IOException;
import java.nio.file.Path;

public class JsonAnalyticRepository implements AnalyticRepository {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN)
            .build();

    private final Path path;

    public JsonAnalyticRepository(Path path) {
        this.path = path;
    }

    @Override
    public void save(AnalyticsReport report) {
        try {
            MAPPER.writeValue(path.toFile(), report);
        } catch (IOException e) {
            throw new AnalyticsSaveException(path, e);
        }
    }
}