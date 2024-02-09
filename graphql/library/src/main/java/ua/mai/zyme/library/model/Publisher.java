package ua.mai.zyme.library.model;


import ua.mai.zyme.library.dto.PublisherDto;

public record Publisher(
        Long id,
        String name
) {
    public static Publisher fromDto(PublisherDto dto){
        return dto != null
              ? new Publisher(dto.id(), dto.name())
              : null;
    }

}