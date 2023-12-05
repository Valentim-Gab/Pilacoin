package br.ufsm.csi.pilacoin.service;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

import br.ufsm.csi.pilacoin.model.json.TypeActionWsJson;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.json.BlockJson;
import br.ufsm.csi.pilacoin.model.json.DifficultJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;

@Service
public class MiningBlockService {
  private DifficultService difficultService;
  private CryptoUtil cryptoUtil;
  public RabbitTemplate rabbitTemplate;

  private final SimpMessagingTemplate template;

  @Value("${queue.bloco.mined}")
  private String blockMinedQueue;

  public MiningBlockService(DifficultService difficultService, CryptoUtil cryptoUtil, RabbitTemplate rabbitTemplate,
                            SimpMessagingTemplate template) {
    this.difficultService = difficultService;
    this.cryptoUtil = cryptoUtil;
    this.rabbitTemplate = rabbitTemplate;
    this.template = template;
  }

  @RabbitListener(queues = { "${queue.block.find}" })
  public void mine(@Payload String strJson) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          while (true) {
            if (difficultService.difficultJson == null) {
              Thread.sleep(1000);

              continue;
            }

            ObjectMapper om = new ObjectMapper();
            BlockJson block = om.readValue(strJson, BlockJson.class);
            DifficultJson difficultJson = difficultService.difficultJson;
            BigInteger dificuldade = new BigInteger(difficultJson.getDificuldade(), 16).abs();

            BlockJson minedBlock = BlockJson.builder()
                .numeroBloco(block.getNumeroBloco())
                .nonceBlocoAnterior(block.getNonce())
                .chaveUsuarioMinerador(cryptoUtil.generateKeys().getPublic().getEncoded())
                .nomeUsuarioMinerador("Gabriel_Valentim")
                .build();

            byte[] bNum = new byte[256 / 8];
            Random random = new Random(System.currentTimeMillis());

            do {
              random.nextBytes(bNum);
              minedBlock.setNonce(new BigInteger(bNum).abs().toString());
            } while (cryptoUtil.generatehash(minedBlock).compareTo(dificuldade) > 0);

            if (difficultJson.getValidadeFinal().compareTo(new Date()) > 0 || true) {
              ObjectMapper mapper = new ObjectMapper();
              String blockStr = mapper.writeValueAsString(minedBlock);

              System.out.println("\n\n[BLOCO MINERADO]: " + blockStr);

              TypeActionWsJson typeActionWsJson = TypeActionWsJson.builder()
                      .message("BLOCO MINERADO")
                      .type(TypeActionWsJson.TypeAction.MINER_BLOCK)
                      .timestamp(System.currentTimeMillis())
                      .build();

              template.convertAndSend("/topic/pilacoin",
                      mapper.writeValueAsString(typeActionWsJson));

              rabbitTemplate.convertAndSend(blockMinedQueue, blockStr);

              Thread.sleep(10000);
            }
          }
        } catch (InterruptedException | JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }
}
