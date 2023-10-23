package ua.mai.zyme.rest.zoo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerMfs {
    public int partnerId;
    public String partnerName;
    public List<PartnerMfsResponse> data;
}
