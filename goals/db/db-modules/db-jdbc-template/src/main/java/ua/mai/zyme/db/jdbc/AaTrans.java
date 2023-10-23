package ua.mai.zyme.db.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AaTrans {
    private Long transId;
    private String transName;
    private String note;
    private LocalDateTime createdDt;
}
