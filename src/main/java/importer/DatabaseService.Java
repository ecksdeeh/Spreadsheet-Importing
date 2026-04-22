package importer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void upsert(String sku, String brand, int linqId, String closeoutId, String closeoutType, String avbStatus) {
        String sql = """
            INSERT INTO avb_closeout (avb_sku, avb_brand, linq_id, closeout_id, closeout_type, avb_status)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                avb_brand = VALUES(avb_brand),
                linq_id = VALUES(linq_id),
                closeout_id = VALUES(closeout_id),
                closeout_type = VALUES(closeout_type),
                avb_status = VALUES(avb_status)
        """;
        jdbcTemplate.update(sql, sku, brand, linqId, closeoutId, closeoutType, avbStatus);
    }
}