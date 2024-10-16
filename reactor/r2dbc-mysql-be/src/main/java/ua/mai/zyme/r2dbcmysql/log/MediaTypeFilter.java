package ua.mai.zyme.r2dbcmysql.log;

import org.springframework.http.MediaType;

public interface MediaTypeFilter {

    default boolean logged(MediaType mediaType) {
        return true;
    }

}
