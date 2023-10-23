package ua.mai.zyme.db.jooq;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa.schema.UserState;
import ua.mai.zyme.db.jooq.converter.UserType;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static ua.mai.zyme.db.aa.schema.Tables.AA_USER;
import static ua.mai.zyme.db.jooq.converter.UserTypeEnumConverter.*;
import static ua.mai.zyme.db.jooq.converter.YearMonthToLocalDateTimeConverter.*;

/**
 * <pre>
 * Примеры использования конверторов в JOOQ.
 *   <i>selectWithConverterForYearMonthType()</i> - примеры конвертации в SELECT операторах используя свой конвертор
 *      <i>YEARMONTH_TO_LOCALDATETIME_CONVERTER</i>, а так же созданный на его базе свой тип данных <i>YEARMONTH</i> (на
 *      базе своего <i>YearMonth</i>).
 *   <i>selectWithConverterForEnum()</i> - примеры конвертации в SELECT операторах используя свой конвертор для Enum -
 *       <i>USER_TYPE_ENUM_CONVERTER</i>.
 *   <i>insertWithConverterForYearMonthType()</i> - примеры конвертации в INSERT операторах своего типа <i>YearMonth</i>
 *       в <i>LocalDateTime</i> (В БД <i>timestamp</i>), используя свой конвертор <i>YEARMONTH_TO_LOCALDATETIME_CONVERTER</i>,
 *       а так же созданный на его базе свой тип данных <i>YEARMONTH</i> (на базе своего <i>YearMonth</i>).
 *   <i>insertWithConverterForEnum()</i> - примеры конвертации в INSERT операторах <i>Enum</i> в <i>String</i> (В БД
 *       <i>char</i>), используя конвертор <i>USER_TYPE_ENUM_CONVERTER</i>, а так же созданный на его базе <i>USERTYPE</i>
 *       (на базе своего <i>UserType</i>).
 *   <i>selectForcedEnum()</i> - пример конвертации поля <i>char</i> в <i>Enum</i>, которая используется JOOQ по умолчанию.
 *       Она настраивается в файле goals/db/db-modules/aa-schema-gen/pom.xm
 *   <i>selectForcedEnumFieldAsChar()</i> - пример считывания из таблицы реального значени поля, для которого задана в
 *       JOOQ по умолчанию конвертация <i>char</i> в <i>Enum</i> .
 * </pre>
 */
