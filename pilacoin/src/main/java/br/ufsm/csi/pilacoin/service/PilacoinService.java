package br.ufsm.csi.pilacoin.service;

import java.util.Optional;

import javax.crypto.Cipher;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.ufsm.csi.pilacoin.model.json.QueryJson;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.json.TransactionJson;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;

@Service
public class PilacoinService {
  @Value("${queue.query}")
  private String query;

  @Value("${queue.user.query}")
  private String userQuery;
  private final PilaCoinRepository pRepository;
  private final CryptoUtil cryptoUtil;
  public RabbitTemplate rabbitTemplate;

  private final SimpMessagingTemplate template;

  @Value("${queue.transfer}")
  private String transactionQueue;

  public PilacoinService(PilaCoinRepository pRepository, CryptoUtil cryptoUtil, RabbitTemplate rabbitTemplate,
                         SimpMessagingTemplate template) {
    this.pRepository = pRepository;
    this.cryptoUtil = cryptoUtil;
    this.rabbitTemplate = rabbitTemplate;
    this.template = template;
  }

  public PilaCoin save(PilaCoinJson pilaCoinJson) {
    Optional<PilaCoin> existingPilaCoin = pRepository.findPilaCoinByNonce(pilaCoinJson.getNonce());
    PilaCoin pilacoin = null;

    if (existingPilaCoin.isPresent()) {
      pilacoin = existingPilaCoin.get();
      pilacoin.setStatus(pilaCoinJson.getStatus());
    } else {
      pilacoin = PilaCoin.builder()
          .chaveCriador(pilaCoinJson.getChaveCriador())
          .dataCriacao(pilaCoinJson.getDataCriacao())
          .nomeCriador(pilaCoinJson.getNomeCriador())
          .nonce(pilaCoinJson.getNonce())
          .status(pilaCoinJson.getStatus())
          .build();
    }

    return pRepository.save(pilacoin);
  }

  public List<PilaCoin> findAll() {
    return pRepository.findAll();
  }

  public Long findAllCount() {
    return pRepository.count();
  }

  @PostConstruct
  public void emitAllCount() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          while (true) {
            template.convertAndSend("/topic/data", findAllCount());

            Thread.sleep(10000);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  public ResponseEntity<Object> findOneByNonce(String nonce) {
    Optional<PilaCoin> pilaCoin = pRepository.findPilaCoinByNonce(nonce);

    try {
      if (pilaCoin.isPresent()) {
        return new ResponseEntity<>(pilaCoin.get(), HttpStatus.OK);
      } else {
        Map<String, Object> jsonData = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        jsonData.put("error", "Pilacoin não encontrado");

        return new ResponseEntity<>(objectMapper.writeValueAsString(jsonData), HttpStatus.BAD_REQUEST);
      }
    } catch (

    JsonProcessingException e) {
      return new ResponseEntity<>("Erro no servidor", HttpStatus.BAD_REQUEST);
    }
  }

  public void update(PilaCoinJson pilaCoinJson, PilaCoin.StatusPila status) {
    Optional<PilaCoin> pilaCoin = pRepository.findPilaCoinByNonce(pilaCoinJson.getNonce());

    if (pilaCoin.isPresent()) {
      pilaCoin.get().setStatus(status);
      pRepository.save(pilaCoin.get());
    } else {
      System.out.println("\n\n[ERROR]: PilaCoin não salvo no Banco");
    }
  }

  @PostConstruct
  public void updateDatabase() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          while (true) {
            QueryJson queryJson = QueryJson.builder()
                    .idQuery(2l)
                    .nomeUsuario("Gabriel_Valentim")
                    .tipoQuery(QueryJson.TypeQuery.PILA)
                    .build();

            ObjectMapper om = new ObjectMapper();

            rabbitTemplate.convertAndSend(query, om.writeValueAsString(queryJson));

            for (int tries = 0; tries <= 10; tries++) {
              String queryResponse = (String) rabbitTemplate.receiveAndConvert(userQuery);

              if (queryResponse == null) {
                Thread.sleep(1000);

                continue;
              }

              QueryJson queryJsonResponse = om.readValue(queryResponse, QueryJson.class);

              if (queryJsonResponse.getIdQuery() == 2 && queryJsonResponse.getPilasResult() != null) {
                for (PilaCoinJson pilacoin : queryJsonResponse.getPilasResult()) {
                  if (pilacoin.getNomeCriador().equals("Gabriel_Valentim")) {
                    save(pilacoin);
                  }
                }
              }
            }

            Thread.sleep(10000);
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  public void deleteByNonce(String nonce) {
    Optional<PilaCoin> pilaCoin = pRepository.findPilaCoinByNonce(nonce);

    if (pilaCoin.isPresent()) {
      pRepository.delete(pilaCoin.get());
    } else {
      System.out.println("\n\n[ERROR]: PilaCoin não salvo no Banco");
    }
  }

  public void transfer(TransactionJson transaction) {
    try {
      transaction.setChaveUsuarioOrigem(cryptoUtil.generateKeys().getPublic().getEncoded());
      transaction.setDataTransacao(new Date());

      ObjectMapper mapper = new ObjectMapper();
      String transactionStr = mapper.writeValueAsString(transaction);

      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, cryptoUtil.generateKeys().getPrivate());

      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] signature = cipher.doFinal(md.digest(transactionStr.getBytes(StandardCharsets.UTF_8)));

      Base64.getEncoder().encodeToString(signature);
      transaction.setAssinatura(signature);

      System.out.println("\n\n[TRANSFER DESTINATION]: " + transaction.getNomeUsuarioDestino());

      rabbitTemplate.convertAndSend(transactionQueue, mapper.writeValueAsString(transaction));

      deleteByNonce(transaction.getNoncePila());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
