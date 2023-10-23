package ua.mai.zyme.rest.zoo.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerMfsServiceDto {
    @NotNull
    private Integer serviceCode;
    @NotNull
    private String serviceTitle;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validTo;

    public PartnerMfsServiceDto(PartnerMfsServiceDtoIn in) {
        serviceCode = in.getServiceCode();
        serviceTitle = in.getServiceTitle();
        validFrom = LocalDate.parse(in.getValidFrom(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (in.getValidTo() != null) {
            validTo = LocalDate.parse(in.getValidTo(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }
}