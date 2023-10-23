package ua.mai.zyme.rest.zoo.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
//import ua.telesens.o320.pgw.rest_services.exceptions.RestFaultException;
//import ua.telesens.o320.pgw.rest_services.exceptions.RestFaults;

public enum LimitType {
    // WRONG(0),
    D(1),
    R(2),
    B(3),
    M(4)
    ;

    LimitType(int id) {
        this.id = id;
    }


   // private String value;
    private final int id;

    public int getSize(){
        return LimitType.values().length;
    }

    @JsonValue
    public int getId() { return this.id; }

    @JsonCreator
    public static LimitType create(Integer val) {
        LimitType[] units = LimitType.values();
        for (LimitType unit : units) {
            if (unit.getId()==val) {
                return unit;
            }
        }
        throw new RuntimeException("limitType");
//        throw new RestFaultException(RestFaults.UNSUPPORTED_PARAMETER_VALUE, null, "limitType", val.toString());
    }
    public String getName() {
        return this.name();
    }
}
