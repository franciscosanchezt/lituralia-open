package com.campusdual.lituraliaopen.api.mapper;

import com.campusdual.lituraliaopen.api.mapper.dtos.BookDTO;
import com.campusdual.lituraliaopen.domain.Book;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PublisherMapper.class, GenreMapper.class, AuthorMapper.class})
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDTO bookToBookDTO(Book book);

    Book bookDTOToBook(BookDTO bookDto);

}
