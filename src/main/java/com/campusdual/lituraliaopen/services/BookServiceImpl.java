package com.campusdual.lituraliaopen.services;

import com.campusdual.lituraliaopen.api.mapper.AuthorMapper;
import com.campusdual.lituraliaopen.api.mapper.BookMapper;
import com.campusdual.lituraliaopen.api.mapper.GenreMapper;
import com.campusdual.lituraliaopen.api.mapper.PublisherMapper;
import com.campusdual.lituraliaopen.api.mapper.dtos.AuthorDTO;
import com.campusdual.lituraliaopen.api.mapper.dtos.BookDTO;
import com.campusdual.lituraliaopen.api.mapper.dtos.GenreDTO;
import com.campusdual.lituraliaopen.api.mapper.dtos.PublisherDTO;
import com.campusdual.lituraliaopen.api.service.BookService;
import com.campusdual.lituraliaopen.domain.Author;
import com.campusdual.lituraliaopen.domain.Book;
import com.campusdual.lituraliaopen.domain.Genre;
import com.campusdual.lituraliaopen.domain.Publisher;
import com.campusdual.lituraliaopen.repositories.AuthorRepository;
import com.campusdual.lituraliaopen.repositories.BookRepository;
import com.campusdual.lituraliaopen.repositories.GenreRepository;
import com.campusdual.lituraliaopen.repositories.PublisherRepository;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {


    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;


    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper,
                           PublisherRepository publisherRepository, PublisherMapper publisherMapper,
                           GenreRepository genreRepository, GenreMapper genreMapper,
                           AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.bookRepository      = bookRepository;
        this.publisherRepository = publisherRepository;
        this.bookMapper          = bookMapper;
        this.publisherMapper     = publisherMapper;
        this.genreRepository     = genreRepository;
        this.genreMapper         = genreMapper;
        this.authorRepository    = authorRepository;
        this.authorMapper        = authorMapper;
    }


    @Override
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                             .map(bookMapper::bookToBookDTO);
    }

    @Override
    public Page<BookDTO> searchBooks(String searchTerm, Pageable pageable) throws ResourceNotFoundException {
        return bookRepository.findByTitleContainingIgnoreCase(searchTerm, pageable)
                             .map(bookMapper::bookToBookDTO);
    }

    @Override
    public BookDTO getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                             .map(bookMapper::bookToBookDTO)
                             .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public BookDTO createBook(BookDTO bookDto) {
        Book entity = bookRepository.save(bookMapper.bookDTOToBook(bookDto));
        return bookMapper.bookToBookDTO(entity);
    }

    @Override
    public BookDTO updateBook(Integer bookId, BookDTO bookDto) {
        Book book = bookMapper.bookDTOToBook(bookDto);
        book.setBookId(bookId);
        Book entity = bookRepository.save(book);
        return bookMapper.bookToBookDTO(entity);
    }

    @Override
    public BookDTO updateBook(BookDTO bookDto) {
        return updateBook(bookDto.getBookId(), bookDto);
    }

    @Override
    public void deleteBookById(Integer bookId) throws ResourceNotFoundException {
        bookRepository.deleteById(bookId);
    }

    // -------- Book's Publisher


    @Override
    public PublisherDTO getBookPublisher(Integer bookId) throws ResourceNotFoundException {
        return bookRepository.findById(bookId)
                             .map(Book::getPublisher)
                             .map(publisherMapper::publisherToPublisherDTO)
                             .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public PublisherDTO setBookPublisher(Integer bookId, Integer publisherId) throws ResourceNotFoundException {
        return bookRepository.findById(bookId)
                             .map(book -> {
                                 Publisher publisher = publisherRepository.findById(publisherId)
                                                                          .orElseThrow(ResourceNotFoundException::new);
                                 book.setPublisher(publisher);
                                 Book book1 = bookRepository.saveAndFlush(book);
                                 return book1.getPublisher();
                             })
                             .map(publisherMapper::publisherToPublisherDTO)
                             .orElseThrow(ResourceNotFoundException::new);
    }

    // -------- Book's Genres

    @Override
    public Slice<GenreDTO> getBookGenres(Integer bookId) throws ResourceNotFoundException {
        return new SliceImpl<>(bookRepository.findById(bookId)
                                             .map(book -> {
                                                 return book.getGenres().stream()
                                                            .map(genreMapper::genreToGenreDTO)
                                                            .collect(Collectors.toList());
                                             })
                                             .orElseThrow(ResourceNotFoundException::new));
    }

    @Override
    public Slice<GenreDTO> setBookGenre(Integer bookId, Integer genreId) throws ResourceNotFoundException {
        return new SliceImpl<>(bookRepository.findById(bookId)
                                             .map(book -> {
                                                 Genre genre = genreRepository.findById(genreId)
                                                                              .orElseThrow(ResourceNotFoundException::new);
                                                 book.getGenres().add(genre);
                                                 Book book1 = bookRepository.saveAndFlush(book);
                                                 return book1.getGenres().stream()
                                                             .map(genreMapper::genreToGenreDTO)
                                                             .collect(Collectors.toList());
                                             })
                                             .orElseThrow(ResourceNotFoundException::new));
    }

    @Override
    public Slice<GenreDTO> deleteBookGenre(Integer bookId, Integer genreId) throws ResourceNotFoundException {
        return new SliceImpl<>(bookRepository.findById(bookId)
                                             .map(book -> {
                                                 Genre genre = genreRepository.findById(genreId)
                                                                              .orElseThrow(ResourceNotFoundException::new);
                                                 book.getGenres().remove(genre);
                                                 Book book1 = bookRepository.saveAndFlush(book);
                                                 return book1.getGenres().stream()
                                                             .map(genreMapper::genreToGenreDTO)
                                                             .collect(Collectors.toList());
                                             })
                                             .orElseThrow(ResourceNotFoundException::new));
    }

    // -------- Book's Authors


    @Override
    public Slice<AuthorDTO> getBookAuthors(Integer bookId) throws ResourceNotFoundException {
        return new SliceImpl<>(bookRepository.findById(bookId)
                                             .map(book -> book.getAuthors().stream()
                                                              .map(authorMapper::authorToAuthorDTO)
                                                              .collect(Collectors.toList()))
                                             .orElseThrow(ResourceNotFoundException::new));
    }

    @Override
    public Slice<AuthorDTO> setBookAuthor(Integer bookId, Integer authorId) throws ResourceNotFoundException {
        return new SliceImpl<>(bookRepository.findById(bookId)
                                             .map(book -> {
                                                 Author author = authorRepository.findById(authorId)
                                                                                 .orElseThrow(ResourceNotFoundException::new);
                                                 book.getAuthors().add(author);
                                                 Book book1 = bookRepository.saveAndFlush(book);
                                                 return book1.getAuthors().stream()
                                                             .map(authorMapper::authorToAuthorDTO)
                                                             .collect(Collectors.toList());
                                             })
                                             .orElseThrow(ResourceNotFoundException::new));
    }

    @Override
    public Slice<AuthorDTO> deleteBookAuthor(Integer bookId, Integer authorId) throws ResourceNotFoundException {
        return new SliceImpl<>(bookRepository.findById(bookId)
                                             .map(book -> {
                                                 Author author = authorRepository.findById(authorId)
                                                                                 .orElseThrow(ResourceNotFoundException::new);
                                                 book.getAuthors().remove(author);
                                                 Book book1 = bookRepository.saveAndFlush(book);
                                                 return book1.getAuthors().stream()
                                                             .map(authorMapper::authorToAuthorDTO)
                                                             .collect(Collectors.toList());
                                             })
                                             .orElseThrow(ResourceNotFoundException::new));
    }
}
