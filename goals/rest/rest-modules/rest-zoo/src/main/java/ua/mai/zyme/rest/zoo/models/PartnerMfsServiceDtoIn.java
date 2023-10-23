package ua.mai.zyme.rest.zoo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerMfsServiceDtoIn {
    @NotNull
    private Integer serviceCode;
    @NotNull
    @Size(max = 256)
    private String serviceTitle;
    @NotNull
    private String validFrom;

    private String validTo;
}