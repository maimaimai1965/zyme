package ua.mai.library.dto;

import ua.mai.library.model.Gender;

import java.util.Arrays;
import java.util.List;

public record AuthorDto(
        Long id,
        String firstName,
        String lastName,
        Character gender
) {
}