package br.ufsm.csi.pilacoin.service;

import java.math.BigInteger;

import br.ufsm.csi.pilacoin.model.json.TypeActionWsJson;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.constant.UserConstant;
import br.ufsm.csi.pilacoin.model.json.BlockJson;
import br.ufsm.csi.pilacoin.model.json.ValidationBlockJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;
import br.ufsm.csi.pilacoin.web.WebSocketService;

@Service
public class ValidationBlockService {
    private final DifficultService difficultService;
    private final RabbitTemplate rabbitTemplate;
    private final WebSocketService webSocketService;

    @Value("${queue.bloco.mined}")
    private String blockMinedQueue;

    @Value("${queue.bloco.valided}")
    private String blockValidedQueue;

    public ValidationBlockService(RabbitTemplate rabbitTemplate, DifficultService difficultService,
            WebSocketService webSocketService) {
        this.rabbitTemplate = rabbitTemplate;
        this.difficultService = difficultService;
        this.webSocketService = webSocketService;
    }

    @RabbitListener(queues = { "${queue.bloco.mined}" })
    public void verifyMinedPila(@Payload String strJson) {
        try {
            BigInteger difficult = difficultService.getDifficult();
            ObjectMapper mapper = new ObjectMapper();
            BlockJson block = null;
            block = mapper.readValue(strJson, BlockJson.class);

            if (block.getChaveUsuarioMinerador() == null
                    || block.getNomeUsuarioMinerador().contains(UserConstant.USERNAME)) {
                rabbitTemplate.convertAndSend(blockMinedQueue, strJson);

                return;
            }

            System.out.println("\n\n[VERIFYING BLOCK]: " + block.getNomeUsuarioMinerador());
            webSocketService.send("VERIFYING BLOCK - minerador: " + block.getNomeUsuarioMinerador(), "/topic/pilacoin",
                    TypeActionWsJson.TypeAction.VALIDATION_BLOCK);

            BigInteger hash = CryptoUtil.generatehash(strJson);

            if (hash.compareTo(difficult) < 0) {
                Thread.sleep(500);

                ValidationBlockJson vBlock = ValidationBlockJson.builder()
                        .bloco(block)
                        .assinaturaBloco(CryptoUtil.generateSignature(block))
                        .chavePublicaValidador(CryptoUtil.generateKeys().getPublic().getEncoded())
                        .nomeValidador(UserConstant.USERNAME).build();

                System.out.println("\n\n[VALID BLOCK]: " + vBlock.getBloco().getNomeUsuarioMinerador());
                webSocketService.send("VALID BLOCK - minerador: " + vBlock.getBloco().getNomeUsuarioMinerador(),
                        "/topic/pilacoin", TypeActionWsJson.TypeAction.VALIDATION_BLOCK);

                rabbitTemplate.convertAndSend(blockValidedQueue, mapper.writeValueAsString(vBlock));
            } else {
                System.out.println("\n\n[INVALID BLOCK]: " + block.getNonce());
                webSocketService.send("INVALID BLOCK - minerador: " + block.getNomeUsuarioMinerador(),
                        "/topic/pilacoin",
                        TypeActionWsJson.TypeAction.VALIDATION_BLOCK);
            }

            Thread.sleep(1000);
        } catch (JsonProcessingException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
