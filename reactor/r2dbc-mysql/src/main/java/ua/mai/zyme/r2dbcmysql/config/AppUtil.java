package ua.mai.zyme.r2dbcmysql.config;

import reactor.core.publisher.Mono;
import ua.mai.zyme.r2dbcmysql.entity.Balance;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public class AppUtil {

    static public LocalDateTime now() {
        return roundLocalDateTime(LocalDateTime.now());
    }
    static public LocalDateTime roundLocalDateTime(LocalDateTime dateTime) {
        return dateTime.withNano(0);
    }

    static public Balance getBalanceFromMono(Mono<Balance> mono) {
        try {
            return mono.toFuture().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }



}
