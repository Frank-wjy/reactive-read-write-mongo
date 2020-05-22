package com.sample.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.sample.app.repository.writer",
        reactiveMongoTemplateRef = "reactiveWriterMongoTemplate")
public class WriterMongoReactiveConfig {
}
