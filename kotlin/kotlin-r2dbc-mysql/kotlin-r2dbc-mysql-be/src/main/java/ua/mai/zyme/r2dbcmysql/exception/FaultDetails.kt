package ua.mai.zyme.r2dbcmysql.exception

import java.text.MessageFormat
import java.util.*


open class FaultDetails(
    val faultInfo: FaultInfo,
    val errorParameters: List<Any?>
) {

    constructor (faultInfo: FaultInfo, vararg errorParameters: Any?) :
      this (faultInfo, listOf(*errorParameters))

    fun getMessage(): String {
        return MessageFormat.format(faultInfo.messageTemplate(), *errorParameters.toTypedArray())
    }

}