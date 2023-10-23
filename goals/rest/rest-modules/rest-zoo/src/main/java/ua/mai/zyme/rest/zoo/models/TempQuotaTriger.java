package ua.mai.zyme.rest.zoo.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TempQuotaTriger {
    @JsonProperty("to")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDateTime to;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal refillAmount;

    public TempQuotaTriger(BigDecimal refillAmount, LocalDateTime to) {
        this.to = to;
        this.refillAmount = refillAmount;
    }
}