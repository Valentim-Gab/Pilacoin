package br.ufsm.csi.pilacoin.model.json;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryJson {
  private Long idQuey;
  private String nomeUsuario;
  private String tipoQuery;
  private PilaCoin.StatusPila status;
  private String usuarioMinerador;
  private String nonce;
  private Long idBloco;
}
