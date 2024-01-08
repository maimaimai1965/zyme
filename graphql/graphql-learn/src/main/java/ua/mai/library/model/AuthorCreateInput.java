package ua.mai.library.model;

import java.util.List;
import java.util.Locale;

public record AuthorCreateInput(
        Long id,
        String firstName,
        String lastName,
        Gender gender
) {
}
