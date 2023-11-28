package br.ufsm.csi.pilacoin.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.crypto.Cipher;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.json.QueryJson;
import br.ufsm.csi.pilacoin.model.json.TransactionJson;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;

@Service
public class PilacoinService {
  private final PilaCoinRepository pRepository;
  private final CryptoUtil cryptoUtil;
  public RabbitTemplate rabbitTemplate;
  private final MessagesService messagesService;

  @Value("${queue.transfer}")
  private String transactionQueue;

  public PilacoinService(PilaCoinRepository pRepository, CryptoUtil cryptoUtil, RabbitTemplate rabbitTemplate,
      MessagesService messagesService) {
    this.pRepository = pRepository;
    this.cryptoUtil = cryptoUtil;
    this.rabbitTemplate = rabbitTemplate;
    this.messagesService = messagesService;
  }

  public PilaCoin save(PilaCoinJson pilaCoinJson, PilaCoin.StatusPila status) {
    PilaCoin pilaCoin = PilaCoin.builder()
        .chaveCriador(pilaCoinJson.getChaveCriador())
        .dataCriacao(pilaCoinJson.getDataCriacao())
        .nomeCriador(pilaCoinJson.getNomeCriador())
        .nonce(pilaCoinJson.getNonce())
        .status(status)
        .build();

    return pRepository.save(pilaCoin);
  }

  public List<PilaCoin> findAll() {
    return pRepository.findAll();
  }

  @RabbitListener(queues = { "${queue.user.query}" })
  public ResponseEntity<Object> findOneByNonce(String nonce, @Payload String strJson) {
    AtomicReference<ResponseEntity<Object>> responseEntity = new AtomicReference<>(null);

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Optional<PilaCoin> pilaCoin = pRepository.findPilaCoinByNonce(nonce);

          if (pilaCoin.isPresent()) {
            QueryJson queryJson = QueryJson.builder()
                .idQuery(2l)
                .nomeUsuario("Gabriel_Valentim")
                .tipoQuery(QueryJson.TypeQuery.PILA)
                .nonce(nonce)
                .build();

            ObjectMapper om = new ObjectMapper();

            rabbitTemplate.convertAndSend(transactionQueue, om.writeValueAsString(queryJson));

            while (true) {
              if (strJson == null) {
                Thread.sleep(500);

                continue;
              }

              QueryJson queryJsonResponse = om.readValue(strJson, QueryJson.class);

              if (queryJsonResponse.getIdQuery() != 2l) {
                continue;
              }

              PilaCoin updatedPilaCoin = PilaCoin.builder()
                  .chaveCriador(queryJsonResponse.getPilasResult().get(0).getChaveCriador())
                  .dataCriacao(queryJsonResponse.getPilasResult().get(0).getDataCriacao())
                  .nomeCriador(queryJsonResponse.getPilasResult().get(0).getNomeCriador())
                  .nonce(queryJsonResponse.getPilasResult().get(0).getNonce())
                  .status(queryJsonResponse.getPilasResult().get(0).getStatus())
                  .build();

              pRepository.save(updatedPilaCoin);

              System.out.println("\n\n[UPDATED PILACOIN]: " + updatedPilaCoin.toString());

              responseEntity.set(new ResponseEntity<>(updatedPilaCoin, HttpStatus.OK));

              break;
            }

            Map<String, Object> jsonData = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            jsonData.put("error", "Pilacoin não encontrado");

            responseEntity.set(new ResponseEntity<>(objectMapper.writeValueAsString(jsonData), HttpStatus.BAD_REQUEST));
          }
        } catch (JsonProcessingException | InterruptedException e) {
          responseEntity.set(new ResponseEntity<>("Erro no servidor", HttpStatus.BAD_REQUEST));
        }
      }
    }).start();

    return responseEntity.get();
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

  public void transfer(TransactionJson transaction) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String transactionStr = mapper.writeValueAsString(transaction);

      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, cryptoUtil.generateKeys().getPrivate());

      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] signature = cipher.doFinal(md.digest(transactionStr.getBytes(StandardCharsets.UTF_8)));

      Base64.getEncoder().encodeToString(signature);

      transaction.setAssinatura(signature);
      transaction.setChaveUsuarioOrigem(cryptoUtil.generateKeys().getPublic().getEncoded());
      transaction.setDataTransacao(new Date());

      // System.out.println("\n\n[TRANSFER]: " + transaction.toString());

      // rabbitTemplate.convertAndSend(transactionQueue,
      // mapper.writeValueAsString(transaction));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
