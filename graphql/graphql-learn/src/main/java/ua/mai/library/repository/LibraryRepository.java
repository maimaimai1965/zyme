package ua.mai.library.repository;

import org.springframework.stereotype.Repository;
import ua.mai.library.dto.AuthorDto;
import ua.mai.library.dto.BookDto;
import ua.mai.library.dto.PublisherDto;
import ua.mai.library.model.BookCreateInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LibraryRepository {

    enum LangEnum {
        en, uk, ru
    }


    private static List<BookDto> bookDtos = new ArrayList<>(
        Arrays.asList(
            new BookDto(1L, "Effective Java", List.of(1L), 416, 1L, LangEnum.en.name()),
            new BookDto(2L, "Hitchhiker's Guide to the Galaxy", List.of(2L), 208, 2L, LangEnum.en.name()),
            new BookDto(3L, "Down Under", List.of(3L), 436, null, LangEnum.en.name())
        ));

    private static List<AuthorDto> authorDtos =  new ArrayList<>(
        Arrays.asList(
            new AuthorDto(1L, "Joshua", "Bloch", 'M'),
            new AuthorDto(2L, "Douglas", "Adams", 'M'),
            new AuthorDto(3L, "Bill", "Bryson", null)
        ));

    private static List<PublisherDto> publisherDtos = new ArrayList<>(
        Arrays.asList(
            new PublisherDto(1L, "O'Reilly Media"),
            new PublisherDto(2L, "Wiley")
        ));

    public List<BookDto> getAllBookDtos() {
        return bookDtos;
    }

    // ----- Book ----
    public BookDto getBookDtoById(Long id) {
        return bookDtos.stream()
                .filter(book -> book.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<BookDto> getBookDtosByIds(List<Long> ids) {
        return bookDtos.stream()
                .filter(book -> ids.contains(book.id()))
                .collect(Collectors.toList());
    }

    public BookDto bookDtoCreate(BookCreateInput input) {
        Long id = input.id();
        String name = input.name();
        if (input.id() == null) {
            id = bookDtos.stream().max(Comparator.comparingLong(BookDto::id)).get().id() + 1;
            name += " {" + id + "}";
        } else if (getBookDtoById(id) != null) {
            throw new RuntimeException("Book with id=" + id + "already exists");
        }
        BookDto dto = new BookDto(id, name, input.authorIds(), input.pageCount(), input.publisherId(),
                input.lang() != null ? input.lang().getLanguage().toString() : null);
        bookDtos.add(dto);
        return dto;
    }


    // ----- Author ----
    public List<AuthorDto> getAllAuthorDtos() {
        return authorDtos;
    }

    public AuthorDto getAuthorDtoById(Long id) {
        return authorDtos.stream()
                .filter(author -> author.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<AuthorDto> getAuthorDtosByIds(List<Long> ids) {
        return authorDtos.stream()
                .filter(author -> ids.contains(author.id()))
                .collect(Collectors.toList());
    }

    // ----- Publisher ----
    public List<PublisherDto> getAllPublisherDtos() {
        return publisherDtos;
    }

    public PublisherDto getPublisherDtoById(Long id) {
        return publisherDtos.stream()
                .filter(publisher -> publisher.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<PublisherDto> getPublisherDtosByIds(List<Long> ids) {
        return publisherDtos.stream()
                .filter(publisher -> ids.contains(publisher.id()))
                .collect(Collectors.toList());
    }

}
