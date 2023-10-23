package ua.mai.zyme.rest.zoo.models;

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
//@JsonIgnoreProperties(ignoreUnknown=false)
public class TransactionResponse {
    @NotNull
    private Long partnerTrId;
    @NotNull
    private LocalDateTime partnerTrDt;
    @NotNull
    private QuotaRefillTransaction[] data;
}
