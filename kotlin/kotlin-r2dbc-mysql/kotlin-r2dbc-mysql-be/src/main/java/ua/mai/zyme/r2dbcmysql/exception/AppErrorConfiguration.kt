package ua.mai.zyme.r2dbcmysql.exception

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.result.view.ViewResolver

@Configuration
open class AppErrorConfiguration {

    @Bean
    @Order(-2)
    open fun errorWebExceptionHandler(
        serverProperties: ServerProperties,
        errorAttributes: ErrorAttributes,
        webProperties: WebProperties,
        viewResolvers: ObjectProvider<ViewResolver>,
        serverCodecConfigurer: ServerCodecConfigurer,
        applicationContext: ApplicationContext
    ): ErrorWebExceptionHandler {
        val exceptionHandler = AppErrorWebExceptionHandler(
            errorAttributes,
            webProperties.resources,
            serverProperties.error,
            applicationContext
        )
        exceptionHandler.setViewResolvers(viewResolvers.orderedStream().toList())
        exceptionHandler.setMessageWriters(serverCodecConfigurer.writers)
        exceptionHandler.setMessageReaders(serverCodecConfigurer.readers)
        return exceptionHandler
    }

}