package com.sample.app.repository.reader.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "person1")
public class ReaderPerson {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private ReaderAddress address;
}
