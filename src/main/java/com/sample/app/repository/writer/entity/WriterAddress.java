package com.sample.app.repository.writer.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WriterAddress {

    private String city;
    private String street;
    private Integer homeNumber;
    private Integer apartmentNumber;
    private Integer zipCode;
}
