package yan.api.sizebay.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;

@Repository
public class ClienteJdbcRepository {

    private final JdbcTemplate jdbc;

    public ClienteJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> findSaldoCliente(Integer clienteId) {
        String sql = "SELECT cli_saldo, cli_limite FROM tb_clientes WHERE cli_id = ?";
        try {
            return jdbc.queryForObject(sql, (rs, rowNum) -> Map.of(
                    "total", rs.getInt("cli_saldo"),
                    "limite", rs.getInt("cli_limite")
            ), clienteId);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
