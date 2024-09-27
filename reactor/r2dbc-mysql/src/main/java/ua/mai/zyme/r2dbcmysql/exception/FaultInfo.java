package ua.mai.zyme.r2dbcmysql.exception;

import java.util.function.Supplier;

/**
 * Created by IPotapchuk, 2/13/2018 12:26 PM
 */
public interface FaultInfo extends Supplier<ServiceFault> {

    String UNEXPECTED_ERROR_CODE = "ERR001";
    String UNEXPECTED_MESSAGE_TEMPLATE =  "Unexpected error: {0}";

    String code();
    String messageTemplate();

    @Override
    default ServiceFault get() {
        return new ServiceFault(this);
    }

}
