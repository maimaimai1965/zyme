package ua.mai.library.model;

import ua.mai.library.dto.BookDto;

import java.util.List;
import java.util.Locale;

public record BookCreateInput (
        Long id,
        String name,
        List<Long> authorIds,
        Integer pageCount,
        Long publisherId,
        Locale lang
) {
}
