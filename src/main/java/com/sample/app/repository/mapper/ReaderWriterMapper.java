package com.sample.app.repository.mapper;

import com.sample.app.repository.reader.entity.ReaderPerson;
import com.sample.app.repository.writer.entity.WriterPerson;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReaderWriterMapper {

    WriterPerson toWriterPerson(ReaderPerson readerPerson);

    ReaderPerson toReaderPerson(WriterPerson writerPerson);
}
