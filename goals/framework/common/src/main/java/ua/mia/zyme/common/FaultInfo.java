package ua.mia.zyme.common;

import java.util.function.Supplier;

/**
 * Created by IPotapchuk, 2/13/2018 12:26 PM
 */
public interface FaultInfo extends Supplier<ServiceFault> {

    String code();

    String message();

    @Override
    default ServiceFault get() {
        return new ServiceFault(this);
    }

    default Supplier<ServiceFault> withParams(Object... errorParams) {
        return () -> new ServiceFault(this).withErrorParameters(errorParams);
    }

}
