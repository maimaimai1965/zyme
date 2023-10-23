package ua.mia.zyme.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    // LocalDate <-> LocalDateTime
    public static LocalDateTime toLocalDateTime(LocalDate d) {
        return d != null ? d.atStartOfDay() : null;
    }
    public static LocalDate toLocalDate(LocalDateTime dt) {
        return dt != null ? dt.toLocalDate() : null;
    }

    // LocalDateTime <-> String
    public static LocalDate toLocalDate(String localDateStr) {
        return LocalDate.parse(localDateStr, DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN));
    }
    public static LocalDate toLocalDate(String localDateStr, String pattern) {
        return LocalDate.parse(localDateStr, DateTimeFormatter.ofPattern(pattern));
    }
    public static LocalDateTime toLocalDateTimeWithDatePattern(String strDt) {
        return toLocalDateTime(toLocalDate(strDt));
    }

    // Date <-> String
    public static Date toDate(String strDt) throws ParseException {
        return new SimpleDateFormat(DEFAULT_DATE_PATTERN).parse(strDt);
    }
    public static Date toDate(String strDt, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(strDt);
    }

    // Timestamp <-> LocalDateTime
    public static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
    public static Timestamp toTimestamp(LocalDateTime dt) {
        return dt != null ? Timestamp.valueOf(dt) : null;
    }

    // Timestamp <-> LocalDate
    public static LocalDate toLocalDate(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime().toLocalDate() : null;
    }
    public static Timestamp toTimestamp(LocalDate d) {
        return d != null ? Timestamp.valueOf(d.atStartOfDay()) : null;
    }

}
