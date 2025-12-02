package yan.api.sizebay.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import yan.api.sizebay.dto.transacao.TransacaoResponse;
import yan.api.sizebay.model.Transacao;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {

    List<Transacao> findByClienteIdOrderByRealizadaEmDesc(Integer clienteId, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM criar_transacao(:clienteId, :valor, :descricao, :tipo)")
    TransacaoResponse executeTransaction(Integer clienteId, Integer valor, String descricao, String tipo);

    @Query(value = "SELECT * FROM tb_transacoes WHERE cli_id = :clienteId ORDER BY tra_realizada_em DESC LIMIT 10", nativeQuery = true)
    List<Transacao> findLatestByClienteId(Integer clienteId);
}