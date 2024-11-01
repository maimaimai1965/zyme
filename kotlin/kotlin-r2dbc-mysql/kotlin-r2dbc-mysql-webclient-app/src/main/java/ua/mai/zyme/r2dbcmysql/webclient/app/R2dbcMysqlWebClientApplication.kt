package ua.mai.zyme.r2dbcmysql.webclient.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Mono
import ua.mai.zyme.r2dbcmysql.entity.Member
import ua.mai.zyme.r2dbcmysql.util.TestUtil
import ua.mai.zyme.r2dbcmysql.webclient.R2dbsMysqlWebClient
import ua.mai.zyme.r2dbcmysql.webclient.property.ZymeR2dbcMysqlProperty

@SpringBootApplication(exclude = [
    R2dbcAutoConfiguration::class
])
@EnableConfigurationProperties(
    ZymeR2dbcMysqlProperty::class
)
open class R2dbcMysqlWebClientApplication {

    @Autowired
    private lateinit var zymeR2dbcMysqlProperty: ZymeR2dbcMysqlProperty

    private lateinit var mysqlWebClient: R2dbsMysqlWebClient

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val app = SpringApplication(R2dbcMysqlWebClientApplication::class.java)
            app.webApplicationType = WebApplicationType.NONE
            app.run(*args)
        }
    }

    @Bean
    open fun run(): CommandLineRunner {
        mysqlWebClient = R2dbsMysqlWebClient(zymeR2dbcMysqlProperty.webclient!!)

        return CommandLineRunner {
            println("\n-------- Приложение запущено! Выполняются действия: ------------")
            insertMember()
            insertMember_ERR101_NotNullNewMemberId()
            findMembersByNameLike()
        }
    }

    private fun insertMember() {
        println("\n*** insertMember() ***")

        val memberIn = TestUtil.newMember("mikeTest")

        try {
            val memberOut = mysqlWebClient.insertMember(Mono.just(memberIn)).block()
            println("-> $memberOut")
        } catch (ex: Exception) {
            println("-> Exception: ${ex.message}")
        }
    }

    private fun insertMember_ERR101_NotNullNewMemberId() {
        println("\n*** insertMember_ERR101_NotNullNewMemberId() ***")

        val memberIn = TestUtil.newMember("mikeTest").apply { memberId = -1 }

        try {
            val memberOut = mysqlWebClient.insertMember(Mono.just(memberIn)).block()
            println("-> $memberOut")
        } catch (ex: Exception) {
            println("-> Exception: ${ex.message}")
        }
    }

    private fun findMembersByNameLike() {
        println("\n*** findMembersByNameLike() ***")

        try {
            println("# findMembersByNameLike():")
            val members = mysqlWebClient.findMembersByNameLike("nTest").toStream().toList()
            println("-> $members")
        } catch (ex: Exception) {
            println("-> Exception: ${ex.message}")
        }
    }

}