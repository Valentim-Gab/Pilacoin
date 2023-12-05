package br.ufsm.csi.pilacoin.model.json;

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
public class ReportJson {
    private Long geradoEm;
    private String nomeUsuario;
    private boolean minerouPila;
    private boolean validouPila;
    private boolean minerouBloco;
    private boolean validouBloco;
    private boolean transferiuPila;
}
