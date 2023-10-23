package ua.mai.zyme.rest.zoo.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum TransactionState {
    CREATED((byte)101, "Invoked", false),
    CHECKED((byte)102, "Checked", false),
    COMMITTED((byte)110, "Committed", false),
    IN_PROGRESS((byte)120, "In Progress", false),
    SCHEDULED((byte)112, "Waiting", false),
    DONE((byte)111, "Done", true),
    NOT_ALLOWED((byte)-102, "Not allowed", true),
    ABORTED((byte)-104, "Aborted", true),
    ABORTED_ON_TIMEOUT((byte)-103, "Timed out", true),
    ERROR((byte)-111, "Error", false),
    ERROR_MAX_ATTEMPTS((byte)-112, "Max Attempts", false),
    CANCELED((byte)-115, "Canceled", true),
    CANCELED_BY_BSS((byte)-116, "Canceled by BSS", true);

    private byte stateNo;
    private String desc;
    private boolean finalState;

    private static Map<Byte, TransactionState> map;

    TransactionState(byte stateNo, String desc, boolean finalState) {
        this.stateNo = stateNo;
        this.desc = desc;
        this.finalState = finalState;
    }

    public byte getStateNo() {
        return stateNo;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isFinalState() {
        return finalState;
    }

    public static TransactionState getState(byte stateNo) {
        if (map == null) {
            map = new HashMap<>();
            Arrays.stream(TransactionState.values()).forEach(state -> map.put(Byte.valueOf(state.getStateNo()), state));
        }
        return map.get(Byte.valueOf(stateNo));
    }

}