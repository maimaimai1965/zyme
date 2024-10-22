package ua.mai.zyme.r2dbcmysql.webclient.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zyme.r2dbc-mysql")
public class ZymeR2dbcMysqlProperty {
    private WebClientProperty webclient;

    public WebClientProperty getWebclient() {
        return webclient;
    }

    public void setWebclient(WebClientProperty webclient) {
        this.webclient = webclient;
    }
}
