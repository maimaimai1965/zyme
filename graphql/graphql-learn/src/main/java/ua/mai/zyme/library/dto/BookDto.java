package ua.mai.zyme.library.dto;

import java.util.List;

public record BookDto(
        Long id,
        String name,
        List<Long> authorIds,
        Integer pageCount,
        Long publisherId,
        String lang
) {
}
