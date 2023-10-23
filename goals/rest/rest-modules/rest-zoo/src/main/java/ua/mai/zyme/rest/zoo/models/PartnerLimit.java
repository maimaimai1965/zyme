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
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonIgnoreProperties(ignoreUnknown=false)
public class PartnerLimit {
    @NotNull
    @Size(max = 64)
    private String partnerAccountCode;
    @NotNull
    private LimitType limitType;
    @NotNull
    private BigDecimal tempQuotaLimit;
    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime to;
    private BigDecimal refillAmount;
    @NotNull
    @Size(max = 128)
    private String reason;
}
