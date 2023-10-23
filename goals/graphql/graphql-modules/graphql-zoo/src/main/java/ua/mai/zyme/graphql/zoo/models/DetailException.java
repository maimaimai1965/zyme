package ua.mai.zyme.graphql.zoo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DetailException {
    private String errorCd;
    private String errorMsg;
    private String detailMsg;
}
