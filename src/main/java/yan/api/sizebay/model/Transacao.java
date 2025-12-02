package yan.api.sizebay.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import yan.api.sizebay.model.enums.TipoTransacao;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_TRANSACOES")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRA_ID")
    private Integer id;

    @Column(nullable = false, name = "TRA_VALOR")
    private Integer valor;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, length = 1, name = "TRA_TIPO")
    private TipoTransacao tipo;

    @Column(nullable = false, length = 10, name = "TRA_DESCRICAO")
    private String descricao;

    @Column(nullable = false, name = "TRA_REALIZADA_EM")
    private LocalDateTime realizadaEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLI_ID", nullable = false)
    private Cliente cliente;
}