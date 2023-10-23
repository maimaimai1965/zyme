package ua.mai.zyme.graphql.zoo.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disabled {
    @NotNull
    @JsonProperty("isDisabled")
    private Boolean disabled;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime disableDate;
}
