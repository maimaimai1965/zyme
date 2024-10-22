package ua.mai.zyme.r2dbcmysql.webclient.property;

public class WebClientProperty {

    private String baseUrl;
    private int connectTimeout;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

}
