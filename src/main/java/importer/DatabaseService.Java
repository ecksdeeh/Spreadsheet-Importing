@Service
public class DatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void upsert(String sku, String brand, int linqId) {

        String sql = """
            INSERT INTO products (avb_sku, avb_brand, linq_id)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                avb_brand = VALUES(avb_brand),
                linq_id = VALUES(linq_id)
        """;

        jdbcTemplate.update(sql, sku, brand, linqId);
    }
}