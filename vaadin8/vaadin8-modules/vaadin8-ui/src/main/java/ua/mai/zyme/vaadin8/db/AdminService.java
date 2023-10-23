package ua.mai.zyme.vaadin8.db;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.TableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ua.telesens.o320.trt.be.schema.tables.records.AaRoleRecord;
import ua.telesens.o320.trt.be.schema.tables.records.AaUserRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ua.telesens.o320.trt.be.schema.Tables.AA_ROLE;
import static ua.telesens.o320.trt.be.schema.tables.AaUser.AA_USER;

@Service
@Transactional
public class AdminService {

    @Autowired
    DSLContext dslContext;
    @Autowired TransactionTemplate transactionTemplate;
    @Autowired PlatformTransactionManager transactionManager;

    private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);

    public Result<AaUserRecord> readUsers() {
        Result<AaUserRecord> result = dslContext.selectFrom(AA_USER).fetch();
        return result;
    }

    public Result<AaRoleRecord> readRoles(Map<String, Object> filterValues) {
        return readEntities(dslContext, AA_ROLE,
                new FilterConditionFinder() {
                    @Override
                    public Condition find(String propertyId, Object filterValue) {
                        String name = AA_ROLE.ROLE_ID.getName();
                        if (AA_ROLE.ROLE_ID.getName().equals(propertyId))
                            return numberFieldLikeIgnoreCase(AA_ROLE.ROLE_ID, filterValue);
//                            return AA_ROLE.ROLE_ID.cast(String.class).likeIgnoreCase("%" + filterValue + "%");
                        else if (AA_ROLE.ROLE_CD.getName().equals(propertyId))
                            return fieldLikeIgnoreCase(AA_ROLE.ROLE_CD, filterValue);
//                            return AA_ROLE.ROLE_CD.likeIgnoreCase("%" + filterValue.toString().toUpperCase() + "%");
                        else if (AA_ROLE.ROLE_NAME.getName().equals(propertyId))
                            return fieldLikeIgnoreCase(AA_ROLE.ROLE_NAME, filterValue);
//                            return AA_ROLE.ROLE_NAME.likeIgnoreCase("%" + filterValue.toString().toUpperCase() + "%");
                        else if (AA_ROLE.ROLE_TYPE.getName().equals(propertyId))
                            return fieldEqual(AA_ROLE.ROLE_TYPE, filterValue);
                        else
                            throw new RuntimeException("Unknown PropertyId='" + propertyId + "' in Role filter ");
                    }
                }, filterValues);
    }

    private <R extends Record, T extends Number> Condition numberFieldLikeIgnoreCase(TableField<R, Long> field, Object filterValue) {
        return field.cast(String.class).likeIgnoreCase("%" + filterValue.toString().toUpperCase() + "%");
    }

    private <R extends Record, T extends Object> Condition fieldLikeIgnoreCase(TableField<R, String> field, Object filterValue) {
        return field.likeIgnoreCase("%" + filterValue.toString().toUpperCase() + "%");
    }

//    private <R extends Record, T> Condition fieldEqual(TableField<R, T> field, T filterValue) {
//        return field.equal(filterValue);
//    }
    private Condition fieldEqual(TableField field, Object filterValue) {
        return field.equal(filterValue);
    }

    @FunctionalInterface
    public interface FilterConditionFinder {
        Condition find(String propertyId, Object filterValue);
    }

    public <ENTITY extends Record> Result<ENTITY> readEntities(DSLContext dslContext, TableImpl<ENTITY> table,
                                                               FilterConditionFinder finder, Map<String, Object> filterValues) {
        SelectWhereStep<ENTITY> select = dslContext.selectFrom(table);

        List<Condition> conditions = new ArrayList<>();
        for (Map.Entry filterValue: filterValues.entrySet())
            conditions.add(finder.find((String)filterValue.getKey(), filterValue.getValue()));

        SelectConditionStep<ENTITY> selectCondition = null;
        for (Condition condition: conditions) {
            if (selectCondition == null)
                selectCondition = select.where(condition);
            else
                selectCondition.and(condition);
        }

        Result<ENTITY> result = select.fetch();
        return result;
    }

}


