package ua.mai.library.repository;

import org.springframework.stereotype.Repository;
import ua.mai.library.dto.AuthorDto;
import ua.mai.library.dto.BookDto;
import ua.mai.library.dto.PublisherDto;
import ua.mai.library.model.AuthorCreateInput;
import ua.mai.library.model.AuthorUpdateInput;
import ua.mai.library.model.BookCreateInput;
import ua.mai.library.model.BookUpdateInput;

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

    public List<BookDto> bookDtos() {
        return bookDtos;
    }


    // ----- Book ----
    public BookDto bookDtoByIdGet(Long id) {
        return bookDtos.stream()
                .filter(book -> book.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<BookDto> bookDtosByIdsGet(List<Long> ids) {
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
        } else if (bookDtoByIdGet(id) != null) {
            throw new RuntimeException("Book with id=" + id + "already exists");
        }
        BookDto dto = new BookDto(id, name, input.authorIds(), input.pageCount(), input.publisherId(),
                input.lang() != null ? input.lang().getLanguage().toString() : null);
        bookDtos.add(dto);
        return dto;
    }

    public BookDto bookDtoUpdate(BookUpdateInput input) {
        if (input.id() == null)
            throw new RuntimeException("'id' is not set");

        BookDto dto = bookDtoByIdGet(input.id());
        if (dto == null)
            throw new RuntimeException("Book with id=" + input.id() + " not exist");

        BookDto updatedDto = input.updateDto(dto);
        bookDtos.remove(dto);
        bookDtos.add(updatedDto);

        return updatedDto;
    }


    // ----- Author ----
    public List<AuthorDto> authorDtosGet() {
        return authorDtos;
    }

    public AuthorDto authorDtoByIdGet(Long id) {
        return authorDtos.stream()
                .filter(author -> author.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<AuthorDto> authorDtosByIdsGet(List<Long> ids) {
        return authorDtos.stream()
                .filter(author -> ids.contains(author.id()))
                .collect(Collectors.toList());
    }

    public AuthorDto authorDtoCreate(AuthorCreateInput input) {
        Long id = input.id();
        String firstName = input.firstName();

        if (input.id() == null) {
            id = authorDtos.stream().max(Comparator.comparingLong(AuthorDto::id)).get().id() + 1;
            firstName += " {" + id + "}";
        } else if (bookDtoByIdGet(id) != null) {
            throw new RuntimeException("Author with id=" + id + "already exists");
        }

        AuthorDto dto = new AuthorDto(id, firstName, input.lastName(), input.gender() != null ? input.gender().getConstant() : null);
        authorDtos.add(dto);

        return dto;
    }

    public AuthorDto authorDtoUpdate(AuthorUpdateInput input) {
        if (input.id() == null)
            throw new RuntimeException("'id' is not set");

        AuthorDto dto = authorDtoByIdGet(input.id());
        if (dto == null)
            throw new RuntimeException("Author with id=" + input.id() + " not exist");

        AuthorDto updatedDto = input.updateDto(dto);
        authorDtos.remove(dto);
        authorDtos.add(updatedDto);

        return updatedDto;
    }


    // ----- Publisher ----
    public List<PublisherDto> publisherDtosGet() {
        return publisherDtos;
    }

    public PublisherDto getPublisherDtoByIdGet(Long id) {
        return publisherDtos.stream()
                .filter(publisher -> publisher.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<PublisherDto> getPublisherDtosByIdsGet(List<Long> ids) {
        return publisherDtos.stream()
                .filter(publisher -> ids.contains(publisher.id()))
                .collect(Collectors.toList());
    }

}
