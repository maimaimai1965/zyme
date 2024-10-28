package ua.mai.zyme.r2dbcmysql.util

import reactor.core.publisher.Mono
import ua.mai.zyme.r2dbcmysql.entity.Balance
import java.time.LocalDateTime
import java.util.concurrent.ExecutionException

object AppUtil {

    @JvmStatic
    fun now(): LocalDateTime {
        return roundLocalDateTime(LocalDateTime.now())
    }

    @JvmStatic
    fun roundLocalDateTime(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.withNano(0)
    }

    @JvmStatic
    fun getBalanceFromMono(mono: Mono<Balance>): Balance {
        return try {
            mono.toFuture().get()
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        }
    }
}