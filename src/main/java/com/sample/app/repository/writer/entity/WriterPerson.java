package com.sample.app.repository.writer.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "person2")
public class WriterPerson {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private WriterAddress address;
}
