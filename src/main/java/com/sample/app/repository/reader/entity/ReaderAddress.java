package com.sample.app.repository.reader.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReaderAddress {

    private String city;
    private String street;
    private Integer homeNumber;
    private Integer apartmentNumber;
    private Integer zipCode;
}
