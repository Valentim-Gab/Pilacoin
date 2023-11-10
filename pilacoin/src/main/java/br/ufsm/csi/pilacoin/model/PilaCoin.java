package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pilacoin")
public class PilaCoin {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chave_criador", nullable = false)
    private byte[] chaveCriador;

    @Column(name = "nome_criador", nullable = false, length = 50)
    private String nomeCriador;

    @Column(name = "data_criacao", nullable = false)
    private Date dataCriacao;

    @Column(name = "nonce", nullable = false)
    private String nonce;

    @Column(name = "status", nullable = false, length = 50)
    private StatusPila status;

    public enum StatusPila { AG_VALIDACAO, AG_BLOCO, BLOCO_EM_VALIDACAO, VALIDO, INVALIDO }

    // public static void main(String[] args) {
    //     try {
    //         KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    //         KeyPair pair = generator.generateKeyPair();
    //         byte[] pubKey = pair.getPublic().getEncoded();

    //         PilaCoin pilaCoin = PilaCoin.builder().chavePublica(pubKey)
    //                 .dataHoraCriacao(new Date()).nomeMinerador("Valentim").build();
    //         String value = "f".repeat(60);
    //         BigInteger difficulty = new BigInteger(value, 16).abs();
    //         BigInteger hash;
    //         ObjectMapper objectMapper = new ObjectMapper();
    //         MessageDigest md = MessageDigest.getInstance("SHA-256");
    //         Random random = new Random();
    //         byte[] bArrToRandom = new byte[256/8];

    //         do {
    //             random.nextBytes(bArrToRandom);
    //             pilaCoin.nonce = new BigInteger(bArrToRandom).abs().toString();

    //             String json = objectMapper.writeValueAsString(pilaCoin);
    //             hash = new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8)));
    //             hash = hash.abs();
    //         } while (hash.compareTo(difficulty) > 0);
    //         System.out.println("Foi");
    //     } catch (NoSuchAlgorithmException | JsonProcessingException e) {
    //         throw new RuntimeException(e);
    //     }
    // }
}
