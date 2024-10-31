package ua.mai.zyme.r2dbcmysql

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.env.Environment
import ua.mai.zyme.r2dbcmysql.util.DefaultProfileUtil
import ua.mai.zyme.r2dbcmysql.util.SpringUtil

@SpringBootApplication
//@EnableR2dbcAuditing
open class R2dbcMysqlBeApplication(private val env: Environment) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(R2dbcMysqlBeApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
//            System.setProperty("spring.application.name", "r2dbc-mysql REST service")

            val app = SpringApplication(R2dbcMysqlBeApplication::class.java)
            DefaultProfileUtil.addDefaultProfile(app)
            try {
                val env = app.run(*args).environment
                SpringUtil.logApplicationStartup(log, env)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}