package ua.mia.zyme.common;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceFault extends RuntimeException {

    private FaultDetails faultInfo;

    public ServiceFault(FaultInfo faultInfo) {
        this(faultInfo.code(), faultInfo.message());
    }

    public ServiceFault(String code, String message) {
        this(code, message, null);
    }

    public ServiceFault(FaultInfo faultInfo, Throwable cause) {
        this(faultInfo.code(), faultInfo.message(), cause);
    }

    public ServiceFault(String code, String message, Throwable cause) {
        super(cause);
        setFaultInfo(new FaultDetails().withCode(code).withMessage(message));
    }

    public ServiceFault(FaultInfo faultInfo, Object... errorParameters) {
        this(faultInfo.code(), faultInfo.message());
        setErrorParameters(errorParameters);
    }

    /**
     * This constructor called by cxf and shouldn't be called directly.
     * Use {@link #ServiceFault(String, String)} or {@link #ServiceFault(FaultInfo)} instead
     **/
    @Deprecated
    public ServiceFault(String message, FaultDetails faultInfo) {
        setFaultInfo(faultInfo);
    }

    /* Keep this method because it's called by cxf */
    public FaultDetails getFaultInfo() {
        return new FaultDetails()
                .withCode(faultInfo.getCode())
                .withMessage(faultInfo.getMessage())
                .withValues(faultInfo.getValues());
    }

    public String getCode() {
        return faultInfo.getCode();
    }

    @Override
    public String getMessage() {
        return faultInfo.getMessage();
    }

    public String getFormattedMessage() {
        return MessageFormat.format(faultInfo.getMessage(), getErrorParameters());
    }

    public void setErrorParameters(Object... errorParameters) {
        faultInfo.getValues().addAll(createTypedValues(errorParameters));
    }

    public Object[] getErrorParameters() {
        return flattenTypedValues(faultInfo.getValues());
    }

    public ServiceFault withErrorParameters(Object... errorParameters) {
        setErrorParameters(errorParameters);
        return this;
    }

    private static List<TypedValue> createTypedValues(Object[] errorParameters) {
        if (errorParameters == null || errorParameters.length == 0) return Collections.emptyList();
        List<TypedValue> typedValues = new ArrayList<>(errorParameters.length);
        for (Object obj : errorParameters) {
            TypedValue value = new TypedValue();
            if (obj instanceof String) {
                value.setStringValue((String) obj);
            } else if (obj instanceof LocalDateTime) {
                value.setDateValue((LocalDateTime) obj);
            }
//            else if (obj instanceof Date) {
//                value.setDateValue((Date) obj);
//            }
            else if (obj instanceof Number) {
                value.setNumberValue(new BigDecimal(obj.toString()));
            } else if (obj instanceof Boolean) {
                value.setBooleanValue((Boolean) obj);
            } else if (obj instanceof Enum) {
                value.setEnumValue(EnumUtil.getValueOfEnum((Enum<?>) obj));
            }
            typedValues.add(value);
        }
        return typedValues;
    }

    private static Object[] flattenTypedValues(List<TypedValue> typedValues) {
        if (typedValues == null || typedValues.isEmpty()) return new Object[0];
        List<Object> errorParams = new ArrayList<>();
        for (TypedValue value : typedValues) {
            if (value.getStringValue() != null) {
                errorParams.add(value.getStringValue());
            } else if (value.getDateValue() != null) {
                errorParams.add(value.getDateValue());
            } else if (value.getBooleanValue() != null) {
                errorParams.add(value.getBooleanValue());
            } else if (value.getNumberValue() != null) {
                errorParams.add(value.getNumberValue());
            } else if (value.getEnumValue() != null) {
                errorParams.add(value.getEnumValue());
            }
        }
        return errorParams.toArray();
    }

    private void setFaultInfo(FaultDetails faultInfo) {
        Assert.notNull(faultInfo, "faultInfo required");
        Assert.hasLength(faultInfo.getCode(), "code must have symbols");
        Assert.hasLength(faultInfo.getMessage(), "message must have symbols");
        this.faultInfo = faultInfo;
    }

}
