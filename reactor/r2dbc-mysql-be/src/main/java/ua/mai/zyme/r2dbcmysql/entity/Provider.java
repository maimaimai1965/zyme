package ua.mai.zyme.r2dbcmysql.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Builder
@Data
@Table(name = "cn_provider")
public class Provider {
    @Id
    Integer providerId;
    String providerName;
    String providerType;
    String state;
    LocalDateTime createdDt;
    LocalDateTime endDt;
}
