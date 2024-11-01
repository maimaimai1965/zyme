package ua.mai.zyme.r2dbcmysql.webclient.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("zyme.r2dbc-mysql")
class ZymeR2dbcMysqlProperty {

    var webclient: WebClientProperty? = null

}