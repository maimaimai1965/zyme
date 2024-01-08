package ua.mai.graphql.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.mai.library.model.*;
import ua.mai.library.repository.LibraryRepository;

import java.util.List;

@Service
public class LibraryService {

    @Autowired
    private LibraryRepository libraryRepository;


    // ----- Book ----
    public List<Book> books() {
        return libraryRepository.bookDtos().stream()
                .map(dto -> Book.fromDto(dto))
                .toList();
    }

    public Book bookById(Long id) {
        return Book.fromDto(libraryRepository.bookDtoByIdGet(id));
    }

    public List<Book> booksByIds(List<Long> ids) {
        return ids != null
              ? libraryRepository.bookDtosByIdsGet(ids).stream()
                    .map(dto -> Book.fromDto(dto))
                    .toList()
              : null;
    }

    public Book bookCreate(BookCreateInput input) {
        return input != null
                ? Book.fromDto(libraryRepository.bookDtoCreate(input))
                : null;
    }

    public Book bookUpdate(BookUpdateInput input) {
        return input != null
                ? Book.fromDto(libraryRepository.bookDtoUpdate(input))
                : null;
    }


    // ----- Author ----
    public List<Author> authors() {
        return libraryRepository.authorDtosGet().stream()
                .map(dto -> Author.fromDto(dto))
                .toList();
    }

    public Author authorById(Long id) {
        return Author.fromDto(libraryRepository.authorDtoByIdGet(id));
    }


    public List<Author> authorsByIds(List<Long> ids) {
        return ids != null
              ? libraryRepository.authorDtosByIdsGet(ids).stream()
                    .map(dto -> Author.fromDto(dto))
                    .toList()
              : null;
    }

    public Author authorCreate(AuthorCreateInput input) {
        return input != null
                ? Author.fromDto(libraryRepository.authorDtoCreate(input))
                : null;
    }

    public Author authorUpdate(AuthorUpdateInput input) {
        return input != null
                ? Author.fromDto(libraryRepository.authorDtoUpdate(input))
                : null;
    }


    // ----- Publisher ----
    public List<Publisher> publishers() {
        return libraryRepository.publisherDtosGet().stream()
                .map(dto -> Publisher.fromDto(dto))
                .toList();
    }
    public Publisher publisherById(Long id) {
        return Publisher.fromDto(libraryRepository.getPublisherDtoByIdGet(id));
    }

}
