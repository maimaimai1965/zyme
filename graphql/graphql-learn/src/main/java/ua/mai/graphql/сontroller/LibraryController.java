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
        return libraryService.authorsByIds(book.authorIds());
    }

    @SchemaMapping
    public Publisher publisher(Book book) {
        return libraryService.publisherById(book.publisherId());
    }

    @QueryMapping
    List<Book> books() {
        return libraryService.books();
    }

    @QueryMapping
    public Book bookById(@Argument Long id) {
        return libraryService.bookById(id);
    }

    @QueryMapping
    public List<Book> booksByIds(@Argument List<Long> ids) {
        return libraryService.booksByIds(ids);
    }

    @MutationMapping
    public BookPayload bookCreate(@Argument BookCreateInput input) {
        return new BookPayload(libraryService.bookCreate(input));
    }

    @MutationMapping
    public BookPayload bookUpdate(@Argument BookUpdateInput input) {
        return new BookPayload(libraryService.bookUpdate(input));
    }


    // ----- Author ----
    @QueryMapping
    List<Author> authors() {
        return libraryService.authors();
    }

    @QueryMapping
    public Author authorById(@Argument Long id) {
        return libraryService.authorById(id);
    }

    @QueryMapping
    public List<Author> authorsByIds(@Argument List<Long> ids) {
        return libraryService.authorsByIds(ids);
    }

    @MutationMapping
    public AuthorPayload authorCreate(@Argument AuthorCreateInput input) {
        return new AuthorPayload(libraryService.authorCreate(input));
    }

    @MutationMapping
    public AuthorPayload authorUpdate(@Argument AuthorUpdateInput input) {
        return new AuthorPayload(libraryService.authorUpdate(input));
    }


    // ----- Publisher ----
    @QueryMapping
    List<Publisher> publishers() {
        return libraryService.publishers();
    }

}