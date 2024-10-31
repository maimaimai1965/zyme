package ua.mai.zyme.r2dbcmysql.util

import org.slf4j.Logger
import org.springframework.core.env.Environment
import java.net.InetAddress
import java.net.UnknownHostException

object SpringUtil {

    fun logApplicationStartup(logger: Logger, env: Environment) {
        var protocol = "http"
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https"
        }

        val serverPort = env.getProperty("server.port")
        var contextPath = env.getProperty("server.servlet.context-path")
        if (contextPath.isNullOrBlank()) {
            contextPath = "/"
        }

        var hostAddress = "localhost"
        try {
            hostAddress = InetAddress.getLocalHost().hostAddress
        } catch (e: UnknownHostException) {
            logger.warn("The host name could not be determined, using `localhost` as fallback")
        }

        logger.info(
            """
            ----------------------------------------------------------
            Application '{}' is running! Access URLs:
            Local:      {}://localhost:{}{}
            External:   {}://{}:{}{}
            Profile(s): {}
            ----------------------------------------------------------
            """.trimIndent(),
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.activeProfiles.joinToString()
        )
    }

}