package ua.mai.zyme.db.jdbc;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс имеющий общие поля с Trans (transId, transName) и отсутствующие в Trans поля (startDt, delay).
 */
public class AnotherTrans {

    @Getter @Setter
    private Long transId;
    @Getter @Setter
    private String transName;
    @Getter @Setter
    private LocalDateTime startDt;
    @Getter @Setter
    private Integer delay;

    public AnotherTrans(Long transId, String transName, LocalDateTime startDt, Integer delay) {
        this.transId = transId;
        this.transName = transName;
        this.startDt = startDt;
        this.delay = delay;
    }

    public List<AnotherTrans> generateListWithoutTransId(String transName, LocalDateTime startDt, int count) {

        assert(count > 0);
        List<AnotherTrans> list = new ArrayList<>();

        for (int i = 1; i<=count; i++ ) {
            list.add(new AnotherTrans(null, transName +  " " + i, startDt, i));
        }
        return list;
    }


}
