package ua.mai.library.dto;

import java.util.List;

public record BookDto(
        Long id,
        String name,
        List<Long> authorIds,
        int pageCount,
        Long publisherId,
        String lang
) {
}