@Service
@Transactional
public class JooqConvertersService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqConvertersService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqConvertersService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }

    /**
     * <pre>
     * Примеры конвертации в SELECT операторах используя свой конвертор YEARMONTH_TO_LOCALDATETIME_CONVERTER, а так же
     * созданный на его базе свой тип данных YEARMONTH (на базе своего YearMonth).
     *
     * SQL:
     * select cast(`aa_user`.`CREATED_DT` as datetime) as `YEAR_MONTH`
     * </pre>
     */
    @Transactional
    public void selectWithConverterForYearMonthType() {
        String userName = "exec1";

        // 1. Считывание LocalDateTime и конвертирование в YearMonth через конвертер типа данных, вызываемого отдельно.
        LocalDateTime resultLocalDateTime = dslContext
                .select(AA_USER.CREATED_DT)
                .from(AA_USER)
                .where(AA_USER.UNAME.eq(userName))
                .fetchAny(AA_USER.CREATED_DT);

        LOG.debug("selectWithConverterForYearMonthType() - Converter is not used, so the result is fetched as LocaleDateTime: {}", resultLocalDateTime);
        YearMonth resultYM  = YEARMONTH.convert(resultLocalDateTime);                         // Используется конвертер типа данных 
        LOG.debug("selectWithConverterForYearMonthType() - convert() Use data type's convert() method: {}", resultYM);
        YearMonth resultYM_ = YEARMONTH_TO_LOCALDATETIME_CONVERTER.from(resultLocalDateTime); // Используется конвертер напрямую
        LOG.debug("selectWithConverterForYearMonthType() - convert() Use converter.from() method: {}", resultYM);

        // 2. Считывание массива значений типа LocalDateTime и конвертирование в массив YearMonth через
        //    YEAR_MONTH_TO_LOCAL_DATE_TIME_ARR_CONVERTER, вызываемого отдельно.
        LocalDateTime[] resultArLocalDateTime = dslContext
                .select(AA_USER.CREATED_DT)
                .from(AA_USER)
                .limit(3)
                .fetchArray(AA_USER.CREATED_DT);

        YearMonth[] resultArrYM2 = YEARMONTH_TO_LOCALDATETIME_ARR_CONVERTER.from(resultArLocalDateTime);
        LOG.debug("selectWithConverterForYearMonthType() - Use converter for arrays: {}", Arrays.toString(resultArrYM2));

        // 3. Считывание массива значений типа LocalDateTime и конвертирование в массив YearMonth с типом YearMonth через fetchArray().
        YearMonth[] resultArrYM3 = dslContext
                .select(AA_USER.CREATED_DT)
                .from(AA_USER)
                .limit(3)
                .fetchArray(AA_USER.CREATED_DT, YEARMONTH_TO_LOCALDATETIME_CONVERTER);
        LOG.debug("selectWithConverterForYearMonthType() - Convert in fetchArray(): {}", Arrays.toString(resultArrYM3));

        // 4. Считывание списка значений типа LocalDateTime и конвертирование в список YearMonth через
        //    YEARMONTH_TO_LOCALDATETIME_CONVERTER в fetch().
        List<YearMonth> resultListYM4 = dslContext
                .select(AA_USER.CREATED_DT)
                .from(AA_USER)
                .limit(3)
                .fetch(AA_USER.CREATED_DT, YEARMONTH_TO_LOCALDATETIME_CONVERTER);
        LOG.debug("selectWithConverterForYearMonthType() - Convert in fetch(): {}", resultListYM4);

        // 5. Считывание списка значений типа LocalDateTime и конвертирование в тип YearMonth в самом SQL, используя свой
        //    тип YearMonth и оператор cast().
        //    SQL:
        //    select cast(`aa_user`.`CREATED_DT` as datetime) as `YEAR_MONTH`
        //    from `aa_user` limit 3
        List resultList5 = dslContext
                .select(AA_USER.CREATED_DT.cast(YEARMONTH).as(AA_USER.USER_TYPE.getName()))
                .from(AA_USER)
                .limit(3)
                .fetch().getValues(AA_USER.USER_TYPE.getName());
        LOG.debug("selectWithConverterForYearMonthType() - cast in select(): {}", resultList5);

        // 6. Считывание списка значений типа LocalDateTime и конвертирование в тип YearMonth используя свой тип данных
        //    YEARMONTH и метод coerce().
        //    SQL:
        //    select `aa_user`.`CREATED_DT` as `YEAR_MONTH` from `aa_user` limit 3
        List resultList6 = dslContext
                .select(AA_USER.CREATED_DT.coerce(YEARMONTH))
                .from(AA_USER)
                .limit(3)
                .fetch().getValues(AA_USER.CREATED_DT.getName());;
        LOG.debug("selectWithConverterForYearMonthType() - coerce in select(): {}", resultList6);

        // 7. Считывание списка значений типа LocalDateTime и конвертирование в тип YearMonth используя свой
        //    YEARMONTH_TO_LOCALDATETIME_CONVERTER в методе convert().
        List resultList7 = dslContext
                .select(AA_USER.CREATED_DT.convert(YEARMONTH_TO_LOCALDATETIME_CONVERTER))
                .from(AA_USER)
                .limit(3)
                .fetchInto(YearMonth.class);
        LOG.debug("selectWithConverterForYearMonthType() - convert(converter) in select(): {}", resultList7);

        // 8. Считывание списка значений типа LocalDateTime и конвертирование в тип YearMonth используя лямбда функцию
        //    для конвертации в методе convertFrom().
        List resultList8 = dslContext
                .select(AA_USER.CREATED_DT.convertFrom(t -> YEARMONTH_TO_LOCALDATETIME_CONVERTER.from(t)))
                .from(AA_USER)
                .limit(3)
                .fetchInto(YearMonth.class);
        LOG.debug("selectWithConverterForYearMonthType() - lambda convertFrom(converter.from()) in select(): {}", resultList8);

        // 9. Считывание списка значений типа LocalDateTime и конвертирование в тип YearMonth используя лямбда функцию
        //    для конвертации в методе convertFrom().
        List resultList9 = dslContext
                .select(AA_USER.CREATED_DT.convertFrom(
                        t -> {
                            if (t != null) {
                                return YearMonth.from(t.toInstant(ZoneOffset.UTC)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate());
                            }
                            return null;
                        }
                ))
                .from(AA_USER)
                .limit(3)
                .fetchInto(YearMonth.class);
        LOG.debug("selectWithConverterForYearMonthType() - lambda converter in select(): {}", resultList9);
    }

    /**
     * <pre>
     * Примеры конвертации в SELECT операторах используя свой конвертор для Enum - USER_TYPE_ENUM_CONVERTER.
     *
     * SQL:
     * select `aa_user`.`USER_TYPE` from `aa_user` where `aa_user`.`UNAME` = ?
     * </pre>
     */
    @Transactional
    public void selectWithConverterForEnum() {
        String userName = "exec1";

        // 1. Считывание символа и конвертирование в Enum через конвертер USER_TYPE_ENUM_CONVERTER, вызываемый отдельно.
        String resultUserType = dslContext
                .select(AA_USER.USER_TYPE)
                .from(AA_USER)
                .where(AA_USER.UNAME.eq(userName))
                .fetchAny(AA_USER.USER_TYPE);

        LOG.debug("Converter is not used, so the result is fetched as String: {}", resultUserType);
        UserType userType = USER_TYPE_ENUM_CONVERTER.from(resultUserType); 
        LOG.debug("selectWithConverterForEnum() - convert() Use data converter.from() method: {}", userType);

        // 2. Считывание массива символов и конвертирование в массив UserType через USER_TYPE_ENUM_ARR_CONVERTER, вызываемого отдельно.
        String[] resultAr2 = dslContext
                .select(AA_USER.USER_TYPE)
                .from(AA_USER)
                .limit(3)
                .fetchArray(AA_USER.USER_TYPE);

        UserType[] resultUserTypeAr2 = USER_TYPE_ENUM_ARR_CONVERTER.from(resultAr2);
        LOG.debug("selectWithConverterForEnum() - Use converter for arrays: {}", Arrays.toString(resultUserTypeAr2));

        // 3. Считывание массива символов и конвертирование в массив UserType в fetchArray().
        UserType[] resultArr3 = dslContext
                .select(AA_USER.USER_TYPE)
                .from(AA_USER)
                .limit(3)
                .fetchArray(AA_USER.USER_TYPE, USER_TYPE_ENUM_CONVERTER);
        LOG.debug("selectWithConverterForEnum() - Convert in fetchArray(): {}", Arrays.toString(resultArr3));

        // 4. Считывание списка символов с конвертированием в UserType в fetch().
        List<UserType> resultList4 = dslContext
                .select(AA_USER.USER_TYPE)
                .from(AA_USER)
                .limit(3)
                .fetch(AA_USER.USER_TYPE, USER_TYPE_ENUM_CONVERTER);
        LOG.debug("selectWithConverterForEnum() - Convert in fetch(): {}", resultList4);

        // 5. Считывание списка символов c конвертированием в UserType в самом SQL используя свой
        //    тип UserType и оператор cast().
        //    SQL:
        //    select cast(`aa_user`.`USER_TYPE` as char) as `USER_TYPE` from `aa_user` limit 3
        List resultList5 = dslContext
                .select(AA_USER.USER_TYPE.cast(USERTYPE).as(AA_USER.USER_TYPE.getName()))
                .from(AA_USER)
                .limit(3)
                .fetch().getValues(AA_USER.USER_TYPE.getName());;
        LOG.debug("selectWithConverterForEnum() - cast in select(): {}", resultList5);

        // 6. Считывание списка символов c конвертированием в UserType используя тип USERTYPE и метод coerce().
        //    SQL:
        //    select `aa_user`.`USER_TYPE` from `aa_user` limit 3
        List result6 = dslContext
                .select(AA_USER.USER_TYPE.coerce(USERTYPE))
                .from(AA_USER)
                .limit(3)
                .fetch().getValues(AA_USER.USER_TYPE.getName());
        LOG.debug("selectWithConverterForEnum() - coerce in select(): {}", result6);

        // 7. Считывание списка символов и конвертирование в тип UserType используя свой USER_TYPE_ENUM_CONVERTER
        //    в методе convert().
        List resultList7 = dslContext
                .select(AA_USER.USER_TYPE.convert(USER_TYPE_ENUM_CONVERTER))
                .from(AA_USER)
                .limit(3)
                .fetchInto(UserType.class);
        LOG.debug("selectWithConverterForEnum() - converter.convert() in select(): {}", resultList7);

        // 8. Считывание списка символов и конвертирование в тип UserType используя лямбда функцию для конвертации в
        //    методе convertFrom().
        List resultList8 = dslContext
                .select(AA_USER.USER_TYPE.convertFrom(t -> USER_TYPE_ENUM_CONVERTER.from(t)))
                .from(AA_USER)
                .limit(3)
                .fetchInto(UserType.class);
        LOG.debug("selectWithConverterForYearMonthType() - lambda convertFrom(converter.from()) in select(): {}", resultList8);
    }

    /**
     * <pre>
     * Примеры конвертации в INSERT операторах своего типа YearMonth в LocalDateTime (В БД timestamp), используя свой
     * конвертор YEARMONTH_TO_LOCALDATETIME_CONVERTER, а так же созданный на его базе свой тип данных YEARMONTH (на базе
     * своего YearMonth).
     *
     * SQL:
     * insert into `aa_user` (`UNAME`, `PROFILE_ID`, `USER_TYPE`, `STATE`, `AUTH_TYPE`, `SHORT_NAME`, `CREATED_DT`, `LAST_PSWD_DT`, `LOGIN_ATTEMP`)
     * values (?, ?, ?, ?, ?, ?, ?, ?, ?)
     * </pre>
     */
    @Transactional
    public void insertWithConverterForYearMonthType() {
        // Prepare.
        dslContext.delete(AA_USER).where(AA_USER.UNAME.like("user_conv_0%")).execute();

        // 1. Вставка с конвертированием типа YearMonth в LocalDateTime через YEAR_MONTH_TO_LOCAL_DATE_TIME_CONVERTER.
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID, AA_USER.USER_TYPE, AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME,
                         AA_USER.CREATED_DT.convert(YEARMONTH_TO_LOCALDATETIME_CONVERTER),
                         AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
                .values("user_conv_01", 1L, "S", UserState.ENABLED, "I", "user_conv_01",
                        YearMonth.of(2022, 3),
                        LocalDateTime.now(), 0)
                .execute();
        LOG.debug("insertWithConverterForYearMonthType() - Use convert(converter) for insert");

        // 2. Вставка с конвертированием типа YearMonth в LocalDateTime через метод convert() использующий лямбды с
        //    методами конвертера YEAR_MONTH_TO_LOCAL_DATE_TIME_CONVERTER.from() и to().
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID, AA_USER.USER_TYPE, AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME,
                        AA_USER.CREATED_DT.convert(
                                YearMonth.class,
                                t -> YEARMONTH_TO_LOCALDATETIME_CONVERTER.from(t), // Не используемая часть
                                u -> YEARMONTH_TO_LOCALDATETIME_CONVERTER.to(u)),
                        AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
                .values("user_conv_02", 1L, "S", UserState.ENABLED, "I", "user_conv_02",
                        YearMonth.of(2022, 3),
                        LocalDateTime.now(), 0)
                .execute();
        LOG.debug("insertWithConverterForYearMonthType() - Use convert(lambdas) for insert");

        // 3. Вставка с конвертированием типа YearMonth в LocalDateTime через метод convert() использующий лямбды для
        //    методов конвертирования.
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID, AA_USER.USER_TYPE, AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME,
                         AA_USER.CREATED_DT.convert(
                               YearMonth.class,
                               t -> null,   // Не используемая часть
                               u -> {
                                   if (u != null) {
                                       return u.atDay(1)
                                               .atStartOfDay(ZoneId.systemDefault())
                                               .toLocalDateTime();
                                   }
                                   return null;
                               }),
                       AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
               .values("user_conv_03", 1L, "S", UserState.ENABLED, "I", "user_conv_03",
                       YearMonth.of(2022, 3),
                       LocalDateTime.now(), 0)
               .execute();
        LOG.debug("insertWithConverterForYearMonthType() - Use convert() for insert");

        // 4. Вставка с конвертированием типа YearMonth в LocalDateTime через метод использующий лямбду с методом
        //    конвертера YEAR_MONTH_TO_LOCAL_DATE_TIME_CONVERTER.to().
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID, AA_USER.USER_TYPE, AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME,
                        AA_USER.CREATED_DT.convertTo(YearMonth.class, u -> YEARMONTH_TO_LOCALDATETIME_CONVERTER.to(u)),
                        AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
                .values("user_conv_04", 1L, "S", UserState.ENABLED, "I", "user_conv_04",
                        YearMonth.of(2022, 3),
                        LocalDateTime.now(), 0)
                .execute();
        LOG.debug("insertWithConverterForYearMonthType() - Use convertTo(converter.to(lambda)) for insert");

        // 5. Вставка с конвертированием типа YearMonth в LocalDateTime через тип данных YEARMONTH в coerce().
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID, AA_USER.USER_TYPE, AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME,
                        AA_USER.CREATED_DT.coerce(YEARMONTH),
                        AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
                .values("user_conv_05", 1L, "S", UserState.ENABLED, "I", "user_conv_05",
                        YearMonth.of(2022, 3),
                        LocalDateTime.now(), 0)
                .execute();
        LOG.debug("insertWithConverterForYearMonthType() - Use coerce() for insert");
    }

    /**
     * <pre>
     * Примеры конвертации в INSERT операторах Enum в String (В БД char), используя конвертор USER_TYPE_ENUM_CONVERTER,
     * а так же созданный на его базе USERTYPE (на базе своего UserType).
     *
     * SQL:
     * insert into `aa_user` (`UNAME`, `PROFILE_ID`, `USER_TYPE`, `STATE`, `AUTH_TYPE`, `SHORT_NAME`, `CREATED_DT`, `LAST_PSWD_DT`, `LOGIN_ATTEMP`)
     * values (?, ?, ?, ?, ?, ?, ?, ?, ?)
     * </pre>
     */
    @Transactional
    public void insertWithConverterForEnum() {
        // Prepare.
        dslContext.delete(AA_USER).where(AA_USER.UNAME.like("user_conv_1%")).execute();

        // 1. Вставка с конвертированием типа UserType в String через USER_TYPE_ENUM_CONVERTER.
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID,
                        AA_USER.USER_TYPE.convert(USER_TYPE_ENUM_CONVERTER),
                        AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME, AA_USER.CREATED_DT, AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
                .values("user_conv_11", 1L,
                        UserType.SYSTEM,
                        UserState.ENABLED, "I", "user_conv_11", LocalDateTime.now(), LocalDateTime.now(), 0)
                .execute();
        LOG.debug("insertWithConverterForEnum() - Use convert(converter) for insert");

        // 2. Вставка с конвертированием UserType в String через метод convert(), использующий лямбды с
        //    методами конвертера USER_TYPE_ENUM_CONVERTER.from() и to().
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID,
                         AA_USER.USER_TYPE.convert(
                                 UserType.class,
                                 t -> USER_TYPE_ENUM_CONVERTER.from(t), // Не используемая часть
                                 u -> USER_TYPE_ENUM_CONVERTER.to(u)),
                         AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME,AA_USER.CREATED_DT,AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
                .values("user_conv_12", 1L,
                        UserType.SYSTEM,
                        UserState.ENABLED, "I", "user_conv_12", LocalDateTime.now(), LocalDateTime.now(), 0)
                .execute();
        LOG.debug("insertWithConverterForEnum() - Use convert(lambdas) for insert");

        // 4. Вставка с конвертированием типа UserType в String через метод использующий лямбду с методом
        //    конвертера USER_TYPE_ENUM_CONVERTER.to().
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID,
                         AA_USER.USER_TYPE.convertTo(UserType.class, u -> USER_TYPE_ENUM_CONVERTER.to(u)),
                         AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME, AA_USER.CREATED_DT, AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
                .values("user_conv_14", 1L,
                        UserType.SYSTEM,
                        UserState.ENABLED, "I", "user_conv_14", LocalDateTime.now(), LocalDateTime.now(), 0)
                .execute();
        LOG.debug("insertWithConverterForEnum() - Use convertTo(converter.to(lambda)) for insert");

        // 5. Вставка с конвертированием типа UserType в String через тип данных YEARMONTH в coerce().
        dslContext
                .insertInto(AA_USER)
                .columns(AA_USER.UNAME, AA_USER.PROFILE_ID,
                         AA_USER.USER_TYPE.coerce(USERTYPE),
                         AA_USER.STATE, AA_USER.AUTH_TYPE, AA_USER.SHORT_NAME, AA_USER.CREATED_DT, AA_USER.LAST_PSWD_DT, AA_USER.LOGIN_ATTEMP)
                .values("user_conv_15", 1L,
                        UserType.SYSTEM,
                        UserState.ENABLED, "I", "user_conv_15", LocalDateTime.now(), LocalDateTime.now(), 0)
                .execute();
        LOG.debug("insertWithConverterForEnum() - Use coerce() for insert");
    }

    /**
     * <pre>
     * Пример конвертации поля <i>char</i> в <i>Enum</i>, которая используется JOOQ по умолчанию.
     * Она настраивается в файле goals/db/db-modules/aa-schema-gen/pom.xm
     *     <forcedType>
     *         <userType>ua.mai.zyme.db.aa.schema.UserState</userType>
     *         <converter>ua.mai.zyme.db.aa.schema.UserStateConverter</converter>
     *         <includeExpression>.*(AA_USER\.STATE)</includeExpression>
     *     </forcedType>
     * , где:
     *   <i>UserState</i> - <i>Enum</i>.
     *   </i>UserStateConverter - конвертер <i>String</i> в <i>Enum</i>.
     * </pre>
     *
     * SQL:
     * select `aa_user`.`UNAME`, `aa_user`.`STATE` from `aa_user` limit 3
     */
    @Transactional
    public void selectForcedEnum() {

        org.jooq.Record record1 = dslContext
                .select(AA_USER.UNAME,
                        AA_USER.STATE)
                .from(AA_USER)
                .limit(3)
                .fetchAny();
        UserState userState = record1.getValue(AA_USER.STATE.getName(), AA_USER.STATE.getType());
        LOG.debug("selectForcedEnum() - forced Convert in fetch(): userState = {}", userState);
    }

    /**
     * <pre>
     * Пример считывания из таблицы реального значения поля, для которого задана в JOOQ по умолчанию конвертация
     * <i>char</i> в <i>Enum</i>.
     *
     * SQL:
     * select `aa_user`.`UNAME`, `aa_user`.`STATE`, STATE as `STATE_ORIG` from `aa_user` limit 3
     */
    @Transactional
    public void selectForcedEnumFieldAsChar() {

        org.jooq.Record record1 = dslContext
                .select(AA_USER.UNAME,
                        AA_USER.STATE, // символ автоматически конвертируется в UserState.
                        field(AA_USER.STATE.getName()).as("STATE_ORIG") // считывание самого символа из поля таблицы
                       )
                .from(AA_USER)
                .limit(3)
                .fetchAny();
        UserState userState = record1.getValue(AA_USER.STATE.getName(), AA_USER.STATE.getType());
        String userStateOrig = record1.getValue("STATE_ORIG", String.class);
        LOG.debug("selectForcedEnumFieldAsChar() - select forced convert field as original: userState={}, userStateOrig={}",
                userState, userStateOrig);
    }

}
