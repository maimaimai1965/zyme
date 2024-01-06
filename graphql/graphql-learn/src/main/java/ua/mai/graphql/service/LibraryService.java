package ua.mai.graphql.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.mai.library.model.Author;
import ua.mai.library.model.Book;
import ua.mai.library.model.BookCreateInput;
import ua.mai.library.model.Publisher;
import ua.mai.library.repository.LibraryRepository;

import java.util.List;

@Service
public class LibraryService {

    @Autowired
    private LibraryRepository libraryRepository;

    public List<Book> getAllBooks() {
        return libraryRepository.getAllBookDtos().stream()
                .map(dto -> Book.fromDto(dto))
                .toList();
    }

    public Book getBookById(Long id) {
        return Book.fromDto(libraryRepository.getBookDtoById(id));
    }

    public List<Book> getBooksByIds(List<Long> ids) {
        return ids != null
              ? libraryRepository.getBookDtosByIds(ids).stream()
                    .map(dto -> Book.fromDto(dto))
                    .toList()
              : null;
    }

    public Book bookCreate(BookCreateInput input) {
        return input != null
                ? Book.fromDto(libraryRepository.bookDtoCreate(input))
                : null;
    }


    public List<Author> getAllAuthors() {
        return libraryRepository.getAllAuthorDtos().stream()
                .map(dto -> Author.fromDto(dto))
                .toList();
    }

    public Author getAuthorById(Long id) {
        return Author.fromDto(libraryRepository.getAuthorDtoById(id));
    }


    public List<Author> getAuthorsByIds(List<Long> ids) {
        return ids != null
              ? libraryRepository.getAuthorDtosByIds(ids).stream()
                    .map(dto -> Author.fromDto(dto))
                    .toList()
              : null;
    }


    public List<Publisher> getAllPublishers() {
        return libraryRepository.getAllPublisherDtos().stream()
                .map(dto -> Publisher.fromDto(dto))
                .toList();
    }
    public Publisher getPublisherById(Long id) {
        return Publisher.fromDto(libraryRepository.getPublisherDtoById(id));
    }

}
