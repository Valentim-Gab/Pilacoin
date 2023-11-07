package br.ufsm.csi.pilacoin.model.json;

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
public class BlockJson {
  private int numeroBloco;
  private String nonceBlocoAnterior;
  private String nonce;
  private byte[] chaveUsuarioMinerador;
  private String nomeUsuarioMinerador;
  private List<TransactionJson> transacoes;
}
