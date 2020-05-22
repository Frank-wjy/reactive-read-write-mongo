package com.sample.app.repository.reader;

import com.sample.app.repository.reader.dto.ReaderPersonDto;
import com.sample.app.repository.reader.entity.ReaderPerson;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReaderRepository extends ReactiveMongoRepository<ReaderPerson, String> {

    @Aggregation("{ $match: { 'address.zipCode' : { $gte : 5 } } }")
    Flux<ReaderPerson> aggregateByZipCodeGte();

    @Aggregation(pipeline = {
            "{ '$match': { 'address.zipCode' : { '$gte' : 5 } } }",
            "{ '$project' : { 'name': '$firstName', 'code': '$address.zipCode' } }"
    })
    Flux<ReaderPersonDto> aggregateAndProjectByZipCodeGte();
}
