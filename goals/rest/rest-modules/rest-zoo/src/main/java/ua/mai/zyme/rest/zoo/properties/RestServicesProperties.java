package ua.mai.zyme.rest.zoo.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

//@ConfigurationProperties(prefix = "zyme.rest-services")
@Data
public class RestServicesProperties {
//    private String apiUrl;
    private ServiceSettings zoo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceSettings {
        private String url;
        private String roles;
    }

}
