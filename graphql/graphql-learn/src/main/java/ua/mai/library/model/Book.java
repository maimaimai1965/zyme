package ua.mai.library.model;

import ua.mai.library.dto.BookDto;

import java.util.List;
import java.util.Locale;

public record Book (
        Long id,
        String name,
        List<Long> authorIds,
        List<Author> authors,
        Integer pageCount,
        Long publisherId,
        Publisher publisher,
        Locale lang
) {
    static public Book fromDto(BookDto dto){
        return dto != null
              ? new Book(dto.id(), dto.name(), dto.authorIds(), null, dto.pageCount(), dto.publisherId(), null,
                         dto.lang() != null ? Locale.of(dto.lang()) : null)
              : null;
    }

    public BookDto toDto(Book book){
        return new BookDto(book.id, book.name, book.authorIds, book.pageCount, book.publisherId, book.lang.getLanguage());
    }

}
