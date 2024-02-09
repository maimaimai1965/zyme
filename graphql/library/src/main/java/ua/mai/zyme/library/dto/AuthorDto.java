package ua.mai.zyme.library.dto;

public record AuthorDto(
        Long id,
        String firstName,
        String lastName,
        Character gender
) {
}