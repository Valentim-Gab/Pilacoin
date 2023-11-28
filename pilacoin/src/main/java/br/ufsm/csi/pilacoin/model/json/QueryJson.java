package br.ufsm.csi.pilacoin.model.json;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.User;

import java.util.List;

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
  private Long idQuery;
  private String nomeUsuario;
  private String usuario;
  private TypeQuery tipoQuery;
  private PilaCoin.StatusPila status;
  private String usuarioMinerador;
  private String nonce;
  private Long idBloco;
  private List<User> usuariosResult;
  private List<PilaCoinJson> pilasResult;
  private List<BlockJson> blocosResult;

  public enum TypeQuery {
    USUARIOS, PILA, BLOCO
  }
}
