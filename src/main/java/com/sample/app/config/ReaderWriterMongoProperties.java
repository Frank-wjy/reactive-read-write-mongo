package com.sample.app.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mongodb")
public class ReaderWriterMongoProperties {

    private MongoProperties reader;
    private MongoProperties writer;
}
