package ua.mai.graphql.сontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import ua.mai.graphql.service.LibraryService;
import ua.mai.library.model.*;

import java.util.List;

@Controller
public class LibraryController {

    @Autowired
    LibraryService libraryService;


    // ----- Book ----
    @SchemaMapping
    public List<Author> authors(Book book) {
        return libraryService.getAuthorsByIds(book.authorIds());
    }

    @SchemaMapping
    public Publisher publisher(Book book) {
        return libraryService.getPublisherById(book.publisherId());
    }

    @QueryMapping
    List<Book> books() {
        return libraryService.getAllBooks();
    }

    @QueryMapping
    public Book bookById(@Argument Long id) {
        return libraryService.getBookById(id);
    }

    @QueryMapping
    public List<Book> booksByIds(@Argument List<Long> ids) {
        return libraryService.getBooksByIds(ids);
    }

    @MutationMapping
    public BookPayload bookCreate(@Argument BookCreateInput input) {
        return new BookPayload(libraryService.bookCreate(input));
    }

    // ----- Author ----
    @QueryMapping
    List<Author> authors() {
        return libraryService.getAllAuthors();
    }


    // ----- Publisher ----
    @QueryMapping
    List<Publisher> publishers() {
        return libraryService.getAllPublishers();
    }

}