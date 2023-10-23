package ua.mai.zyme.graphql.zoo.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.mai.zyme.db.zo.schema.TankType;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tank {
    @NotNull
    private Integer tankId;
    @NotNull
    private TankType tankType;
    @NotNull
    private String numberCd;
    private String descr;

    Animal currAnimals;     // Животные, которые размещаются здесь сейчас.
    List<Animal> animals;   // Все животные, которые размещались здесь.
}
