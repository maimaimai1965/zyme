package ua.mai.zyme.r2dbcmysql.exception

import org.springframework.http.HttpStatus

class FaultException : RuntimeException {

    private val faultDetails: FaultDetails

    constructor(faultInfo: FaultInfo, vararg errorParameters: Any?) : super() {
        this.faultDetails = FaultDetails(faultInfo, errorParameters.toList())
    }

    constructor(cause: Throwable?, faultInfo: FaultInfo, vararg errorParameters: Any?) : super(cause) {
        this.faultDetails = FaultDetails(faultInfo, errorParameters.toList())
    }

    constructor(cause: Throwable?) : super(cause) {
        this.faultDetails = FaultDetails(AppFaultInfo.UNEXPECTED_ERROR, cause?.message)
    }

    val code: String
        get() = faultDetails.faultInfo.code()

    override val message: String
        get() = faultMessage

    val httpStatus: HttpStatus
        get() = faultDetails.faultInfo.httpStatus()

    val faultMessage: String
        get() = faultDetails.getMessage()

    val causeMessage: String?
        get() = cause?.message

    val errorParameters: List<Any?>
        get() = faultDetails.errorParameters

    override fun toString(): String {
        return "Code=$code, Message=\"$faultMessage\"" +
                (cause?.let { ", Details=$it" } ?: "") +
                ", HttpStatus=$httpStatus"
    }

}