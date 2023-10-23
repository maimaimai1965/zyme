package ua.mai.zyme.rest.zoo.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerMfsResponse {

    public Integer serviceCode;
    public String serviceTitle;
    public String state;
    public LocalDate validFrom;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDate validTo;

}
