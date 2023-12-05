package br.ufsm.csi.pilacoin.model.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionJson {
  private byte[] chaveUsuarioOrigem;
  private byte[] chaveUsuarioDestino;
  private String nomeUsuarioOrigem;
  private String nomeUsuarioDestino;
  private byte[] assinatura;
  private String noncePila;
  private Date dataTransacao;
  private Long id;
  private String status;
}
