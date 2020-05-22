package com.sample.app.repository.reader.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReaderPersonDto {

    private String name;
    private Integer code;
}
