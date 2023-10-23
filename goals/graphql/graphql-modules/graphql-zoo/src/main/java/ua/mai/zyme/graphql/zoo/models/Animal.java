package ua.mai.zyme.graphql.zoo.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Animal {
    @NotNull
    private Long animalId;
    @NotNull
    private String nickname;
    @NotNull
    private Integer animalTypeId;
    @NotNull
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDt;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deathDt;
    private String descr;

    private Tank currTank;     // Текущее место пребывание.
    private List<Tank> tanks;  // Все места пребывания.
}
