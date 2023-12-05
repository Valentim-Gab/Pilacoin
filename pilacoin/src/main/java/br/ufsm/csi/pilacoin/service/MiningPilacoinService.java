package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.constant.UserConstant;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.json.TypeActionWsJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;
import br.ufsm.csi.pilacoin.utils.StrUtil;
import br.ufsm.csi.pilacoin.web.WebSocketService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

@Service
public class MiningPilacoinService {
    private final DifficultService difficultService;
    private final RabbitTemplate rabbitTemplate;
    private final WebSocketService webSocketService;

    @Value("${queue.pilacoin.mined}")
    private String pilaMineradoQueue;

    public MiningPilacoinService(DifficultService difficultService,
            RabbitTemplate rabbitTemplate, WebSocketService webSocketService) {
        this.difficultService = difficultService;
        this.rabbitTemplate = rabbitTemplate;
        this.webSocketService = webSocketService;
    }

    @PostConstruct
    public void minerar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        BigInteger difficult = difficultService.getDifficult();
                        ObjectMapper mapper = new ObjectMapper();

                        System.out.println("\n\n[MINING PILACOIN]");
                        webSocketService.send("MINING PILACOIN", "/topic/pilacoin",
                                TypeActionWsJson.TypeAction.MINER_PILACOIN);

                        PilaCoinJson pilaJson = PilaCoinJson.builder()
                                .nomeCriador(UserConstant.USERNAME)
                                .dataCriacao(new Date())
                                .chaveCriador(CryptoUtil.generateKeys().getPublic().getEncoded()).build();

                        byte[] bNum = new byte[256 / 8];
                        Random random = new Random(System.currentTimeMillis());

                        do {
                            random.nextBytes(bNum);
                            pilaJson.setNonce(new BigInteger(bNum).abs().toString());
                        } while (CryptoUtil.generatehash(pilaJson).compareTo(difficult) > 0);

                        if (difficultService.getFinalValidity().compareTo(new Date()) > 0 || true) {
                            Thread.sleep(500);

                            String pilaStr = mapper.writeValueAsString(pilaJson);

                            System.out.println("\n\n[MINED PILA]: " + pilaStr);
                            webSocketService.send(
                                    "MINED PILA - nonce: " + StrUtil.limitCharsAddEllipsis(pilaJson.getNonce(), 10),
                                    "/topic/pilacoin",
                                    TypeActionWsJson.TypeAction.MINER_PILACOIN);

                            rabbitTemplate.convertAndSend(pilaMineradoQueue, pilaStr);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("\n\n[MINED PILA]: " + e);
                    webSocketService.send("ERRO AO MINERAR PILA", "/topic/pilacoin",
                            TypeActionWsJson.TypeAction.MINER_PILACOIN);

                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
