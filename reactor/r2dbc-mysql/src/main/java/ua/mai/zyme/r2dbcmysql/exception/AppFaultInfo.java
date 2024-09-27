package ua.mai.zyme.r2dbcmysql.exception;

public enum AppFaultInfo implements FaultInfo {

    UNEXPECTED_ERROR(FaultInfo.UNEXPECTED_ERROR_CODE, FaultInfo.UNEXPECTED_MESSAGE_TEMPLATE),

    MEMBER_NOT_EXISTS("ERR100", "Member not exists (memberId={0})"),

    BALANCE_FOR_MEMBER_NOT_EXISTS("ERR300", "Balance for member (memberId={0}) not exists"),
    BALANCE_AMOUNT_CANNOT_BE_NEGATIVE("ERR301", "Amount in balance can't be negative (memberId={0}, amount={1})"),
    BALANCE_CHANGE_DATE_EARLIER_LAST_DATE("ERR302", "You cannot change a balance with a change date prior to the last change date (memberId={0}, lastModifiedDate={1}, modifiedDate={2})"),
    BALANCE_AMOUNT_NOT_ENOUGH("ERR303", "Not enough amount on balance (memberId={0}, amount={1}, delta={2})"),

    TRANSFER_FROM_MEMBER_NOT_EXISTS("ERR400", "From member in transfer not exists"),
    TRANSFER_TO_MEMBER_NOT_EXISTS("ERR400", "To member in transfer not exists"),
    ;

    private final String code;
    private final String messageTemplate;

    AppFaultInfo(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String messageTemplate() {
        return messageTemplate;
    }

}
