package ua.mai.zyme.rest.zoo.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerAccountsResponse {
    @NotNull
    public int partnerId;
    public String partnerName;
    public String partnerAccountCode;
    public String accountType;
    public BigDecimal quotaLimit;
    public Integer limitType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal tempQuotaLimit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public TempQuotaTriger tempQuotaTriger;


    public PartnerAccountsResponse(int partnerId,
                                   String partnerName,
                                   String partnerAccountCode,
                                   String accountType,
                                   BigDecimal quotaLimit,
                                   BigDecimal tempQuotaLimit,
                                   Integer limitType,
                                   TempQuotaTriger tempQuotaTriger) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.partnerAccountCode = partnerAccountCode;
        this.accountType = accountType;
        this.quotaLimit = quotaLimit;
        this.tempQuotaLimit = tempQuotaLimit;
        this.tempQuotaTriger = tempQuotaTriger;
        this.limitType = limitType;
    }
}



