package com.sample.app.web.rest;


import com.sample.app.repository.mapper.ReaderWriterMapper;
import com.sample.app.repository.reader.ReaderRepository;
import com.sample.app.repository.reader.dto.ReaderPersonDto;
import com.sample.app.repository.reader.entity.ReaderAddress;
import com.sample.app.repository.reader.entity.ReaderPerson;
import com.sample.app.repository.writer.WriterRepository;
import com.sample.app.repository.writer.entity.WriterPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

@RestController
@RequiredArgsConstructor
public class PersonController {

    private final ReaderRepository readerRepository;
    private final WriterRepository writerRepository;
    private final ReaderWriterMapper readerWriterMapper;

    @PostMapping("/post-reader-person")
    Mono<Void> postReaderPerson() {
        int nextInt = new Random().nextInt(10);
        return readerRepository.save(

                ReaderPerson.builder()
                        .firstName("Israel-" + nextInt)
                        .lastName("Israeli-" + nextInt)
                        .address(ReaderAddress
                                .builder()
                                .city("Tel-Aviv-Yafo-" + nextInt)
                                .street("Jerusalem-" + nextInt)
                                .homeNumber(nextInt)
                                .apartmentNumber(nextInt)
                                .zipCode(nextInt)
                                .build())
                        .build())
                .then();
    }

    @GetMapping("/get-reader-person")
    Flux<ReaderPerson> getReaderPerson() {
        return readerRepository.findAll();
    }

    @GetMapping("/get-writer-person")
    Flux<WriterPerson> getWriterPerson() {
        return writerRepository.findAll();
    }

    @GetMapping("/get-aggregated-reader-person")
    Flux<ReaderPerson> getAggregatedReaderPerson() {
        return readerRepository.aggregateByZipCodeGte();
    }

    @GetMapping("/get-aggregated-reader-person-dto")
    Flux<ReaderPersonDto> getAggregatedReaderPersonDto() {
        return readerRepository.aggregateAndProjectByZipCodeGte();
    }

    @PostMapping("/post-reader-writer-person")
    Mono<Void> postReadWritePerson() {

        return readerRepository
                .findAll()
                .map(readerWriterMapper::toWriterPerson)
                .flatMap(writerRepository::save)
                .then();
    }
}
