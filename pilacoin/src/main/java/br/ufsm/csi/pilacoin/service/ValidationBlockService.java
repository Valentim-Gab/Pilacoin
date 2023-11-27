package br.ufsm.csi.pilacoin.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.json.BlockJson;
import br.ufsm.csi.pilacoin.model.json.DifficultJson;
import br.ufsm.csi.pilacoin.model.json.ValidationBlockJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;

// @Service
public class ValidationBlockService {
  @Value("${queue.bloco.mined}")
  private String blockMinedQueue;

  @Value("${queue.bloco.valided}")
  private String blockValidedQueue;

  private DifficultService difficultService;
  private RabbitTemplate rabbitTemplate;
  private CryptoUtil cryptoUtil;

  public ValidationBlockService(RabbitTemplate rabbitTemplate, DifficultService difficultService,
      CryptoUtil cryptoUtil) {
    this.rabbitTemplate = rabbitTemplate;
    this.difficultService = difficultService;
    this.cryptoUtil = cryptoUtil;
  }

  @RabbitListener(queues = { "${queue.bloco.mined}" })
  public void verifyMinedPila(@Payload String strJson) {
    try {
      ObjectMapper om = new ObjectMapper();
      BlockJson block = null;
      block = om.readValue(strJson, BlockJson.class);

      if (block.getChaveUsuarioMinerador() == null
          || block.getNomeUsuarioMinerador().contains("Gabriel_Valentim")) {
        rabbitTemplate.convertAndSend(blockMinedQueue, strJson);

        return;
      }

      while (difficultService.difficultJson == null) {
        Thread.sleep(1000);
      }

      System.out.println("\n\n[VERIFYING BLOCK]: " + block.getNomeUsuarioMinerador());

      DifficultJson difficultJson = difficultService.difficultJson;
      BigInteger difficult = new BigInteger(difficultJson.getDificuldade(), 16).abs();
      BigInteger hash = cryptoUtil.generatehash(strJson);

      if (hash.compareTo(difficult) < 0) {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, cryptoUtil.generateKeys().getPrivate());

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] signature = cipher.doFinal(md.digest(strJson.getBytes(StandardCharsets.UTF_8)));

        Base64.getEncoder().encodeToString(signature);

        ValidationBlockJson vBlock = ValidationBlockJson.builder()
            .bloco(block)
            .assinaturaBloco(signature)
            .chavePublicaValidador(cryptoUtil.generateKeys().getPublic().getEncoded())
            .nomeValidador("Gabriel_Valentim").build();

        System.out.println("\n\n[VALID BLOCK]: " + vBlock.getBloco().getNomeUsuarioMinerador());

        rabbitTemplate.convertAndSend(blockValidedQueue, om.writeValueAsString(vBlock));
      } else {
        System.out.println("\n\n[INVALID BLOCK]: " + block.getNonce());
      }
    } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException
        | BadPaddingException | NoSuchPaddingException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
