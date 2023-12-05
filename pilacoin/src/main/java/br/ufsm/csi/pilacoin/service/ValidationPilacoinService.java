package br.ufsm.csi.pilacoin.service;

import java.math.BigInteger;

import br.ufsm.csi.pilacoin.constant.UserConstant;
import br.ufsm.csi.pilacoin.model.json.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.utils.CryptoUtil;
import br.ufsm.csi.pilacoin.utils.StrUtil;
import br.ufsm.csi.pilacoin.web.WebSocketService;

@Service
public class ValidationPilacoinService {
    private final DifficultService difficultService;
    private final RabbitTemplate rabbitTemplate;
    private final WebSocketService webSocketService;

    @Value("${queue.pilacoin.mined}")
    private String pilaMineradoQueue;

    @Value("${queue.pilacoin.valided}")
    private String pilaValidedQueue;

    public ValidationPilacoinService(RabbitTemplate rabbitTemplate, DifficultService difficultService,
            WebSocketService webSocketService) {
        this.rabbitTemplate = rabbitTemplate;
        this.difficultService = difficultService;
        this.webSocketService = webSocketService;
    }

    @RabbitListener(queues = { "${queue.pilacoin.mined}" })
    public void verifyMinedPila(@Payload String strJson) {
        try {
            BigInteger difficult = difficultService.getDifficult();
            ObjectMapper mapper = new ObjectMapper();
            PilaCoinJson pilaCoin = mapper.readValue(strJson, PilaCoinJson.class);

            if (pilaCoin.getNomeCriador() == null || pilaCoin.getNomeCriador().contains(UserConstant.USERNAME)) {
                rabbitTemplate.convertAndSend(pilaMineradoQueue, strJson);

                return;
            }

            System.out.println("\n\n[VERIFYING PILACOIN]: " + pilaCoin.getNomeCriador());
            webSocketService.send("VERIFYING PILA - criador: " + pilaCoin.getNomeCriador(), "/topic/pilacoin",
                    TypeActionWsJson.TypeAction.VALIDATION_PILACOIN);

            BigInteger hash = CryptoUtil.generatehash(strJson);

            if (hash.compareTo(difficult) < 0) {
                Thread.sleep(500);

                ValidationPilaCoinJson vPilaCoin = ValidationPilaCoinJson.builder()
                        .pilaCoinJson(pilaCoin)
                        .assinaturaPilaCoin(CryptoUtil.generateSignature(pilaCoin))
                        .chavePublicaValidador(CryptoUtil.generateKeys().getPublic().getEncoded())
                        .nomeValidador("Gabriel_Valentim").build();

                System.out.println("[VALID PILACOIN]: " + pilaCoin.getNonce());
                webSocketService.send("VALID PILA - nonce: " + StrUtil.limitCharsAddEllipsis(pilaCoin.getNonce(), 10),
                        "/topic/pilacoin",
                        TypeActionWsJson.TypeAction.VALIDATION_PILACOIN);

                rabbitTemplate.convertAndSend(pilaValidedQueue, mapper.writeValueAsString(vPilaCoin));
            } else {
                System.out.println("[INVALID PILACOIN]: " + pilaCoin.getNonce());
                webSocketService.send("INVALID PILA - criador: " + pilaCoin.getNomeCriador(),
                        "/topic/pilacoin",
                        TypeActionWsJson.TypeAction.VALIDATION_PILACOIN);
            }

            Thread.sleep(1000);
        } catch (JsonProcessingException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
