package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Date;
import java.util.Random;

@Data
@Builder
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PilaCoin {
    private byte[] chavePublica;
    private String nomeMinerador;
    private Date dataHoraCriacao;
    private String nonce;
    private StatusPila statusPila;
    public enum StatusPila { AG_VALIDACAO, AG_BLOCO, BLOCO_EM_VALIDACAO, VALIDO, INVALIDO }

    public static void main(String[] args) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            KeyPair pair = generator.generateKeyPair();
            byte[] pubKey = pair.getPublic().getEncoded();

            PilaCoin pilaCoin = PilaCoin.builder().chavePublica(pubKey)
                    .dataHoraCriacao(new Date()).nomeMinerador("Valentim").build();
            String value = "f".repeat(60);
            BigInteger difficulty = new BigInteger(value, 16).abs();
            BigInteger hash;
            ObjectMapper objectMapper = new ObjectMapper();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            Random random = new Random();
            byte[] bArrToRandom = new byte[256/8];

            do {
                random.nextBytes(bArrToRandom);
                pilaCoin.nonce = new BigInteger(bArrToRandom).abs().toString();

                String json = objectMapper.writeValueAsString(pilaCoin);
                hash = new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8)));
                hash = hash.abs();
            } while (hash.compareTo(difficulty) > 0);
            System.out.println("Foi");
        } catch (NoSuchAlgorithmException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
