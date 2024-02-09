package ua.mai.zyme.library.model;

import ua.mai.zyme.library.dto.BookDto;

import java.util.List;
import java.util.Locale;

import static ua.mai.zyme.library.model.ModelUtils.valueForUpdate;

public record BookUpdateInput(
        Long id,
        String name,
        List<Long> authorIds,
        Integer pageCount,
        Long publisherId,
        Locale lang,

        Boolean nameSetNull,
        Boolean authorIdsSetNull,
        Boolean pageCountSetNull,
        Boolean publisherIdSetNull,
        Boolean langSetNull
) {
    public BookDto updateDto(BookDto dto) {
        if (dto == null)
            throw new RuntimeException("DTO for update is not set");
        if (!id.equals(dto.id()))
            throw new RuntimeException("'id' in Update is not equal to 'id' in DTO");

        BookDto updatedDto = new BookDto(
                dto.id(),
                valueForUpdate("name", dto.name(), name, nameSetNull),
                valueForUpdate("authorIds", dto.authorIds(), authorIds, authorIdsSetNull),
                valueForUpdate("pageCount", dto.pageCount(), pageCount, pageCountSetNull),
                valueForUpdate("publisherId", dto.publisherId(), publisherId, publisherIdSetNull),
                valueForUpdate("lang", dto.lang(), lang, langSetNull)
                );
        return updatedDto;
    }

}
