package com.sample.app.repository.writer;

import com.sample.app.repository.writer.entity.WriterPerson;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface WriterRepository extends ReactiveMongoRepository<WriterPerson, String> {
}
