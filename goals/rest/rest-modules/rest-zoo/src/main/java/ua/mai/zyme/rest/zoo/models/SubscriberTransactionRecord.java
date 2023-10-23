package ua.mai.zyme.rest.zoo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberTransactionRecord {
    private Long transId;
    private LocalDateTime transDt;
    private BigDecimal amount;
    private String currencyCd;
    private Long partnerId;
    private String partnerTitle;
    private Byte stateNo;
    private String stateCd;
    private String stateTitle;
    private Long serviceNo;
    private String serviceTitle;
    // additionalData
    private Integer code;
    private String name;
    private String serviceType;
    private String channel;
    private Long partnerTransNr;
    private BigDecimal partnerFee;
    private String descr;
    private BigDecimal limitAfterTrans;
}
