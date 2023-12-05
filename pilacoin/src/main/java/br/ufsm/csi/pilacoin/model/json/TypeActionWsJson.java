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
public class TypeActionWsJson {
    private String message;
    private TypeAction type;
    private Long timestamp;

    public enum TypeAction { VALIDATION_PILACOIN, VALIDATION_BLOCK, MINER_BLOCK, MINER_PILACOIN }
}
