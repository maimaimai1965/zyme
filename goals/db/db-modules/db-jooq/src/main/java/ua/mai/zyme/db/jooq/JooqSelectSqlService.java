package ua.mai.zyme.db.jooq;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.aa.schema.tables.AaTrans;
import ua.mai.zyme.db.aa.schema.tables.records.AaTransRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ua.mai.zyme.db.aa.schema.tables.AaRole.AA_ROLE;
import static ua.mai.zyme.db.aa.schema.tables.AaTrans.AA_TRANS;
import static ua.mai.zyme.db.aa.schema.tables.AaUser.AA_USER;
import static ua.mai.zyme.db.aa.schema.tables.AaUserRole.AA_USER_ROLE;

/**
 * <pre>
 * Примеры SELECT операций в JOOQ.
 *
 *   <i>selectFetchOne()</i> - чтение одной записи используя <i>fetchOne()</i>.
 *   <i>selectFetchOptional()</i> - чтение одной записи используя <i>fetchOptional()</i>.
 *   <i>selectFetchInTableRecord()</i> - чтение записей типа <i>TableRecord</i> и <i>fetch()</i>, <i>fetchArray()</i>,
 *       <i>fetchAny()</i>.
 *   <i>selectFetchInAnotherClassWithCommonFields()</i> - чтение записей, используя <i>fetch()</i> и <i>fetchAny()</i>,
 *       которые сразу приводятся к объектам другого класса, который содержит некоторые поля из полей запроса.
 *   <i>selectFetchValuesInList()</i> - чтение, используя <i>fetchValues(TableField)</i>, заданного поля всех записей
 *       таблицы, и помещение их в список.
 *   <i>selectComplicatedSql()</i> - запрос использует функции <i>val(), inline(), nvl(), concat(), lower(), upper()</i>.
 *   <i>selectWithAsterisk()</i> - запрос использует метод <i>asterisk()</i> - в SQL - это '*'.
 *   <i>selectWithLimitOffset()</i> - Pagination - запрос использует методы <i>limit()</i> и <i>offset()</i>.
 *   <i>selectWithInField()</i> - запрос использует конструкцию IN (для одного поля) в секции WHERE - in(select...) и
 *       in(val(), inline()).
 *   <i>selectWithInRow()</i> - запрос использует конструкцию IN (для нескольких полей) в секции WHERE - <i>row().in(select...)</i>.
 *   <i>selectWithCompare()</i> - запрос использует операторы сравнения (=, !=, >, >=, <, <=, IS NULL, IS NOT NULL).
 *   <i>selectWithCast()</i> - преобразование типа считываемых полей. Запрос использует оператор <i>cast()</i> -
 *       преобразование осуществляется в SQL.
 *   <i>selectWithCoerce()</i> - преобразование типа считываемых полей. Запрос использует оператор <i>coerce()</i> -
 *       преобразование осуществляется в Java.
 *   <i>selectWithSubqueryField()</i> - в запросе используется подзапрос в получаемом поле. В подзапросе используется
 *       таже тоблица, что и в основном запросе.
 *   <i>selectFromSameTable()</i> - в запросе осуществляется <i>join</i> к <b>той же таблице</b>, которая является основной.
 *   <i>selectDistinct()</i> - запрос использует <i>selectDistinct()</i>.
 * </pre>
 */
@Service
@Transactional
public class JooqSelectSqlService {

    private static final Logger LOG = LoggerFactory.getLogger(JooqSelectSqlService.class);

    private final DSLContext dslContext;  // Дефолтный dslContext (БД zyme)

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String schema = "zyme";

