package ua.mai.zyme.library.model;

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
