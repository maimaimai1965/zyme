package ua.mai.zyme.db.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AaTransRowMapper implements RowMapper<AaTrans> {
    @Override
    public AaTrans mapRow(ResultSet rs, int rowNum) throws SQLException {
        AaTrans aaTrans = new AaTrans();

        aaTrans.setTransId(rs.getLong("TRANS_ID"));
        aaTrans.setTransName(rs.getString("TRANS_NAME"));
        aaTrans.setNote(rs.getString("NOTE"));
        aaTrans.setCreatedDt(rs.getTimestamp("CREATED_DT").toLocalDateTime());
        return aaTrans;
    }
}
