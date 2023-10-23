package ua.mai.zyme.db.aa.schema;

import ua.mai.zyme.db.jooq.CharEnumConverter;

import java.util.Map;

public class UserStateConverter extends CharEnumConverter<UserState> {
    public UserStateConverter() {
        super(UserState.class, Map.of(
                UserState.ENABLED, "E",
                UserState.DISABLED, "D",
                UserState.LOCKED, "L"
        ));
    }
}
