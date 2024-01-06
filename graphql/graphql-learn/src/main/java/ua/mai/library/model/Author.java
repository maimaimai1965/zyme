package ua.mai.library.model;

import ua.mai.library.dto.AuthorDto;

import java.util.Arrays;
import java.util.List;

public record Author (
        Long id,
        String firstName,
        String lastName,
        Gender gender
) {

    static public Author fromDto(AuthorDto dto){
        return dto != null
              ? new Author(dto.id(), dto.firstName(), dto.lastName(), Gender.constantOf(dto.gender()))
              : null;
    }

    public AuthorDto toDto() {
        return new AuthorDto(id, firstName, lastName, gender != null ? gender.constant : null);
    }

}