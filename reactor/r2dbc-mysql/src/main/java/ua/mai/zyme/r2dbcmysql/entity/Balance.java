package ua.mai.zyme.r2dbcmysql.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import ua.mai.zyme.r2dbcmysql.exception.AppException;
import ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo;
import ua.mai.zyme.r2dbcmysql.exception.ServiceFault;

import java.time.LocalDateTime;

import static ua.mai.zyme.r2dbcmysql.exception.AppFaultInfo.*;

@Builder
@Data
public class Balance {

    @Id
    @Column("member_id")
    private Integer memberId;

    private Long amount;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("last_modified_date")
    private LocalDateTime lastModifiedDate;

    public Long increaseBalance(Long delta, LocalDateTime modifiedDate) {
        Long newAmount = this.amount + delta;
        if (newAmount < 0)
            throw new ServiceFault(BALANCE_AMOUNT_NOT_ENOUGH, memberId, amount, delta);
        if (modifiedDate.isBefore(modifiedDate))
            throw new ServiceFault(BALANCE_CHANGE_DATE_EARLIER_LAST_DATE, memberId, lastModifiedDate, modifiedDate);
        amount = newAmount;
        lastModifiedDate = modifiedDate;
        return newAmount;
    }

}

