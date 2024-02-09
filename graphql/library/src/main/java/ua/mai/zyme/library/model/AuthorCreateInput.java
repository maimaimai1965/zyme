package ua.mai.zyme.library.model;

public record AuthorCreateInput(
        Long id,
        String firstName,
        String lastName,
        Gender gender
) {
}
