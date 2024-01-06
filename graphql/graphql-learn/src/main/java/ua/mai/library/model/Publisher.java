package ua.mai.library.model;


import ua.mai.library.dto.PublisherDto;

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