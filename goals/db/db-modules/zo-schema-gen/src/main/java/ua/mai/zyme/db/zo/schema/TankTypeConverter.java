package ua.mai.zyme.db.zo.schema;

import ua.mai.zyme.db.jooq.CharEnumConverter;

import java.util.Map;

public class TankTypeConverter extends CharEnumConverter<TankType> {
    public TankTypeConverter() {
        super(TankType.class, Map.of(
                TankType.AQUARIUM, "A",
                TankType.CAGE, "C",
                TankType.PADDOCK, "P",
                TankType.TERRARIUM, "T"
        ));
    }
}
