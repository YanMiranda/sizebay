package yan.api.sizebay.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import yan.api.sizebay.dto.transacao.TransacaoResponse;
import yan.api.sizebay.exception.ClienteNaoEncontradoException;
import yan.api.sizebay.exception.SaldoInsuficienteException;
import yan.api.sizebay.model.Transacao;

import java.util.List;

@Repository
public class TransacaoJdbcRepository {

    private final JdbcTemplate jdbc;

    public TransacaoJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public TransacaoResponse createTransaction(Integer clienteId, Integer valor, String descricao, String tipo) {
        String sql = "SELECT * FROM create_transaction(?, ?, ?, ?)";

        return jdbc.queryForObject(sql, (rs, rowNum) -> {
            char status = rs.getString("status").charAt(0);

            return switch (status) {
                case 'O' -> new TransacaoResponse(
                        rs.getInt("limite"),
                        rs.getInt("novo_saldo")
                );
                case 'N' -> throw new ClienteNaoEncontradoException();
                case 'I' -> throw new SaldoInsuficienteException();
                default  -> throw new IllegalStateException("Status desconhecido do banco: " + status);
            };
        }, clienteId, valor, descricao, tipo);
    }

    public List<Transacao> buscarUltimasTransacoes(Integer clienteId) {
        String sql = """
            SELECT tra_valor, tra_tipo, tra_descricao, tra_realizada_em 
            FROM tb_transacoes 
            WHERE cli_id = ? 
            ORDER BY tra_realizada_em DESC 
            LIMIT 10
            """;

        return jdbc.query(sql, (rs, rowNum) -> Transacao.builder()
                        .valor(rs.getInt("tra_valor"))
                        .tipo(String.valueOf(rs.getString("tra_tipo").charAt(0)))
                        .descricao(rs.getString("tra_descricao"))
                        .realizadaEm(rs.getTimestamp("tra_realizada_em").toLocalDateTime())
                        .build()
                , clienteId);
    }
}