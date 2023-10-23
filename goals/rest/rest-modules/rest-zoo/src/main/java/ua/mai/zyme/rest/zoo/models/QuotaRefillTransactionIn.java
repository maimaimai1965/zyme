package ua.mai.zyme.rest.zoo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotaRefillTransactionIn {
    @NotNull
    private Long docId;
    @NotNull
    private String docDt;
    @NotNull
    @Size(max = 20)
    private String partnerAccCd;
    @NotNull
    private BigDecimal amount;
    @NotNull
    @Size(max = 240)
    private String bankTrxNumber;
    @NotNull
    private String realDt;
    @NotNull
    @Size(max = 256)
    private String trxText;
}
