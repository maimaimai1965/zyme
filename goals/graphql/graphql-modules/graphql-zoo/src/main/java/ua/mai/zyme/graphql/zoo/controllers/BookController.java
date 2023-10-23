package ua.mai.zyme.graphql.zoo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import ua.mai.zyme.graphql.zoo.models.Author;
import ua.mai.zyme.graphql.zoo.models.Book;
import ua.mai.zyme.graphql.zoo.services.BookService;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @QueryMapping
    public Book bookById(@Argument String id) {
        return bookService.bookById(id);
    }

    @SchemaMapping
    public Author author(Book book) {
        return bookService.authorById(book.authorId());
    }
}