    public JooqSelectSqlService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }

    /**
     * <pre>
     * Чтение записи используя <i>selectFrom()...fetchOne()</i>:
     * a) SELECT возвращает <b>одну</b> запись - <i>queryOneRecord</i>:
     *    1. <i>fetchOne()</i> - получение объекта <i>RecordTable</i>, поля которого соответствуют полям SELECT.
     *    2. <i>fetchOne().into(поля)</i> - получение объекта <i>Record2</i>, поля которого содержат только заданные поля
     *       в <i>into()</i>.
     *    3. <i>fetchOne().into(AnotherTrans.class)</i> - получение другого объекта <i>AnotherTrans</i>, поля которого
     *       содержат только некоторые поля из SELECT. У созданного объекта заполняются только те поля, которые
     *       возвращает SELECT.
     *    4. <i>fetchOneInto(AnotherTrans.class)</i> - получение другого объекта <i>AnotherTrans</i>, поля которого
     *       содержат только некоторые поля из SELECT. У созданного объекта заполняются только те поля, которые
     *       возвращает SELECT.
     *    5. <i>fetchOne().into(поля)</i> - получение объекта объявленного как <i><b>var</b></i>, поля которого
     *       содержат только заданные поля в <i>into()</i>.
     * b) SELECT не возвращает <b>ни одной записи</b> - <i>queryZeroRecord</i>:
     *    1. <i>fetchOne()</i> - возвращается <i><b>null</b></i> вместо объекта <i>RecordTable</i>.
     *    2. <i>fetchOne().into(поля)</i> - возвращается <i><b>null</b></i> вместо объекта <i>Record2</i>.
     *    3. <i>fetchOne().into(AnotherTrans.class)</i> - вызывается <b>исключение</b> <i>NullPointerException</i> вместо
     *       объекта <i>AnotherTrans</i>.
     *    4. <i>fetchOneInto(AnotherTrans.class)</i> - возвращается <i><b>null</b></i> вместо объекта <i>AnotherTrans</i>.
     * c) SELECT возвращает <b>более одной</b> записи - <i>queryTwoRecord</i>:
     *    1. <i>fetchOne()</i> - вызывается <b>исключение</b> <i>TooManyRowsException</i>.
     *    2. <i>fetchOne().into(поля)</i> - вызывается <b>исключение</b> <i>TooManyRowsException</i>.
     *
     * SQL:
     *   select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     *   from `aa_trans`
     *   limit 1  - a) queryOneRecord
     *   limit 0  - b) queryZeroRecord
     *   limit 2  - c) queryTwoRecord
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectFetchOne() {
        // a)
        SelectQuery<AaTransRecord> queryOneRecord = dslContext
                .selectFrom(AA_TRANS)
                .limit(1)
                .getQuery();

        // a.1.
        AaTransRecord recordA1 =
                queryOneRecord.fetchOne();
        LOG.debug("selectFetchOne fetchOne() into Record [{}]: transId={}; transName='{}'; note='{}'; createdDt='{}'", schema,
                recordA1.getTransId(),
                recordA1.getTransName(),
                recordA1.getNote(),
                recordA1.getCreatedDt());
        // a.2.
        Record2 recordA2 =
                queryOneRecord.fetchOne().into(AA_TRANS.TRANS_ID, AA_TRANS.TRANS_NAME);
        LOG.debug("selectFetchOne fetchOne() into Record2 [{}]: transId={}; transName='{}'", schema,
                recordA2.get(AA_TRANS.TRANS_ID.getName()),
                recordA2.get(AA_TRANS.TRANS_NAME.getName()));
        // a.3.
        AnotherTrans anotherTransA3 =
                queryOneRecord.fetchOne().into(AnotherTrans.class);
        LOG.debug("selectFetchOne fetchOne().into(.class) into AnotherTrans [{}]: transId={}; transName='{}'", schema,
                anotherTransA3.getTransId(),
                anotherTransA3.getTransName());
        // a.4.
        AnotherTrans anotherTransA4 =
                queryOneRecord.fetchOneInto(AnotherTrans.class);
        LOG.debug("selectFetchOne fetchOneInto(.class) into AnotherTrans [{}]: transId={}; transName='{}'", schema,
                anotherTransA4.getTransId(),
                anotherTransA4.getTransName());
        // a.5.
        var varA5 =
                queryOneRecord.fetchOne().into(AA_TRANS.TRANS_ID, AA_TRANS.TRANS_NAME);
        LOG.debug("selectFetchOne fetchOne() into varA5 [{}]: transId={}; transName='{}'", schema,
                varA5.get(AA_TRANS.TRANS_ID.getName()),
                varA5.get(AA_TRANS.TRANS_NAME.getName()));

        // b)
        SelectQuery queryZeroRecord = dslContext
                .selectFrom(AA_TRANS)
                .limit(0)
                .getQuery();
        // b.1.
        AaTransRecord recordB1 =
                (AaTransRecord) queryZeroRecord.fetchOne();
        LOG.debug("selectFetchOne fetchOne() for 0 records [{}] return {}", schema, recordB1);
        // b.2.
        Record2 recordB2 =
                queryOneRecord.fetchOne().into(AA_TRANS.TRANS_ID, AA_TRANS.TRANS_NAME);
        LOG.debug("selectFetchOne fetchOne().into() for 0 records [{}] return {}", schema, recordB2);
        // b.3.
        try {
            AnotherTrans anotherTransB3 =
                    queryZeroRecord.fetchOne().into(AnotherTrans.class);
            LOG.debug("selectFetchOne fetchOne().into() into AnotherTransfor for 0 records [{}]: {}", schema, anotherTransB3);
        } catch (NullPointerException e) {
            LOG.debug("selectFetchOne fetchOne().into() for 0 records [{}] exception: {}", schema, e.getMessage());
        }
        // b.4.
        AnotherTrans anotherTransB4 =
                (AnotherTrans)queryZeroRecord.fetchOneInto(AnotherTrans.class);
        LOG.debug("selectFetchOne fetchOneInto() into AnotherTrans for 0 records [{}]: {}", schema, anotherTransB4);

        // c)
        SelectQuery queryTwoRecord = dslContext
                .selectFrom(AA_TRANS)
                .limit(2)
                .getQuery();

        // c.1.
        try {
            AaTransRecord recordC1 =
                    (AaTransRecord) queryTwoRecord.fetchOne();
        } catch (TooManyRowsException e) {
            LOG.debug("selectFetchOne fetchOne() for 2 records [{}] exception: {}", schema, e.getMessage());
        }
        // c.2.
        try {
            Record2 recordC2 =
                    queryTwoRecord.fetchOne().into(AA_TRANS.TRANS_ID, AA_TRANS.TRANS_NAME);
        } catch (TooManyRowsException e) {
            LOG.debug("selectFetchOne fetchOne().into() for 2 records [{}] exception: {}", schema, e.getMessage());
        }
    }

    /**
     * <pre>
     * Чтение одной записи используя <i>fetchOptional()</i>.
     * a) SELECT возвращает одну запись - <i>queryOneRecord</i>:
     *    1. <i>fetchOptional()</i> - получение объекта <i>Optional{RecordTable}</i> поля которого соответствуют полям SELECT.
     *    2. <i>fetchOptionalInto(Record2.class)</i> - получение объекта <i>Optional{Record2}</i> поля которого содержат
     *       только некоторые поля SELECT.
     * b) SELECT не возвращает ни одной записи - <i>queryZeroRecord</i>:
     *    1. <i>fetchOptional()</i> - возвращается <i>Optional.empty</i> вместо объекта <i>Optional{RecordTable}</i>.
     *    2. <i>fetchOptionalInto(Record2.class)</i> - возвращается <i>Optional.empty</i> вместо объекта <i>Optional{Record2}</i>.
     * c) SELECT возвращает более одной записи - <i>queryTwoRecord</i>:
     *    1. <i>fetchOptional()</i> - вызывается <b>исключение</b> <i>TooManyRowsException</i> вместо возврата объекта <i>Optional{RecordTable}</i>.
     *    2. <i>fetchOptionalInto(Record2.class)</i> - вызывается <b>исключение</b> <i>TooManyRowsException</i> вместо
     *       возврата объекта <i>Optional{Record2}</i>.
     *
     * SQL:
     *   select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     *   from `aa_trans`
     *   limit 1
     *  </pre>
     */
    @Transactional(readOnly = true)
    public void selectFetchOptional() {
        // a)
        SelectQuery queryOneRecord = dslContext
                .selectFrom(AA_TRANS)
                .limit(1)
                .getQuery();

        // a.1.
        Optional<AaTransRecord> optionalA1 =
                queryOneRecord.fetchOptional();
        LOG.debug("selectFetchOptional fetchOptional() into Optional<Record> [{}]: transId={}; transName='{}'; note='{}'; createdDt='{}'", schema,
                optionalA1.get().getTransId(),
                optionalA1.get().getTransName(),
                optionalA1.get().getNote(),
                optionalA1.get().getCreatedDt());

        // a.2.
        Optional<Record2> optionalA2 =
                queryOneRecord.fetchOptionalInto(Record2.class);
        LOG.debug("selectFetchOptional fetchOptionalInto() into Optional<Record2> [{}]: transId={}; transName='{}'", schema,
                optionalA2.get().get(AA_TRANS.TRANS_ID.getName()),
                optionalA2.get().get(AA_TRANS.TRANS_NAME.getName()));

        // b)
        SelectQuery queryZeroRecord = dslContext
                .selectFrom(AA_TRANS)
                .limit(0)
                .getQuery();
        // b.1.
        Optional<AaTransRecord> optionalB1 =
                queryZeroRecord.fetchOptional();
        LOG.debug("selectFetchOptional fetchOptional() for 0 records [{}] return {}}", schema, optionalB1);

        // b.2.
        Optional<Record2> optionalB2 =
                queryZeroRecord.fetchOptionalInto(Record2.class);
        LOG.debug("selectFetchOptional fetchOptionalInto().into() for 0 records [{}] return {}}", schema, optionalB2);

        // c)
        SelectQuery queryTwoRecord = dslContext
                .selectFrom(AA_TRANS)
                .limit(2)
                .getQuery();

        // c.1.
        try {
            Optional<AaTransRecord> optionalC1 =
                    queryOneRecord.fetchOptional();
        } catch (TooManyRowsException e) {
            LOG.debug("selectFetchOptional fetchOptional() for 2 records [{}] exception: {}", schema, e.getMessage());
        }
        // c.2.
        try {
            Optional<Record2> optionalC2 =
                    queryOneRecord.fetchOptionalInto(Record2.class);
        } catch (TooManyRowsException e) {
            LOG.debug("selectFetchOptional fetchOptionalInto() for 2 records [{}] exception: {}", schema, e.getMessage());
        }
    }

    /**
     * <pre>
     * Чтение нескольких записей с использованием <i>TableRecord</i> используя <i>fetch()</i>, <i>fetchArray()</i>,
     * <i>fetchAny()</i>.
     * a) SELECT возвращает одну или более записей - <i>queryOneOrMoreRecords</i>:
     *   1. <i>fetch()</i> - получение объекта <i>Result</i>, содержащего в списке объекты типа <i>TableRecord</i>.
     *   2. <i>fetch().into(TableRecord.class)</i> - получение списка объектов типа <i>TableRecord</i>.
     *   3. <i>fetchArray()</i> - получение массива объектов типа <i>TableRecord</i>.
     *   4. <i>fetchAny()</i> - получение любой записи типа <i>TableRecord</i>.
     * b) SELECT не возвращает ни одной записи - <i>queryNoRecords</i>:
     *   1. <i>fetch()</i> - при получении записей в списке объекта <i>Result</i> получаем пустой список.
     *   2. <i>fetch().into(TableRecord.class)</i> - получаем <b>пустой список</b>.
     *   3. <i>fetchArray()</i> - при получении записей в массиве получаем <b>пустой массив</b>.
     *   4. <i>fetchAny()</i> - при получении любой записи получаем <i><b>null</b></i>.
     *
     * SQL:
     *   select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     *   from `aa_trans`
     *   limit 10  - a) <i>queryOneOrMoreRecords</i>
     *   limit  0  - b) <i>queryNoRecords</i>
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectFetchInTableRecord() {
        // a)
        SelectQuery<AaTransRecord> queryOneOrMoreRecords = dslContext
                .selectFrom(AA_TRANS)
                .where()
                .limit(10)
                .getQuery();
        // a.1.
        Result<AaTransRecord> resultA1 = queryOneOrMoreRecords.fetch();
        List<AaTransRecord> listA1 = resultA1.stream().toList();
        LOG.debug("selectFetchInTableRecord fetch() into Result list [{}]: count={}", schema, resultA1.stream().count());

        // a.2.
        List<AaTransRecord> listA2 = queryOneOrMoreRecords.fetch().into(AaTransRecord.class);
        LOG.debug("selectFetchInTableRecord fetch() into list [{}]: size={}", schema, listA2.size());

        // a.3.
        AaTransRecord[] arA3 = (AaTransRecord[]) queryOneOrMoreRecords.fetchArray();
        LOG.debug("selectFetchInTableRecord fetchArray() into array [{}]: length={}", schema, arA3.length);

        // a.4.
        AaTransRecord recordA4 = (AaTransRecord) queryOneOrMoreRecords.fetchAny();
        LOG.debug("selectFetchInTableRecord fetchAny() record [{}]: count=1", schema);

        // b)
        SelectQuery queryNoRecords = dslContext
                .selectFrom(AA_TRANS)
                .where()
                .limit(0)
                .getQuery();
        // b.1.
        Result<AaTransRecord> resultB1 = queryNoRecords.fetch();
        List<AaTransRecord> listB1 = resultA1.stream().toList();
        LOG.debug("selectFetchInTableRecord fetch() into Result list [{}]: count={}", schema, resultB1.stream().count());

        // b.2.
        List<AaTransRecord> listB2 = queryNoRecords.fetch().into(AaTransRecord.class);
        LOG.debug("selectFetchInTableRecord fetch() into list [{}]: size={}", schema, listB2.size());

        // b.3.
        AaTransRecord[] arB3 = (AaTransRecord[]) queryNoRecords.fetchArray();
        LOG.debug("selectFetchInTableRecord fetchArray() into array [{}]: length={}", schema, arB3.length);

        // b.4.
        AaTransRecord recordB4 = (AaTransRecord) queryNoRecords.fetchAny();
        LOG.debug("selectFetchInTableRecord fetchAny() record [{}]: {}", schema, recordB4);
    }

    /**
     * <pre>
     * Чтение нескольких записей, используя <i>fetch()</i>, и одной записи, используя <i>fetchAny()</i>, которые сразу
     * приводятся к объектам другого класса, который содержит некоторые поля из полей запроса.
     * a) SELECT возвращает <b>одну или более</b> записей - <i>queryOneOrMoreRecords</i>:
     *   1. <i>fetch().into(Another.class)</i> - получаем список.
     *   2. <i>fetchAny().into(Another.class)</i> - получаем любую запись.
     * b) SELECT не возвращает <b>ни одной записи</b> - <i>queryNoRecords</i>:
     *   1. <i>fetch().into(Another.class)</i> - получаем <b>пустой список</b>.
     *   2. <i>fetchAny().into(Another.class)</i> - получаем <b>исключение</b> <i>NullPointerException</i>.
     *
     * В запросе есть поля, которых нет в другом классе. В классе есть поля, которых нет в запросе. При выполнении
     * заполняются только те поля, которые есть в запросе и в другом классе.
     *
     * SQL:
     *   select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     *   from `aa_trans`
     *   limit 10  - a) <i>queryOneOrMoreRecords</i>
     *   limit  0  - b) <i>queryNoRecords</i>
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectFetchInAnotherClassWithCommonFields() {
        // a) SELECT возвращает одну или более записей
        SelectQuery queryOneOrMoreRecords = dslContext
                .selectFrom(AA_TRANS)
                .where()
                .limit(10)
                .getQuery();
        // a.1.
        List<AnotherTrans> listA1 = queryOneOrMoreRecords.fetch().into(AnotherTrans.class);
        LOG.debug("selectFetchInAnotherClass fetch() [{}]: size={}", schema, listA1.size());

        // a.2.
        AnotherTrans recordA2 = (AnotherTrans) queryOneOrMoreRecords.fetchAny().into(AnotherTrans.class);
        LOG.debug("selectFetchInAnotherClass fetchAny() record [{}]: count=1", schema);

        // b) SELECT не возвращает ни одной записи
        SelectQuery queryNoRecords = dslContext
                .selectFrom(AA_TRANS)
                .where()
                .limit(0)
                .getQuery();
        // b.1.
        List<AnotherTrans> listB1 = queryNoRecords.fetch().into(AnotherTrans.class);
        LOG.debug("selectFetchInAnotherClass fetch().into() for 0 records [{}]: size={}", schema, listB1.size());

        // b.2.
        try {
            AnotherTrans recordB2 = (AnotherTrans) queryNoRecords.fetchAny().into(AnotherTrans.class);
        } catch (Exception e) {
            LOG.debug("selectFetchInAnotherClass fetchAny().into() for 0 records [{}] exception: {}", schema, e);
        }
    }

    /**
     * <pre>
     * Чтение используя fetchValues(TableField) заданного поля для всех записей таблицы, и помещение их в список.
     *
     * SQL:
     *   select `aa_trans`.`TRANS_NAME` from `aa_trans`
     * </pre>
     */
    @Transactional(readOnly = true)
    public List<String> selectFetchValuesInList() {
        List<String> list = dslContext.fetchValues(AA_TRANS.TRANS_NAME);
        LOG.debug("selectFetchValuesInList fetchValues() into list [{}]: count={}", schema, list.size());
        return list;
    }


    /**
     * <pre>
     * Запрос использует функции val(), inline(), nvl(), concat(), lower(), upper():
     *   <i>DSL.val()</i> - параметр в запросе;
     *   <i>DSL.inline()</i> - константа в запросе;
     *   <i>DSL.nvl()</i> - присвоение значения, если значение равно null (в SQL используется функция MySQL <i>ifnull()</i>);
     *   <i>DSL.castNull()</i> - использование присвоение значения null в запросе (в SQL используется оператор <i>cast()</i>);
     *   <i>DSL.coalesce()</i> - присвоение значения для null (похож на <i>nvl()</i>);
     *   <i>DSL.concat()</i> - объединение строк;
     *   <i>DSL.lower()</i> - привод строки к нижнему регистру;
     *   <i>DSL.upper()</i> - привод строки к верхнему регистру;
     *
     * SQL:
     * select ifnull(`aa_trans`.`TRANS_NAME`, 'UNDEF')                                    as `TRANS_NAME`,
     *        coalesce(`aa_trans`.`TRANS_NAME`, 'UNDEF')                                  as `TRANS_NAME_2`,
     *        `aa_trans`.`CREATED_DT`,
     *        concat(lower(`aa_trans`.`TRANS_NAME`), ' ', upper(`aa_trans`.`TRANS_NAME`)) as `DOUBLE_TRANS_NAME`,
     *        10                                                                          as `INT_FIELD`,
     *        null                                                                        as `NULL_AS_STRING`,
     *        cast(null as signed)                                                        as `NULL_AS_INTEGER`,
     *        cast(`aa_trans`.`CREATED_DT` as date)                                       as `CREATED_DT_AS_LOCAL_DATE`,
     *        (((10 - 2) * (7 / 3)) / 2)                                                  as `CALC_VALUE`
     * from `aa_trans`
     * where (`aa_trans`.`TRANS_NAME` <> 'XXXXXX'
     *   and `aa_trans`.`CREATED_DT` >= {ts '2000-01-01 00:00:00.0'})
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectComplicatedSql() {
        int intParam = 10;
        String transNameParam = "XXXXXX";
        LocalDateTime localDateTimeParam = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);

        var listA1 = dslContext
                .select(DSL.nvl(AA_TRANS.TRANS_NAME, "UNDEF").as(AA_TRANS.TRANS_NAME.getName()),
                        DSL.coalesce(AA_TRANS.TRANS_NAME, "UNDEF").as(AA_TRANS.TRANS_NAME.getName() + "_2"),
                        AA_TRANS.CREATED_DT,
                        DSL.concat(DSL.lower(AA_TRANS.TRANS_NAME), DSL.inline(" "), DSL.upper(AA_TRANS.TRANS_NAME)).as("DOUBLE_TRANS_NAME"),
                        DSL.val(intParam).as("INT_FIELD"),
                        DSL.inline((String) null).as("NULL_AS_STRING"),
                        DSL.castNull(Integer.class).as("NULL_AS_INTEGER"),
                        AA_TRANS.CREATED_DT.cast(LocalDate.class).as("CREATED_DT_AS_LOCAL_DATE"),
                        DSL.val(10).sub(2).mul(DSL.val(7).div(3)).div(2).as("CALC_VALUE")
                        )
                .from(AA_TRANS)
                .where(AA_TRANS.TRANS_NAME.notEqual(DSL.inline(transNameParam))
                  .and(AA_TRANS.CREATED_DT.greaterOrEqual(DSL.inline(localDateTimeParam)))
                 )
                .limit(2)
                .fetch();
        for (var record: listA1) {
            String transName = record.get("TRANS_NAME", String.class);
            LocalDateTime createdDt = record.get("CREATED_DT", LocalDateTime.class);
            String doubleTransName = record.get("DOUBLE_TRANS_NAME", String.class);
            Integer intField = record.get("INT_FIELD", Integer.class);
            String nullAsString = record.get("NULL_AS_STRING", String.class);
            LocalDate createdDtAsLocalDate = record.get("CREATED_DT_AS_LOCAL_DATE", LocalDate.class);
            Double calcValue = record.get("CALC_VALUE", Double.class);
        }
        LOG.debug("selectComplicatedSql() [{}]: size={}", schema, listA1.size());
    }

    /**
     * <pre>
     * Запрос использует метод <i>asterisk()</i> - в SQL - это '*'.
     * 1. Вычитываются все поля таблицы.
     *    SQL:
     *    select * from `aa_trans` limit 2
     *
     * 2. Вычитываются все поля таблицы за исключением указанных - формируется список вычитываемых колонок.
     *    SQL:
     *    select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`
     *    from `aa_trans`
     *    limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithAsterisk() {

        // 1.
        var list1 = dslContext
                .select(DSL.asterisk())
                .from(AA_TRANS)
                .limit(2)
                .fetch();
        LOG.debug("selectWithAsterisk() [{}] 1: size={}", schema, list1.size());

        // 2.
        var list2 = dslContext
                .select(DSL.asterisk().except(AA_TRANS.NOTE, AA_TRANS.CREATED_DT))
                .from(AA_TRANS)
                .limit(2)
                .fetch();
        LOG.debug("selectWithAsterisk() [{}] 2: size={}", schema, list2.size());
    }

    /**
     * <pre>
     * 1
     *
     * SQL:
     * select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     * from `aa_trans`
     * limit 10 offset 3
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithLimitOffset() {

        // 1. Раздельное задание limit и offset.
        var list1 = dslContext
                .selectFrom(AA_TRANS)
                .offset(3)
                .limit(10)
                .fetch();
        LOG.debug("selectWithLimitOffset() [{}] 1: size={}", schema, list1.size());

        // 2.
        var list2 = dslContext
                .select(DSL.asterisk().except(AA_TRANS.NOTE, AA_TRANS.CREATED_DT))
                .from(AA_TRANS)
                .limit(3, 10)
                .fetch();
        LOG.debug("selectWithLimitOffset() [{}] 2: size={}", schema, list2.size());
    }

    /**
     * <pre>
     * Запрос использует конструкцию IN (для одного поля) в секции WHERE - in(select...) и in(val(), inline()).
     *
     * SQL:
     * select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     * from `aa_trans`
     * where (`aa_trans`.`TRANS_ID` in (select `aa_trans`.`TRANS_ID`
     *                                  from `aa_trans`
     *                                  where (lower(`aa_trans`.`TRANS_NAME`) like lower('%S%') and
     *                                         `aa_trans`.`TRANS_NAME` not like '%QQ%'))
     *     or `aa_trans`.`TRANS_ID` in (10, 111)
     *     or `aa_trans`.`TRANS_ID` not in (222, 333))
     * order by `aa_trans`.`TRANS_NAME` desc, `aa_trans`.`TRANS_ID` asc
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithInField() {
        int intParam = 2;

        var listA1 = dslContext
                .selectFrom(AA_TRANS)
                .where(AA_TRANS.TRANS_ID.in(
                            dslContext.select(AA_TRANS.TRANS_ID).from(AA_TRANS)
                                      .where(AA_TRANS.TRANS_NAME.likeIgnoreCase("%S%")
                                        .and(AA_TRANS.TRANS_NAME.notLike("%QQ%"))))
                       .or(AA_TRANS.TRANS_ID.in(DSL.val(intParam), DSL.inline(111)))
                       .or(AA_TRANS.TRANS_ID.notIn(DSL.inline(222), DSL.inline(333))))
                .orderBy(AA_TRANS.TRANS_NAME.desc(), AA_TRANS.TRANS_ID.asc())
                .limit(2)
                .fetch();
        LOG.debug("selectWithInField() [{}]: size={}", schema, listA1.size());
    }

    /**
     * <pre>
     * Запрос использует конструкцию IN (для нескольких полей) в секции WHERE - row().in(select...).
     *
     * SQL:
     * select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     * from `aa_trans`
     * where ((`aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`) in (select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`
     *                                                             from `aa_trans`
     *                                                             where lower(`aa_trans`.`TRANS_NAME`) like lower('%S%'))
     *                                                                or `aa_trans`.`TRANS_ID` not in (222, 333))
     * order by `aa_trans`.`TRANS_NAME` desc
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithInRow() {

        var list = dslContext
                .selectFrom(AA_TRANS)
                .where(DSL.row(AA_TRANS.TRANS_ID, AA_TRANS.TRANS_NAME).in(
                                 dslContext.select(AA_TRANS.TRANS_ID, AA_TRANS.TRANS_NAME).from(AA_TRANS)
                                           .where(AA_TRANS.TRANS_NAME.likeIgnoreCase("%S%")))
                   .or(AA_TRANS.TRANS_ID.notIn(DSL.inline(222), DSL.inline(333))))
                .orderBy(AA_TRANS.TRANS_NAME.desc())
                .limit(2)
                .fetch();
        LOG.debug("selectWithInRow() [{}]: size={}", schema, list.size());
    }

    /**
     * <pre>
     * Запрос использует операторы сравнения.
     *
     * SQL:
     * select `aa_trans`.`TRANS_ID`, `aa_trans`.`TRANS_NAME`, `aa_trans`.`NOTE`, `aa_trans`.`CREATED_DT`
     * from `aa_trans`
     * where (`aa_trans`.`CREATED_DT` = {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` = {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` <> {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` <> {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` >= {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` >= {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` > {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` > {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` <= {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` <= {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` < {ts '2000-01-01 00:00:00.0'} or
     *        `aa_trans`.`CREATED_DT` < {ts '2000-01-01 00:00:00.0'})
     * order by `aa_trans`.`TRANS_NAME` desc, `aa_trans`.`TRANS_ID` asc
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithCompare() {
        LocalDateTime localDateTimeParam = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);

        var listA1 = dslContext
                .selectFrom(AA_TRANS)
                .where(AA_TRANS.CREATED_DT.equal(DSL.inline(localDateTimeParam))            // ==  equal()
                   .or(AA_TRANS.CREATED_DT.eq(DSL.inline(localDateTimeParam)))              // ==  eq()
                   .or(AA_TRANS.CREATED_DT.notEqual(DSL.inline(localDateTimeParam)))        // <>  notEqual()
                   .or(AA_TRANS.CREATED_DT.ne(DSL.inline(localDateTimeParam)))              // <>  ne()
                   .or(AA_TRANS.CREATED_DT.greaterOrEqual(DSL.inline(localDateTimeParam)))  // >=  greaterOrEqual()
                   .or(AA_TRANS.CREATED_DT.ge(DSL.inline(localDateTimeParam)))              // >=  ge()
                   .or(AA_TRANS.CREATED_DT.greaterThan(DSL.inline(localDateTimeParam)))     // >   greaterThan()
                   .or(AA_TRANS.CREATED_DT.gt(DSL.inline(localDateTimeParam)))              // >   gt()
                   .or(AA_TRANS.CREATED_DT.lessOrEqual(DSL.inline(localDateTimeParam)))     // >=  greaterOrEqual()
                   .or(AA_TRANS.CREATED_DT.le(DSL.inline(localDateTimeParam)))              // >=  ge()
                   .or(AA_TRANS.CREATED_DT.lessThan(DSL.inline(localDateTimeParam)))        // >   greaterThan()
                   .or(AA_TRANS.CREATED_DT.lt(DSL.inline(localDateTimeParam)))              // >   gt()
                   .or(AA_TRANS.CREATED_DT.isNull())                                        // IS NULL      isNull()
                   .or(AA_TRANS.CREATED_DT.isNotNull())                                     // IS NOT NULL  isNull()
                )
                .orderBy(AA_TRANS.TRANS_NAME.desc(), AA_TRANS.TRANS_ID.asc())
                .limit(2)
                .fetch();
        LOG.debug("selectWithCompare [{}]: size={}", schema, listA1.size());
    }

    /**
     * <pre>
     * Преобразование типа считываемых полей. Запрос использует оператор cast() - преобразование осуществляется в SQL.
     *
     * SQL:
     * select cast(`aa_trans`.`CREATED_DT` as date) as `CREATED_DT_AS_LOCAL_DATE`,
     *        cast(`aa_trans`.`CREATED_DT` as char) as `CREATED_DT_AS_STRING`,
     *        null                                  as `NULL_AS_STRING`
     * from `aa_trans`
     * order by `aa_trans`.`TRANS_NAME` desc, `aa_trans`.`TRANS_ID` asc
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithCast() {

        var listA1 = dslContext
                .select(AA_TRANS.CREATED_DT.cast(LocalDate.class).as("CREATED_DT_AS_LOCAL_DATE"),
                        AA_TRANS.CREATED_DT.cast(String.class).as("CREATED_DT_AS_STRING"),
                        DSL.inline((String) null).as("NULL_AS_STRING")
                )
                .from(AA_TRANS)
                .orderBy(AA_TRANS.TRANS_NAME.desc(), AA_TRANS.TRANS_ID.asc())
                .limit(2)
                .fetch();
        for (var record: listA1) {
            LocalDate createdDtAsLocalDate = record.get("CREATED_DT_AS_LOCAL_DATE", LocalDate.class);
            String createdDtAsString = record.get("CREATED_DT_AS_STRING", String.class);
            String nullAsString = record.get("NULL_AS_STRING", String.class);
        }
        LOG.debug("selectComplicatedSql fetch() [{}]: size={}", schema, listA1.size());
    }

    /**
     * <pre>
     * Преобразование типа считываемых полей. Запрос использует оператор coerce() - преобразование осуществляется в Java.
     *
     * SQL:
     * select `aa_trans`.`CREATED_DT` as `CREATED_DT_AS_LOCAL_DATE`,
     *        `aa_trans`.`CREATED_DT` as `CREATED_DT_AS_STRING`
     * from `aa_trans`
     * order by `aa_trans`.`TRANS_NAME` desc, `aa_trans`.`TRANS_ID` asc
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithCoerce() {

        var listA1 = dslContext
                .select(AA_TRANS.CREATED_DT.coerce(LocalDate.class).as("CREATED_DT_AS_LOCAL_DATE"),
                        AA_TRANS.CREATED_DT.coerce(String.class).as("CREATED_DT_AS_STRING")
                )
                .from(AA_TRANS)
                .orderBy(AA_TRANS.TRANS_NAME.desc(), AA_TRANS.TRANS_ID.asc())
                .limit(2)
                .fetch();
        for (var record: listA1) {
            LocalDate createdDtAsLocalDate = record.get("CREATED_DT_AS_LOCAL_DATE", LocalDate.class);
            String createdDtAsString = record.get("CREATED_DT_AS_STRING", String.class);
        }
        LOG.debug("selectComplicatedSql fetch() [{}]: size={}", schema, listA1.size());
    }

    /**
     * <pre>
     * В запросе используется подзапрос в получаемом поле. В подзапросе используется таже тоблица, что и в основном
     * запросе.
     *
     * SQL:
     * select `aa_trans`.`TRANS_ID`,
     *        (select `t2`.`CREATED_DT`
     *         from `aa_trans` as `t2`
     *         where `t2`.`TRANS_ID` = `aa_trans`.`TRANS_ID`) as `CREATED_DT_FROM_T2`
     * from `aa_trans`
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithSubqueryField() {

        AaTrans t2 = AA_TRANS.as("t2");

        var list = dslContext
                .select(AA_TRANS.TRANS_ID,
                        dslContext.select(t2.CREATED_DT)
                                  .from(t2)
                                  .where(t2.TRANS_ID.eq(AA_TRANS.TRANS_ID))
                                  .asField("CREATED_DT_FROM_T2")
                )
                .from(AA_TRANS)
                .limit(2)
                .fetch();
        for (var record: list) {
            long transId = record.get("TRANS_ID", Long.class);
            LocalDateTime createdDtFromT2 = record.get("CREATED_DT_FROM_T2", LocalDateTime.class);
        }
        LOG.debug("selectWithSubqueryField [{}]: size={}", schema, list.size());
    }

    /**
     * <pre>
     * В запросе осуществляется join к той же таблице, которая является основной.
     *
     * SQL:
     * select `aa_trans`.`TRANS_ID`,
     *        `t2`.`TRANS_NAME`,
     *        `t3`.`CREATED_DT`
     * from `aa_trans`
     * join `aa_trans` as `t2` on `t2`.`TRANS_ID` = `aa_trans`.`TRANS_ID`
     * left outer join `aa_trans` as `t3` on `t3`.`TRANS_ID` = `aa_trans`.`TRANS_ID`
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectFromSameTable() {
        AaTrans t2 = AA_TRANS.as("t2");
        AaTrans t3 = AA_TRANS.as("t3");

        var list = dslContext
                .select(AA_TRANS.TRANS_ID,
                        t2.TRANS_NAME,
                        t3.CREATED_DT
                 )
                .from(AA_TRANS)
                .join(t2).on(t2.TRANS_ID.eq(AA_TRANS.TRANS_ID))
                .leftJoin(t3).on(t3.TRANS_ID.eq(AA_TRANS.TRANS_ID))
                .limit(2)
                .fetch();
        LOG.debug("selectFromSameTable [{}]: size={}", schema, list.size());
    }

    /**
     * <pre>
     * Запрос использует <i>selectDistinct()</i>.
     *
     * SQL:
     * select distinct `aa_trans`.`TRANS_NAME`, `aa_trans`.`CREATED_DT`
     * from `aa_trans`
     * order by `aa_trans`.`TRANS_NAME` desc
     * limit 2
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectDistinct() {

        var list = dslContext
                .selectDistinct(AA_TRANS.TRANS_NAME, AA_TRANS.CREATED_DT)
                .from(AA_TRANS)
                .orderBy(AA_TRANS.TRANS_NAME.desc())
                .limit(2)
                .fetch();
        LOG.debug("selectDistinct() [{}]: size={}", schema, list.size());
    }


    /**
     * <pre>
     * Запрос использует <i>with()</i>.
     *
     * SQL:
     * with
     *   `u` as (
     *     select `aa_user`.`USER_ID`, `aa_user`.`UNAME`, `aa_user`.`SHORT_NAME`, `aa_user`.`FULL_NAME`
     *     from `aa_user`
     *   ),
     *   `r` as (
     *     select `aa_role`.`ROLE_ID`, `aa_role`.`ROLE_CD`, `aa_role`.`ROLE_NAME`
     *     from `aa_role`
     *   )
     * select
     *   `aa_user_role`.`USER_ID`,
     *   `u`.`UNAME`,
     *   `u`.`SHORT_NAME`,
     *   `u`.`FULL_NAME`,
     *   `aa_user_role`.`ROLE_ID`,
     *   `r`.`ROLE_CD`,
     *   `r`.`ROLE_NAME`
     * from `aa_user_role`
     * join `u` on `u`.`USER_ID` = `aa_user_role`.`USER_ID`
     * join `r` on `r`.`ROLE_ID` = `aa_user_role`.`ROLE_ID`
     * order by `u`.`UNAME` desc, `r`.`ROLE_CD` desc
     * </pre>
     */
    @Transactional(readOnly = true)
    public void selectWithWith() {

        // Вариант 1.
        CommonTableExpression<Record4<Long, String, String, String>> u =
            DSL.name("u")
               .as(dslContext
                      .select(AA_USER.USER_ID, AA_USER.UNAME, AA_USER.SHORT_NAME, AA_USER.FULL_NAME)
                      .from(AA_USER)
                      );
        CommonTableExpression<Record3<Long, String, String>> r =
                DSL.name("r")
                        .as(dslContext
                                .select(AA_ROLE.ROLE_ID, AA_ROLE.ROLE_CD, AA_ROLE.ROLE_NAME)
                                .from(AA_ROLE)
                        );
        var list = dslContext
                .with(u)
                .with(r)
                .select(AA_USER_ROLE.USER_ID, u.field(AA_USER.UNAME), u.field(AA_USER.SHORT_NAME), u.field(AA_USER.FULL_NAME),
                        AA_USER_ROLE.ROLE_ID, r.field(AA_ROLE.ROLE_CD), r.field(AA_ROLE.ROLE_NAME))
                .from(AA_USER_ROLE)
                .join(u).on(u.field(AA_USER.USER_ID).eq(AA_USER_ROLE.USER_ID))
                .join(r).on(r.field(AA_ROLE.ROLE_ID).eq(AA_USER_ROLE.ROLE_ID))
                .orderBy(u.field(AA_USER.UNAME).desc(), r.field(AA_ROLE.ROLE_CD).desc())
                .fetch();
        LOG.debug("selectWithWith() [{}] 1: size={}", schema, list.size());

        // Вариант 2.
        list = dslContext
                .with("u")
                    .as(dslContext.select(AA_USER.USER_ID, AA_USER.UNAME, AA_USER.SHORT_NAME, AA_USER.FULL_NAME)
                                  .from(AA_USER))
                .with("r")
                    .as(dslContext.select(AA_ROLE.ROLE_ID, AA_ROLE.ROLE_CD, AA_ROLE.ROLE_NAME)
                                  .from(AA_ROLE))
                .select(AA_USER_ROLE.USER_ID, u.field(AA_USER.UNAME), u.field(AA_USER.SHORT_NAME), u.field(AA_USER.FULL_NAME),
                        AA_USER_ROLE.ROLE_ID, r.field(AA_ROLE.ROLE_CD), r.field(AA_ROLE.ROLE_NAME))
                .from(AA_USER_ROLE)
                .join(DSL.name("u"))
                    .on(DSL.field(DSL.name("u", AA_USER.USER_ID.getName())).eq(AA_USER_ROLE.USER_ID))
                .join(DSL.name("r"))
                    .on(DSL.field(DSL.name("r", AA_ROLE.ROLE_ID.getName())).eq(AA_USER_ROLE.ROLE_ID))
                .orderBy(DSL.field(DSL.name("u", AA_USER.UNAME.getName())).desc(),
                         DSL.field(DSL.name("r", AA_ROLE.ROLE_CD.getName())).desc())
                .fetch();
        LOG.debug("selectWithWith() [{}] 2: size={}", schema, list.size());
    }

}
