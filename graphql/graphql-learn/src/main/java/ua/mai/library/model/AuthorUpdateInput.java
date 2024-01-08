package ua.mai.library.model;

import ua.mai.library.dto.AuthorDto;
import ua.mai.library.dto.BookDto;

import java.util.List;
import java.util.Locale;

import static ua.mai.graphql.model.GraphqlUtils.valueForUpdate;

public record AuthorUpdateInput(
        Long id,
        String firstName,
        String lastName,
        Character gender,

        Boolean firstNameSetNull,
        Boolean lastNameSetNull,
        Boolean genderSetNull
) {
    public AuthorDto updateDto(AuthorDto dto) {
        if (dto == null)
            throw new RuntimeException("DTO for update is not set");
        if (!id.equals(dto.id()))
            throw new RuntimeException("'id' in Update is not equal to 'id' in DTO");

        AuthorDto updatedDto = new AuthorDto(
                dto.id(),
                valueForUpdate("firstName", dto.firstName(), firstName, firstNameSetNull),
                valueForUpdate("lastName", dto.lastName(), lastName, lastNameSetNull),
                valueForUpdate("gender", dto.gender(), gender, genderSetNull)
                );
        return updatedDto;
    }

}
