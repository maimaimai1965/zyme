package ua.mai.zyme.rest.zoo.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotaRefillTransaction {
    @NotNull
    private Long docId;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate docDt;
    @NotNull
    @Size(max = 20)
    private String partnerAccCd;
    @NotNull
    private BigDecimal amount;
    @NotNull
    @Size(max = 240)
    private String bankTrxNumber;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate realDt;
    @NotNull
    @Size(max = 256)
    private String trxText;

    public QuotaRefillTransaction(QuotaRefillTransactionIn in) {
        docId = in.getDocId();
        docDt = LocalDate.parse(in.getDocDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        partnerAccCd = in.getPartnerAccCd();
        amount = in.getAmount();
        bankTrxNumber = in.getBankTrxNumber();
        realDt = LocalDate.parse(in.getRealDt(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));;
        trxText = in.getTrxText();
    }

}
