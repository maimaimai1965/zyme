package ua.mai.zyme.rest;

import ua.mai.zyme.rest.exceptions.RestFaultDetailMsgException;
import ua.mai.zyme.rest.exceptions.RestFaults;
import ua.mia.zyme.common.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public final class RestUtil {

    public static LocalDate toLocalDate(String paramName, String strDate) {
        try {
            return strDate != null ? DateUtil.toLocalDate(strDate) : null;
        }
        catch (DateTimeParseException ex) {
            throw new RestFaultDetailMsgException(ex, ex.toString(), RestFaults.UNSUPPORTED_PARAMETER_VALUE, paramName, strDate);
        }
    }

    public static LocalDateTime toLocalDateTime(String paramName, String strDate) {
        try {
            return strDate != null ? DateUtil.toLocalDateTimeWithDatePattern(strDate) : null;
        }
        catch (DateTimeParseException ex) {
            throw new RestFaultDetailMsgException(ex, ex.toString(), RestFaults.UNSUPPORTED_PARAMETER_VALUE, paramName, strDate);
        }
    }

    public static BigDecimal toBigDecimal(String paramName, String strLong) {
        return BigDecimal.valueOf(toLong(paramName, strLong));
    }

    public static Long toLong(String paramName, String strLong) {
        try {
            return strLong != null ? Long.valueOf(strLong) : null;
        }
        catch (NumberFormatException ex) {
            throw new RestFaultDetailMsgException(ex, ex.toString(), RestFaults.UNSUPPORTED_PARAMETER_VALUE, paramName, strLong);
        }
    }

    public static Integer toInteger(String paramName, String strInteger) {
        try {
            return strInteger != null ? Integer.valueOf(strInteger) : null;
        }
        catch (NumberFormatException ex) {
            throw new RestFaultDetailMsgException(ex, ex.toString(), RestFaults.UNSUPPORTED_PARAMETER_VALUE, paramName, strInteger);
        }
    }

    public static Boolean toBoolean(String paramName, String str) {
        if (str == null || str.isEmpty())
            return null;
        else if (str.equals("0"))
            return Boolean.FALSE;
        else if (str.equals("1"))
            return Boolean.TRUE;

        throw new RestFaultDetailMsgException(str, RestFaults.UNSUPPORTED_PARAMETER_VALUE, paramName, str);
    }

    public static List<Integer> toIntegerList(String paramName, List<String> list) {
        return list == null || list.size() == 0 ? null : list.stream().map(str -> toInteger(paramName, str)).collect(Collectors.toList());
    }

    public static String[] toStringArray(String commaListStr) {
        if (commaListStr == null) {
            return new String[]{};
        }
        else {
            String[] ar = commaListStr.split(",");
            for (int i=0; i<ar.length; i++ ) {
                ar[i] = ar[i].trim();
            }
            return ar;
        }
    }

}
