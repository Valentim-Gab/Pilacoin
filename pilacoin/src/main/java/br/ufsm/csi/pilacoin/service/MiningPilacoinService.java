package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.json.DifficultJson;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.json.TypeActionWsJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

// @Service
public class MiningPilacoinService {
    private DifficultService difficultService;
    private CryptoUtil cryptoUtil;
    public RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate template;

    @Value("${queue.pilacoin.mined}")
    private String pilaMineradoQueue;

    public MiningPilacoinService(DifficultService difficultService, CryptoUtil cryptoUtil,
            RabbitTemplate rabbitTemplate, SimpMessagingTemplate template) {
        this.difficultService = difficultService;
        this.cryptoUtil = cryptoUtil;
        this.rabbitTemplate = rabbitTemplate;
        this.template = template;
    }

    @PostConstruct
    public void minerar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(500);

                        if (difficultService.difficultJson == null) {
                            continue;
                        }

                        System.out.println("\n\n[MINING PILACOIN]");

                        ObjectMapper mapper = new ObjectMapper();
                        TypeActionWsJson typeActionWsJson = TypeActionWsJson.builder()
                                .message("MINING PILACOIN")
                                .type(TypeActionWsJson.TypeAction.MINER_PILACOIN)
                                .timestamp(System.currentTimeMillis())
                                .build();

                        template.convertAndSend("/topic/pilacoin",
                                mapper.writeValueAsString(typeActionWsJson));

                        DifficultJson difficultJson = difficultService.difficultJson;
                        BigInteger dificuldade = new BigInteger(difficultJson.getDificuldade(),
                                16).abs();

                        PilaCoinJson pilaJson = PilaCoinJson.builder()
                                .nomeCriador("Gabriel_Valentim")
                                .dataCriacao(new Date())
                                .chaveCriador(cryptoUtil.generateKeys().getPublic().getEncoded()).build();

                        byte[] bNum = new byte[256 / 8];
                        Random random = new Random(System.currentTimeMillis());

                        do {
                            random.nextBytes(bNum);
                            pilaJson.setNonce(new BigInteger(bNum).abs().toString());
                        } while (cryptoUtil.generatehash(pilaJson).compareTo(dificuldade) > 0);

                        if (difficultJson.getValidadeFinal().compareTo(new Date()) > 0 || true) {
                            String pilaStr = mapper.writeValueAsString(pilaJson);
                            String formattedNonce =  pilaJson.getNonce().
                                    substring(0, Math.min(pilaJson.getNonce().length(), 10)) + "...";

                            typeActionWsJson.setMessage("MINED PILA - nonce: " + formattedNonce);
                            typeActionWsJson.setTimestamp(System.currentTimeMillis());
                            template.convertAndSend("/topic/pilacoin",
                                    mapper.writeValueAsString(typeActionWsJson));

                            System.out.println("\n\n[MINED PILA]: " + pilaStr);

                            //rabbitTemplate.convertAndSend(pilaMineradoQueue, pilaStr);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
