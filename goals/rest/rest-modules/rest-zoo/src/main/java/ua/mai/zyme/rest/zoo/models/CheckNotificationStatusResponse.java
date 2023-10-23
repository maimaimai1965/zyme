package ua.mai.zyme.rest.zoo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckNotificationStatusResponse {
    @NotNull
    private String clientId;
    @NotNull
    private Disabled[] data;
}
